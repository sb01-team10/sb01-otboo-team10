package com.codeit.weatherwear.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotNull(message = "비밀번호는 null이어서는 안 됩니다.")
    @Size(min = 6, max = 15, message = "비밀번호는 6-15자 사이여야 합니다.")
    String password
) {

}
