package com.konstan.cvbuilderapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RegisterRequest {

    @Email(message = "El email debe ser valido.")
    @Email(message = "Email es obligatorio")
    private String email;
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 2, max = 15, message = "El nombre debe tener entre 2 y 15 símbolos.")
    private String name;
    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 6, max = 15,message = "La contraseña debe tener entre 6 y 15 símbolos.")
    private String password;
    private  String profileImageUrl;

}
