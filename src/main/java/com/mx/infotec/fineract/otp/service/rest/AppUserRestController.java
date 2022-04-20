/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.mx.infotec.fineract.otp.service.rest;

import com.mx.infotec.fineract.otp.api.v1.domain.AppUser;
import com.mx.infotec.fineract.otp.api.v1.domain.AuthenticationFlow;
import com.mx.infotec.fineract.otp.api.v1.domain.SignupResponse;
import com.mx.infotec.fineract.otp.api.v1.exeption.ServiceException;
import com.mx.infotec.fineract.otp.service.internal.config.CustomTotp;
import com.mx.infotec.fineract.otp.service.internal.service.AppUserService;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

// import com.codahale.passpol.PasswordPolicy;
// import com.codahale.passpol.Status;

@EnableAutoConfiguration
@SuppressWarnings("unused")
@RestController
@RequestMapping(path = "/totp")
public class AppUserRestController {
 private final AppUserService appUserService;
  private final static Logger logger = LoggerFactory.getLogger(AppUserRestController.class);
  private final PasswordEncoder passwordEncoder;
  private final String userNotFoundEncodedPassword;
  private final static String USER_AUTHENTICATION_OBJECT = "USER_AUTHENTICATION_OBJECT";

  @Autowired
  public AppUserRestController( AppUserService appUserService,
  final PasswordEncoder passwordEncoder) {
    this.appUserService = appUserService;
    this.passwordEncoder = passwordEncoder;
    this.userNotFoundEncodedPassword = this.passwordEncoder.encode("userNotFoundPassword");
  }

  @GetMapping(path="/status", produces = "application/json")
  public @ResponseBody ResponseEntity<Void> status() {
    return ResponseEntity.noContent().build();
  }

  @PostMapping(path = "/signup", consumes = "application/json", produces = "application/json")
  public SignupResponse signup(@RequestBody @Valid final AppUser appuser) throws InterruptedException {
    final boolean userExists = this.appUserService.existsByUserName(appuser.getUsername());
    if (userExists && !appuser.getRenew() && !appuser.getRenewPassword()) 
      return new SignupResponse(SignupResponse.Status.USERNAME_TAKEN);     

    if(!appuser.getRenew() && !appuser.getRenewPassword()){
      appuser.setSecret(Base32.random());
      appuser.setPasswordHash(this.passwordEncoder.encode(appuser.getPassword()));
      appuser.setEnabled(false);
     
      this.appUserService.StoreAppUser(appuser);
      return new SignupResponse(SignupResponse.Status.OK, appuser.getUsername(), appuser.getSecret());
    }
    else{
        this.throwIfUserNotExists(appuser.getUsername());
        this.throwIfUserNotEnable(appuser.getUsername());
        if(!isNullOrEmpty(appuser.getPassword())){
          if(appuser.getRenewPassword()){
            if(isNullOrEmpty(appuser.getNewPassword()))
              throw ServiceException.notFound("Password renew data provided are incorrect");
            else
              appuser.setPasswordHash(this.passwordEncoder.encode(appuser.getNewPassword()));
          }
          if(appuser.getRenew())
            appuser.setSecret(Base32.random());
          final AppUser user = this.findUserByUserName(appuser.getUsername());
          boolean pwMatches = this.passwordEncoder.matches(appuser.getPassword(), user.getPasswordHash());
          if (pwMatches && user.getEnabled().booleanValue()) {
            appuser.setEnabled(false);
            this.appUserService.UpdateAppUser(appuser);
            return new SignupResponse(SignupResponse.Status.OK, appuser.getUsername(), appuser.getSecret());
          }            
        }
        throw ServiceException.notFound("User or password are incorects"); 
    }  
  }

  @PostMapping(path = "/signup-confirm-secret", consumes = "application/json", produces = "application/json")
  public boolean signupConfirmSecret(@RequestBody @Valid final AppUser appuser) {
      this.throwIfUserNotExists(appuser.getUsername());      
      if(!isNullOrEmpty(appuser.getCode())){
        final AppUser user = this.findUserByUserName(appuser.getUsername());
        final String secret = user.getSecret();
        final Totp totp = new Totp(secret);
        if (totp.verify(appuser.getCode())) {
          user.setEnabled(true);
          this.appUserService.UpdateAppUser(user);
          return true;
        }
      }
      else {
        throw ServiceException.notFound("No given TOTP code for {0}.", appuser.getUsername());
      }  

    return false;
  }

