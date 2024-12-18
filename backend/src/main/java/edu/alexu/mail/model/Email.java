package edu.alexu.mail.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name="emails")
@EntityListeners(AuditingEntityListener.class)
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonProperty("senderId")
    private int senderId;

    @Column(columnDefinition = "BLOB")
    private List<String> receiversEmailAddresses;

    @Column(columnDefinition = "TEXT")
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String body;

    private int priority;

    @CreatedDate
    private LocalDateTime creationDateTime;

    public int getId() {
        return id;
    }

    public int getSenderId() {
        return senderId;
    }

    public List<String> getReceiversEmailAddresses() {
        return receiversEmailAddresses;
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

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setReceiversEmailAddresses(List<String> receiversEmailAddresses) {
        this.receiversEmailAddresses = receiversEmailAddresses;
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
}
