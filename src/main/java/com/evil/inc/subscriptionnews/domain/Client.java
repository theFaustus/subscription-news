package com.evil.inc.subscriptionnews.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Client {
    private String id = UUID.randomUUID().toString();
    private String email;

    public Client(String email) {
        this.email = email;
    }

    public void receive(List<News> news) {
        System.out.println(this.toString());
        news.forEach(System.out::println);
    }

    void setId(String id) {
        this.id = id;
    }
}
