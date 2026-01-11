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

/**
 * Contrôleur gérant les endpoints liés aux locations (rentals) :
 * - consultation de la liste des locations
 * - consultation du détail d'une location
 * - création et mise à jour d'une location (avec upload d'image)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    /**
     * Récupère la liste de toutes les locations disponibles.
     *
     * @return RentalsResponse contenant la liste des locations au format DTO
     */
    @GetMapping("/rentals")
    public RentalsResponse list() {
        List<RentalResponse> rentals = StreamSupport.stream(rentalService.getRentals().spliterator(), false)
                .map(this::toDto)
                .toList();
        return new RentalsResponse(rentals);
    }

    /**
     * Récupère le détail d'une location à partir de son identifiant.
     *
     * @param id Identifiant de la location
     * @return RentalResponse correspondant à la location demandée
     * @throws ResponseStatusException 404 si la location n'existe pas
     */
    @GetMapping("/rentals/{id}")
    public RentalResponse get(@PathVariable Long id) {
        Rental r = rentalService.getRental(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found"));
        return toDto(r);
    }

    /**
     * Crée une nouvelle location avec une image (upload multipart/form-data).
     *
     * @param name Nom de la location
     * @param surface Surface de la location (m²)
     * @param price Prix de la location
     * @param description Description de la location
     * @param picture Image associée à la location
     * @param authentication Informations de l'utilisateur authentifié (propriétaire de la location)
     * @return SimpleMessageResponse indiquant que la location a été créée
     * @throws ResponseStatusException 401 si l'utilisateur n'est pas authentifié
     * @throws ResponseStatusException 400 si le fichier ne peut pas être stocké
     */
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

    /**
     * Met à jour une location existante (avec possibilité de remplacer l'image).
     * Seul le propriétaire de la location est autorisé à la modifier.
     *
     * @param id Identifiant de la location à modifier
     * @param name Nouveau nom de la location
     * @param surface Nouvelle surface (m²)
     * @param price Nouveau prix
     * @param description Nouvelle description
     * @param picture Nouvelle image (optionnelle). Si absente, l'image existante est conservée.
     * @param authentication Informations de l'utilisateur authentifié
     * @return SimpleMessageResponse indiquant que la location a été mise à jour
     * @throws ResponseStatusException 404 si la location n'existe pas
     * @throws ResponseStatusException 401 si l'utilisateur n'est pas authentifié
     * @throws ResponseStatusException 403 si l'utilisateur n'est pas le propriétaire
     * @throws ResponseStatusException 400 si le fichier ne peut pas être stocké
     */
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

        // Vérifie que seul le propriétaire peut mettre à jour la location
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
