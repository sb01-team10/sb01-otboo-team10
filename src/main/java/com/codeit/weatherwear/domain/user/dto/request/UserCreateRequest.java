package com.codeit.weatherwear.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotNull(message = "이름은 null이어서는 안 됩니다.")
    @Size(min = 1, max = 20, message = "이름은 1-20자 사이여야 합니다.")
    String name,

    @NotNull(message = "이메일은 null이어서는 안 됩니다.")
    @Email
    String email,

    @NotNull(message = "비밀번호는 null이어서는 안 됩니다.")
    @Size(min = 6, max = 15, message = "비밀번호는 6-15자 사이여야 합니다.")
    String password
) {

}
