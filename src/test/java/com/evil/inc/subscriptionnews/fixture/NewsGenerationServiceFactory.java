package com.evil.inc.subscriptionnews.fixture;

import com.evil.inc.subscriptionnews.service.contracts.NewsGenerationService;
import com.evil.inc.subscriptionnews.service.NewsGenerationServiceImpl;
import org.springframework.web.client.RestTemplate;

public class NewsGenerationServiceFactory {
    private static NewsGenerationService newsGenerationService;

    public static NewsGenerationService create() {
        if (newsGenerationService != null)
            return newsGenerationService;
        return new NewsGenerationServiceImpl(new RestTemplate());
    }

    public static void setNewsGenerationService(NewsGenerationService newsGenerationService) {
        NewsGenerationServiceFactory.newsGenerationService = newsGenerationService;
    }
}
