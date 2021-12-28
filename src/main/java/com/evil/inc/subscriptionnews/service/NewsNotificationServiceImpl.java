package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.Client;
import com.evil.inc.subscriptionnews.repository.ClientRepository;
import com.evil.inc.subscriptionnews.service.dto.News;
import com.evil.inc.subscriptionnews.service.dto.NewsType;
import com.evil.inc.subscriptionnews.exceptions.NewsProviderConnectionTimedOutException;
import com.evil.inc.subscriptionnews.service.contracts.NewsGenerationService;
import com.evil.inc.subscriptionnews.service.contracts.NewsNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
class NewsNotificationServiceImpl implements NewsNotificationService {
    private final NewsGenerationService newsGenerationService;
    private final ClientRepository clientRepository;
    private List<News> news = new CopyOnWriteArrayList<>();

    @Override
    @Transactional(readOnly = true)
    public void sendNewsFor(LocalDate localDate) {
        if (localDate == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        try {
            news = newsGenerationService.generateHeadersOnlyNewsFor(localDate);
        } catch (NewsProviderConnectionTimedOutException ex) {
            news = Collections.singletonList(
                    new News(localDate, "unknown", "unknown", "Oops.", "admin", NewsType.HEADERS_ONLY));
            log.debug("Oops, something happened", ex);
        }
        notifyClients(clientRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public void sendNewsForDayBefore(LocalDate localDate) {
        if (localDate == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        try {
            news = newsGenerationService.generateHeadersOnlyNewsFor(localDate.minusDays(1));
        } catch (NewsProviderConnectionTimedOutException ex) {
            news = Collections.singletonList(
                    new News(localDate, "unknown", "unknown", "Oops.", "admin", NewsType.HEADERS_ONLY));
            log.debug("Oops, something happened", ex);
        }
        notifyClients(clientRepository.findAll());
    }

    private void notifyClients(final List<Client> clients) {
        for (Client c : clients) {
            //email sending
            log.info("Sending email with {} news to client {}", news.size(), c.getId());
            news.forEach(n -> log.info(n.toString()));
        }
    }

    @Override
    @Transactional
    public void removeSubscriber(String id) {
        clientRepository.deleteById(id);
    }

    @Override
    @Transactional
    public String addSubscriber(String email) {
        final Client client = new Client(email);
        return clientRepository.save(client).getId();
    }

    List<News> getNews() {
        return news;
    }
}
