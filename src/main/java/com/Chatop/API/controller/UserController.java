package com.Chatop.API.controller;

import com.Chatop.API.dto.user.UserResponse;
import com.Chatop.API.model.User;
import com.Chatop.API.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Contrôleur gérant les opérations liées aux utilisateurs.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Récupère les informations d’un utilisateur à partir de son identifiant.
     *
     * @param id Identifiant de l’utilisateur
     * @return UserResponse contenant les informations du profil utilisateur
     * @throws ResponseStatusException 404 si l’utilisateur n’existe pas
     */
    @GetMapping("/user/{id}")
    public UserResponse getUser(@PathVariable("id") Long id) {
        User u = userService.getUser(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }
}
