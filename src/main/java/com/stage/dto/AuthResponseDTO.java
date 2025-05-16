package com.stage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDTO {
    public String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }
}
