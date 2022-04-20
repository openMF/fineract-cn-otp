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
package com.mx.infotec.fineract.otp.service.internal.service;

import java.util.Optional;

import javax.transaction.Transactional;

import com.mx.infotec.fineract.otp.api.v1.domain.AppUser;
import com.mx.infotec.fineract.otp.api.v1.exeption.ServiceException;
import com.mx.infotec.fineract.otp.service.internal.mapper.AppUserMapper;
import com.mx.infotec.fineract.otp.service.internal.repository.AppUserEntity;
import com.mx.infotec.fineract.otp.service.internal.repository.AppUserJpaEntityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppUserServiceImp implements AppUserService {

  private final AppUserJpaEntityRepository AppUserJpaEntityRepository;

  @Autowired
  public AppUserServiceImp(final AppUserJpaEntityRepository AppUserJpaEntityRepository) {
    super();
    this.AppUserJpaEntityRepository = AppUserJpaEntityRepository;
  }

  public Optional<AppUser> findByUserName(final String username) {
    return this.AppUserJpaEntityRepository.findByUsername(username).map(AppUserMapper::map);
  }

  public Boolean existsByUserName(final String username) {
     return this.AppUserJpaEntityRepository.existsByUsername(username);
  }

  public Boolean existsByUsernameAndEnable(final String username) {
    return this.AppUserJpaEntityRepository.existsByUsernameAndEnable(username);
 }

 @Transactional
  public String StoreAppUser(final AppUser AppUserCommand) {

    final AppUserEntity entity = new AppUserEntity();
    entity.setId(AppUserCommand.getId());
    entity.setUsername(AppUserCommand.getUsername());
    entity.setPasswordHash(AppUserCommand.getPasswordHash());
    entity.setSecret(AppUserCommand.getSecret());
    entity.setEnabled(AppUserCommand.getEnabled());
    entity.setNonLocked(true);
    entity.setLoginAttempt(0);
    this.AppUserJpaEntityRepository.save(entity);

    return AppUserCommand.getUsername();
  }

  @Transactional
  public String UpdateAppUser(final AppUser AppUserCommand) {

    final AppUserEntity entity = this.findCountryEntityOrThrow(AppUserCommand.getUsername());
    if(AppUserCommand.getRenew())
      entity.setSecret(AppUserCommand.getSecret());
    if(AppUserCommand.getRenewPassword())
      entity.setPasswordHash(AppUserCommand.getPasswordHash());
    entity.setEnabled(AppUserCommand.getEnabled());

    this.AppUserJpaEntityRepository.save(entity);
    return AppUserCommand.getUsername();
  }

  @Transactional
  public void UpdateLoginAttemp(final AppUser AppUserCommand) {

      final AppUserEntity entity = this.findCountryEntityOrThrow(AppUserCommand.getUsername());
      entity.setLoginAttempt(AppUserCommand.getLoginAttempt());
      entity.setNonLocked(AppUserCommand.getNonLocked());
      this.AppUserJpaEntityRepository.save(entity);
  }

  private AppUserEntity findCountryEntityOrThrow(String username) {
    return this.AppUserJpaEntityRepository.findByUsername(username)
        .orElseThrow(() -> ServiceException.notFound("UserName ''{0}'' not found", username));

  }

  @Override
  public Boolean removeUser(AppUser appUser) {
    final AppUserEntity entity = this.findCountryEntityOrThrow(appUser.getUsername());
    this.AppUserJpaEntityRepository.delete(entity);
    return true;
  }
}
