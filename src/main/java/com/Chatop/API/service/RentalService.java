package com.Chatop.API.service;

import com.Chatop.API.model.Rental;
import com.Chatop.API.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;

    public Iterable<Rental> getRentals() {
        return rentalRepository.findAll();
    }

    public Optional<Rental> getRental(Long id) {
        return rentalRepository.findById(id);
    }

    public Rental save(Rental rental) {
        return rentalRepository.save(rental);
    }
}
