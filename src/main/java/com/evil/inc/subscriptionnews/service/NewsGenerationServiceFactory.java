package com.evil.inc.subscriptionnews.service;

public class NewsGenerationServiceFactory {
    private static NewsGenerationService newsGenerationService;

    public static NewsGenerationService create() {
        if (newsGenerationService != null)
            return newsGenerationService;
        return new NewsGenerationServiceImpl();
    }

    public static void setNewsGenerationService(NewsGenerationService newsGenerationService) {
        NewsGenerationServiceFactory.newsGenerationService = newsGenerationService;
    }
}
