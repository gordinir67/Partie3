package com.Chatop.API.controller;

import com.Chatop.API.dto.common.SimpleMessageResponse;
import com.Chatop.API.dto.rental.RentalResponse;
import com.Chatop.API.dto.rental.RentalsResponse;
import com.Chatop.API.model.Rental;
import com.Chatop.API.model.User;
import com.Chatop.API.repository.UserRepository;
import com.Chatop.API.service.FileStorageService;
import com.Chatop.API.service.RentalService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @GetMapping("/rentals")
    public RentalsResponse list() {
        List<RentalResponse> rentals = StreamSupport.stream(rentalService.getRentals().spliterator(), false)
                .map(this::toDto)
                .toList();
        return new RentalsResponse(rentals);
    }

    @GetMapping("/rentals/{id}")
    public RentalResponse get(@PathVariable Long id) {
        Rental r = rentalService.getRental(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found"));
        return toDto(r);
    }

    @PostMapping(value = "/rentals", consumes = {"multipart/form-data"})
    public SimpleMessageResponse create(
            @RequestParam("name") @NotBlank String name,
            @RequestParam("surface") @NotNull Integer surface,
            @RequestParam("price") @NotNull Integer price,
            @RequestParam("description") @NotBlank String description,
            @RequestPart("picture") MultipartFile picture,
            Authentication authentication
    ) {
        try {
            String email = authentication.getName();
            User owner = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

            String pictureUrl = fileStorageService.store(picture);

            Rental r = new Rental();
            r.setName(name);
            r.setSurface(surface);
            r.setPrice(price);
            r.setDescription(description);
            r.setPicture(pictureUrl);
            r.setOwner(owner);

            rentalService.save(r);
            return new SimpleMessageResponse("Rental created !");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not store file");
        }
    }

    @PutMapping(value = "/rentals/{id}", consumes = {"multipart/form-data"})
    public SimpleMessageResponse update(
            @PathVariable Long id,
            @RequestParam("name") @NotBlank String name,
            @RequestParam("surface") @NotNull Integer surface,
            @RequestParam("price") @NotNull Integer price,
            @RequestParam("description") @NotBlank String description,
            @RequestPart(value = "picture", required = false) MultipartFile picture,
            Authentication authentication
    ) {
        Rental r = rentalService.getRental(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found"));

        // Optional: ensure only owner can update
        String email = authentication.getName();
        User current = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        if (!r.getOwner().getId().equals(current.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        r.setName(name);
        r.setSurface(surface);
        r.setPrice(price);
        r.setDescription(description);

        if (picture != null && !picture.isEmpty()) {
            try {
                r.setPicture(fileStorageService.store(picture));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not store file");
            }
        }

        rentalService.save(r);
        return new SimpleMessageResponse("Rental updated !");
    }

    private RentalResponse toDto(Rental r) {
        return RentalResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .surface(r.getSurface())
                .price(r.getPrice())
                .picture(r.getPicture())
                .description(r.getDescription())
                .ownerId(r.getOwner() != null ? r.getOwner().getId() : null)
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
