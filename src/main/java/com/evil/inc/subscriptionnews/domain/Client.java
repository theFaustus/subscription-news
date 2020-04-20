package com.evil.inc.subscriptionnews.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class Client {
    private String id = UUID.randomUUID().toString();

    public void receive(News news){
        System.out.println(news);
    }
}
