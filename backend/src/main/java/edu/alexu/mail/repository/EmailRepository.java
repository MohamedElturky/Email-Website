package edu.alexu.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.alexu.mail.model.Email;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Integer> {
    List<Email> findAllBySenderId(int senderId);
}
