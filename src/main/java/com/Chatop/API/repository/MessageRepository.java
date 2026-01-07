package com.Chatop.API.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.Chatop.API.model.Message;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {

}