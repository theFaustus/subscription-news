package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.Client;
import com.evil.inc.subscriptionnews.domain.News;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NewsNotificationServiceImpl implements NewsNotificationService {
    private Set<Client> clients = new HashSet<>();

    private final NewsGenerationService newsGenerationService;

    public void addSubscriber(Client client) {
        this.clients.add(client);
    }
    public void sendNewsFor(LocalDate localDate) {
        News news = newsGenerationService.generateNewsFor(localDate);
        clients.forEach(c -> c.receive(news));
    }

    public void removeSubscriber(Client client) {
        this.clients.remove(client);
    }

}
