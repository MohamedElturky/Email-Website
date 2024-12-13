package edu.alexu.mail.repository;

import edu.alexu.mail.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Integer> {
    List<Email> getEmailById(int id);
}
