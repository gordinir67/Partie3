package com.Chatop.API.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.Chatop.API.model.Rental;

@Repository
public interface RentalRepository extends CrudRepository<Rental, Long> {

}