package com.Chatop.API.controller;

import com.Chatop.API.dto.common.SimpleMessageResponse;
import com.Chatop.API.dto.message.MessageRequest;
import com.Chatop.API.model.Message;
import com.Chatop.API.model.Rental;
import com.Chatop.API.model.User;
import com.Chatop.API.repository.RentalRepository;
import com.Chatop.API.repository.UserRepository;
import com.Chatop.API.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Contrôleur gérant l'envoi de messages entre utilisateurs concernant une location.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    /**
     * Permet à un utilisateur authentifié d'envoyer un message à propos d'une location.
     *
     * @param request Données du message (contenu, identifiant de la location, utilisateur)
     * @param authentication Informations de l'utilisateur connecté
     * @return SimpleMessageResponse indiquant que le message a bien été envoyé
     */
    @PostMapping("/messages")
    public SimpleMessageResponse create(@Valid @RequestBody MessageRequest request, Authentication authentication) {

        // Récupération de l'utilisateur actuellement connecté
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "error"));

        // Vérifie que l'utilisateur ne tente pas d'envoyer un message au nom d'un autre utilisateur
        if (request.getUser_id() != null && !request.getUser_id().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "error");
        }

        // Vérifie que la location ciblée existe
        Rental r = rentalRepository.findById(request.getRental_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found"));

        // Création du message
        Message m = new Message();
        m.setUser(currentUser);
        m.setRental(r);
        m.setMessage(request.getMessage());

        // Sauvegarde du message en base
        messageService.save(m);

        return new SimpleMessageResponse("Message send with success");
    }
}
