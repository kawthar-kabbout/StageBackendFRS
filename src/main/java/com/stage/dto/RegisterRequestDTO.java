package com.stage.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    public String username;
    public String password;
    public String email;
}