  @PostMapping(path = "/verify-totp", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Object> verifyTOTP(@RequestBody @Valid final AppUser appuser) {
    if ( isNullOrEmpty(appuser.getUsername()) || isNullOrEmpty(appuser.getCode())) {
        return ResponseEntity.ok().body(AuthenticationFlow.NOT_AUTHENTICATED);
    }
    this.throwIfUserNotExists(appuser.getUsername());      
    this.throwIfUserNotEnable(appuser.getUsername());

    final AppUser user = this.findUserByUserName(appuser.getUsername());
    if(!user.getNonLocked())
        return ResponseEntity.ok().body(AuthenticationFlow.LOCKED);
    
    if(!isNullOrEmpty(user.getSecret())){
        AuthenticationFlow result = validateTOTP(user.getSecret(),appuser.getCode());            
        if(result ==  AuthenticationFlow.NOT_AUTHENTICATED){
          user.increseLoginAttemp();
          if(user.getLoginAttempt() >= 5)
            user.block();
        }
        else
          user.resetLoginAttemp();
        if(!appuser.getCode().equals("000000"))
          this.appUserService.UpdateLoginAttemp(user);
        return ResponseEntity.ok().body(result);            
    }
    
    return ResponseEntity.ok().body(AuthenticationFlow.NOT_AUTHENTICATED);
  }

  @PostMapping(path = "/remove", consumes = "application/json", produces = "application/json")
  public boolean remove(@RequestBody @Valid final AppUser appuser) {
    if (isNullOrEmpty(appuser.getUsername()))
      return false;
    final boolean userExists = this.appUserService.existsByUserName(appuser.getUsername());
    if(userExists){
      final AppUser user = this.findUserByUserName(appuser.getUsername());
      this.appUserService.removeUser(appuser);
    }
    return false;
  }

  @PostMapping(path = "/unblock", consumes = "application/json", produces = "application/json")
  public boolean unblock(@RequestBody @Valid final AppUser appuser) {
    if (isNullOrEmpty(appuser.getUsername()))
      return false;
    this.throwIfUserNotExists(appuser.getUsername());      
    this.throwIfUserNotEnable(appuser.getUsername());

    final AppUser user = this.findUserByUserName(appuser.getUsername());
    if(!user.getNonLocked()){
      user.unblock();
      this.appUserService.UpdateLoginAttemp(user);
      return true;
    }
    
    return false;

  }

  private AuthenticationFlow validateTOTP(String secret,String token){
    if (!secret.isEmpty() && !token.isEmpty()) {
      CustomTotp totp = new CustomTotp(secret);
      if (totp.verify(token, 1, 1).isValid()) return AuthenticationFlow.AUTHENTICATED;
    }
    return AuthenticationFlow.NOT_AUTHENTICATED;
  }

  private boolean isNullOrEmpty(String str) {
    if(str != null && !str.isEmpty())
        return false;
    return true;
  }

  private void throwIfUserNotExists(final String username) {
    if(!isNullOrEmpty(username)){
      if (!this.appUserService.existsByUserName(username)) {
        throw ServiceException.notFound("User {0} not found.", username);
      }
    }
    else
      throw ServiceException.notFound("Username was not provided", username);    
  } 

  private void throwIfUserNotEnable(final String username) {
    if(!isNullOrEmpty(username)){
      if (!this.appUserService.existsByUsernameAndEnable(username)) {
        throw ServiceException.notFound("User {0} is not enable.", username);
      }
    }
    else
      throw ServiceException.notFound("Username was not provided", username);
    
  } 

  private AppUser findUserByUserName(final String username){
    final Optional<AppUser> appUserRecord =  this.appUserService.findByUserName(username); 
    if (appUserRecord.isPresent())
      return appUserRecord.get();
    else
      throw ServiceException.notFound("UserName {0} not found.", username);
  }
}
