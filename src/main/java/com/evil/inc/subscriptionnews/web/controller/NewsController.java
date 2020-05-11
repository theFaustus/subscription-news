package com.evil.inc.subscriptionnews.web.controller;

import com.evil.inc.subscriptionnews.domain.Client;
import com.evil.inc.subscriptionnews.domain.News;
import com.evil.inc.subscriptionnews.service.contracts.NewsGenerationService;
import com.evil.inc.subscriptionnews.service.contracts.NewsNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {
    private final NewsNotificationService newsNotificationService;
    private final NewsGenerationService newsGenerationService;

    @PostMapping(value = "/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody String email) {
        Client client = new Client(email);
        newsNotificationService.addSubscriber(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(client.getId());
    }

    @DeleteMapping(value = "/unsubscribe/{id}")
    public ResponseEntity<?> unsubscribe(@PathVariable String id) {
        newsNotificationService.removeSubscriber(new Client(id, ""));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/send/{date}")
    public ResponseEntity<?> send(@PathVariable String date) {
        newsNotificationService.sendNewsFor(LocalDate.parse(date));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{date}")
    public ResponseEntity<List<News>> getNews(@PathVariable String date){
        return ResponseEntity.ok(newsGenerationService.generateHeadersOnlyNewsFor(LocalDate.parse(date)));
    }
}
