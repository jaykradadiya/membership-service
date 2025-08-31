package com.membership.program.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {

    @NotNull
    @NotEmpty
    String userName;

    @NotNull
    @NotEmpty
    String passWord;
}
