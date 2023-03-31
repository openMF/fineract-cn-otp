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
package com.mx.infotec.fineract.otp.service.internal.repository;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import javax.persistence.*;

@SuppressWarnings("unused")
@Entity
@Table(name = "otp_app_user")
@Getter
@Setter
public class AppUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "username")
  private String username;

  @Column(name = "password_hash")
  private String passwordHash;

  @Column(name = "secret")
  private String secret;

  @Column(name = "enabled")
  private Boolean enabled;

  @Column(name = "nonlocked")
  private Boolean nonlocked;

  @Column(name = "login_attempt")
  private int loginAttempt;

  public AppUserEntity() {
    super();
  }
}
