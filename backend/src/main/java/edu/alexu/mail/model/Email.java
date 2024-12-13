package edu.alexu.mail.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Table(name="emails")
@EntityListeners(AuditingEntityListener.class)
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String sender;

    private String receiver;

    private String topic;

    private String body;

    private int priority;

    private String pathname;

    @CreatedDate
    private LocalDateTime creationDateTime;

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getTopic() {
        return topic;
    }

    public String getBody() {
        return body;
    }

    public int getPriority() {
        return priority;
    }

    public String getPathname() {
        return pathname;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }
}
