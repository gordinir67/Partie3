package com.Chatop.API.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequest {
    @NotNull
    private Long rental_id;

    @NotNull
    private Long user_id;

    @NotBlank
    private String message;
}
