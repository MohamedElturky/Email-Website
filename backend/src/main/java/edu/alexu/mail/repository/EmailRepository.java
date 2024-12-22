package edu.alexu.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.alexu.mail.model.Email;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Integer> {
    List<Email> findAllBySenderId(int senderId);

    @Query(value = "SELECT * FROM emails WHERE receivers_email_addresses LIKE CONCAT('%', :receiverEmailAddress, '%')", nativeQuery = true)
    List<Email> findAllByReceiverEmailAddress(String receiverEmailAddress);
}
