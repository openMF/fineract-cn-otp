package com.mx.infotec.fineract.otp.api.v1.exeption;

import lombok.Getter;

@SuppressWarnings("WeakerAccess")
@Getter
public final class ServiceError {

  private final int code;
  private final String message;

  private ServiceError(final int code, final String message) {
    super();
    this.code = code;
    this.message = message;
  }

  public static Builder create(final int code) {
    return new Builder(code);
  }

  public static final class Builder {

    private final int code;
    private String message;

    public Builder(final int code) {
      super();
      this.code = code;
    }

    public Builder message(final String message) {
      this.message = message;
      return this;
    }

    public ServiceError build() {
      return new ServiceError(this.code, this.message);
    }
  }

  @Override
  public String toString() {
    return "ServiceError{" +
            "code=" + code +
            ", message='" + message + '\'' +
            '}';
  }
}
