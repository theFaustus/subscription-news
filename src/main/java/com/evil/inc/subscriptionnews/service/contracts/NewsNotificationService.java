package com.evil.inc.subscriptionnews.service.contracts;

import java.time.LocalDate;

public interface NewsNotificationService {

    String addSubscriber(String email);

    void sendNewsFor(LocalDate localDate);

    void sendNewsForDayBefore(LocalDate localDate);

    void removeSubscriber(String id);
}
