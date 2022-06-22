package com.easemob.agora.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AppUser {
    @NotEmpty(message = "userAccount cannot be empty")
    private String userAccount;

    @NotEmpty(message = "userPassword cannot be empty")
    private String userPassword;
}
