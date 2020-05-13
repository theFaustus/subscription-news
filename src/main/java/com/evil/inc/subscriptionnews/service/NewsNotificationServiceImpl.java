package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.Client;
import com.evil.inc.subscriptionnews.domain.News;
import com.evil.inc.subscriptionnews.domain.NewsType;
import com.evil.inc.subscriptionnews.exceptions.NewsProviderConnectionTimedOutException;
import com.evil.inc.subscriptionnews.service.contracts.NewsGenerationService;
import com.evil.inc.subscriptionnews.service.contracts.NewsNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
class NewsNotificationServiceImpl implements NewsNotificationService {
    private Set<Client> clients = new HashSet<>();

    private final NewsGenerationService newsGenerationService;

    @Override
    public void addSubscriber(Client client) {
        this.clients.add(client);
    }

    @Override
    public void sendNewsFor(LocalDate localDate) {
        if(localDate == null){
            throw new IllegalArgumentException("Date must not be null");
        }
        List<News> news;
        try {
            news = newsGenerationService.generateHeadersOnlyNewsFor(localDate);
        } catch (NewsProviderConnectionTimedOutException ex) {
            news = Collections.singletonList(new News(localDate, "unknown", "unknown", "Oops.", "admin", NewsType.HEADERS_ONLY));
            log.debug("Oops, something happened", ex);
        }
        for (Client c : clients) {
            c.receive(news);
        }
    }

    @Override
    public void sendNewsForYesterday(LocalDate localDate) {
        if(localDate == null){
            throw new IllegalArgumentException("Date must not be null");
        }
        List<News> news;
        try {
            news = newsGenerationService.generateHeadersOnlyNewsFor(localDate.minusDays(1));
        } catch (NewsProviderConnectionTimedOutException ex) {
            news = Collections.singletonList(new News(localDate, "unknown", "unknown", "Oops.", "admin", NewsType.HEADERS_ONLY));
            log.debug("Oops, something happened", ex);
        }
        for (Client c : clients) {
            c.receive(news);
        }
    }

    @Override
    public void removeSubscriber(Client client) {
        this.clients.removeIf(c -> c.getId().equals(client.getId()));
    }

}
