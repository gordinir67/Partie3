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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    @PostMapping("/messages")
    public SimpleMessageResponse create(@Valid @RequestBody MessageRequest request, Authentication authentication) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "error"));

        if (request.getUser_id() != null && !request.getUser_id().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "error");
        }

        Rental r = rentalRepository.findById(request.getRental_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found"));

        Message m = new Message();
        m.setUser(currentUser);
        m.setRental(r);
        m.setMessage(request.getMessage());

        messageService.save(m);
        return new SimpleMessageResponse("Message send with success");
    }
}
