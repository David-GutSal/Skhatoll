package com.skhatoll.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    String nombre,
    
    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 4, message = "La contrasena debe tener al menos 4 caracteres")
    String password
) {}