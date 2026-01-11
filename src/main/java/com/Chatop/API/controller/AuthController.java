package com.Chatop.API.controller;

import com.Chatop.API.dto.auth.AuthResponse;
import com.Chatop.API.dto.auth.LoginRequest;
import com.Chatop.API.dto.auth.RegisterRequest;
import com.Chatop.API.dto.user.UserResponse;
import com.Chatop.API.model.User;
import com.Chatop.API.repository.UserRepository;
import com.Chatop.API.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur gérant l'authentification et l'inscription des utilisateurs.
 * Fournit les endpoints de création de compte, de connexion et de récupération
 * des informations de l'utilisateur connecté.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    /**
     * Permet de créer un nouveau compte utilisateur.
     *
     * @param request Objet contenant les informations d'inscription (nom, email, mot de passe)
     * @return AuthResponse contenant le token JWT de l'utilisateur nouvellement créé
     */
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Permet à un utilisateur existant de se connecter à l'application.
     *
     * @param request Objet contenant l'email et le mot de passe de l'utilisateur
     * @return AuthResponse contenant le token JWT de l'utilisateur connecté
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Récupère les informations du profil de l'utilisateur actuellement authentifié.
     *
     * @param authentication Objet Spring Security contenant les informations de l'utilisateur connecté
     * @return UserResponse contenant les informations du profil utilisateur
     */
    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        String email = authentication.getName();
        User u = userRepository.findByEmail(email).orElseThrow();
        return UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }
}
