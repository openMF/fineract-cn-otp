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
package com.mx.infotec.fineract.otp.service.internal.mapper;

import com.mx.infotec.fineract.otp.api.v1.domain.AppUser;
import com.mx.infotec.fineract.otp.service.internal.repository.AppUserEntity;


public class AppUserMapper {

  private AppUserMapper() {
    super();
  }

  public static AppUser map(final AppUserEntity AppUserJpaEntity) {
    final AppUser AppUser = new AppUser();
    AppUser.setId(AppUserJpaEntity.getId());
    AppUser.setUsername(AppUserJpaEntity.getUsername());
    AppUser.setPasswordHash(AppUserJpaEntity.getPasswordHash());
    AppUser.setSecret(AppUserJpaEntity.getSecret());
    AppUser.setEnabled(AppUserJpaEntity.getEnabled());
    AppUser.setLoginAttempt(AppUserJpaEntity.getLoginAttempt());
    AppUser.setNonLocked(AppUserJpaEntity.getNonlocked());
    return AppUser;
  }

  public static AppUserEntity map(final AppUser AppUser) {
    final AppUserEntity AppUserJpaEntity = new AppUserEntity();
    AppUserJpaEntity.setId(AppUser.getId());
    AppUserJpaEntity.setUsername(AppUser.getUsername());
    AppUserJpaEntity.setPasswordHash(AppUser.getPasswordHash());
    AppUserJpaEntity.setSecret(AppUser.getSecret());
    AppUserJpaEntity.setEnabled(AppUser.getEnabled());
    AppUserJpaEntity.setLoginAttempt(AppUser.getLoginAttempt());
    AppUserJpaEntity.setNonlocked(AppUser.getNonLocked());
    return AppUserJpaEntity;
  }
}
