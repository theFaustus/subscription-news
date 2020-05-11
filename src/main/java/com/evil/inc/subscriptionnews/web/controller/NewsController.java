package com.evil.inc.subscriptionnews.web.controller;

import com.evil.inc.subscriptionnews.domain.Client;
import com.evil.inc.subscriptionnews.service.contracts.NewsNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {
    private final NewsNotificationService newsNotificationService;

    @PostMapping(value = "/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody String email) {
        newsNotificationService.addSubscriber(new Client(email));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/send/{date}")
    public ResponseEntity<?> send(@PathVariable String date) {
        newsNotificationService.sendNewsFor(LocalDate.parse(date));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
