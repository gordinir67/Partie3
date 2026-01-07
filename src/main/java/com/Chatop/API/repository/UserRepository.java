package com.Chatop.API.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.Chatop.API.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

}

