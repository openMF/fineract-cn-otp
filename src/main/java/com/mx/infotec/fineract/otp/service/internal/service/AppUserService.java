package com.mx.infotec.fineract.otp.service.internal.service;

import java.util.Optional;

import com.mx.infotec.fineract.otp.api.v1.domain.AppUser;

import org.springframework.stereotype.Component;

@Component
public interface AppUserService {
    Optional<AppUser> findByUserName(final String username);
    Boolean existsByUserName(final String username);
    Boolean existsByUsernameAndEnable(final String username);
    Boolean removeUser(final AppUser AppUserCommand);
    String StoreAppUser(final AppUser AppUserCommand);
    String UpdateAppUser(final AppUser AppUserCommand);
    public void UpdateLoginAttemp(final AppUser AppUserCommand);
}