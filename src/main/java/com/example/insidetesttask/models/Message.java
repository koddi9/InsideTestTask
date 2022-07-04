package com.example.insidetesttask.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String value;
    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    public Message(String value, User user) {
        this.value = value;
        this.user = user;
    }
}
