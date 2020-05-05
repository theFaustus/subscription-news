package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.Client;
import com.evil.inc.subscriptionnews.domain.News;
import com.evil.inc.subscriptionnews.domain.NewsType;
import com.evil.inc.subscriptionnews.exceptions.NewsProviderConnectionTimedOutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsNotificationServiceImpl implements NewsNotificationService {
    private Set<Client> clients = new HashSet<>();

    private final NewsGenerationService newsGenerationService;

    @Override
    public void addSubscriber(Client client) {
        this.clients.add(client);
    }

    @Override
    public void sendNewsFor(LocalDate localDate) {
        News news;
        try {
            news = newsGenerationService.generateNewsFor(localDate);
        } catch (NewsProviderConnectionTimedOutException ex) {
            news = new News(localDate, "Oops.", NewsType.HEADERS_ONLY);
            log.debug("Oops, something happened", ex);
        }
        for (Client c : clients) {
            c.receive(news);
        }
    }

    @Override
    public void removeSubscriber(Client client) {
        this.clients.remove(client);
    }

}
