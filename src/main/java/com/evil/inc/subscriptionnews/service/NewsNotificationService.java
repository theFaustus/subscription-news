package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.Client;
import com.evil.inc.subscriptionnews.domain.News;

import java.time.LocalDate;

public interface NewsNotificationService {

    void addSubscriber(Client client);

    void sendNewsFor(LocalDate localDate);

    void removeSubscriber(Client client);
}
