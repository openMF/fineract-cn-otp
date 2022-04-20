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
package com.mx.infotec.fineract.otp.api.v1.domain;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@SuppressWarnings({"WeakerAccess", "unused"})
public class AppUser {

  private Long id;

  @NotNull(message = "UserName may not be blank")
  @Length(max = 50)
  private String username;

  @Length(max = 100)
  private String password;

  @Length(min = 0,max = 100)
  private String newPassword;

  private String passwordHash;

  @Length(max = 50)
  private String secret;

  @Length(min = 6,max = 6)
  private String code;

  private Boolean enabled;
  
  private Boolean renew;

  private Boolean renewPassword;

  private Boolean removeUser;

  private Boolean nonLocked;
  
  private int loginAttempt;

  public AppUser() {
    super();
  }

  public AppUser(String username, String password,Boolean enabled, String secret, int loginAttempt) {
    this.username = username;
    this.password = password;
    this.secret = secret;
    this.enabled = enabled;
    this.renew = false;
    this.loginAttempt = loginAttempt;
  }

  public static AppUser create( final String username, final String password,final String secret, final int loginAttempt){
      return new AppUser(username,password,true,secret,loginAttempt);
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasswordHash() {
    return this.passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getSecret() {
    return this.secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public Boolean isEnabled() {
    return this.enabled;
  }

  public Boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Boolean getNonLocked() {
    return this.nonLocked;
  }

  public void setNonLocked(Boolean nonLocked) {
    this.nonLocked = nonLocked;
  }

  public void block(){ 
    this.nonLocked = false;
  }

  public void unblock(){ 
    this.nonLocked = true;
    this.loginAttempt = 0;
  }

  public int getLoginAttempt()
  { 
    return this.loginAttempt; 
  } 

  public void setLoginAttempt(int loginAttempt) {
    this.loginAttempt = loginAttempt; 
  }

  public void increseLoginAttemp() 
  { 
    this.loginAttempt++; 
  }

  public void resetLoginAttemp() 
  { 
    this.loginAttempt = 0; 
  }


  public Boolean getRenew() {
    if(this.renew == null) 
      return false;
    return this.renew;
  }

  public void setRenew(Boolean renew) {
    this.renew = renew;
  }

  public Boolean getRenewPassword() {
    if(this.renewPassword == null) 
      return false;
    return this.renewPassword;
  }

  public void setRenewPassword(Boolean renewPassword) {
    this.renewPassword = renewPassword;
  }

  public String getNewPassword() {
    return this.newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public Boolean getRemoveUser() {
    if(this.removeUser == null) 
      return false;
    return this.removeUser;
  }

  public void setRemoveUser(Boolean removeUser) {
    this.removeUser = removeUser;
  }
}