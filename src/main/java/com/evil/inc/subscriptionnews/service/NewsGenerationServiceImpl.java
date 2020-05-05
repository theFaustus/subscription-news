package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.News;
import com.evil.inc.subscriptionnews.domain.NewsType;
import com.evil.inc.subscriptionnews.exceptions.NewsProviderConnectionTimedOutException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class NewsGenerationServiceImpl implements NewsGenerationService {
    @Override
    public News generateNewsFor(LocalDate localDate) throws NewsProviderConnectionTimedOutException {
        //gather all the news for the localDate
        //if there is no response within some time throw NewsProviderConnectionTimedOutException
        return new News(localDate, "A lot of interesting things", NewsType.FULL);
    }
}
