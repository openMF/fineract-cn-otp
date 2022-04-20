package com.mx.infotec.fineract.otp.service.internal.config;

import java.util.Collection;

import com.mx.infotec.fineract.otp.api.v1.domain.AppUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AppUserAuthentication implements Authentication {

  private static final long serialVersionUID = 1L;
  private final AppUser userDetail;

  public AppUserAuthentication(AppUser userDetail) {
    this.userDetail = userDetail;
  }

  
  public String getName() {
    return this.userDetail.getUsername();
  }

  
  public Object getCredentials() {
    return null;
  }

  
  public Object getDetails() {
    return null;
  }

  
  public Object getPrincipal() {
    return this.userDetail;
  }

  
  public boolean isAuthenticated() {
    return true;
  }

  
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    throw new UnsupportedOperationException(
        "this authentication object is always authenticated");
  }

  
  public Collection<? extends GrantedAuthority> getAuthorities() {
      // TODO Auto-generated method stub
      return null;
  }

}
