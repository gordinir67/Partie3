package com.Chatop.API.dto.rental;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RentalResponse {
    private Long id;
    private String name;
    private Integer surface;
    private Integer price;
    private String picture;
    private String description;
    private Long ownerId;


    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;
}
