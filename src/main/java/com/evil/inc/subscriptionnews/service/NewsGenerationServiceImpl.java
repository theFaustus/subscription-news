package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.News;
import com.evil.inc.subscriptionnews.domain.NewsType;
import com.evil.inc.subscriptionnews.exceptions.NewsProviderConnectionTimedOutException;
import com.evil.inc.subscriptionnews.service.contracts.NewsGenerationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public
class NewsGenerationServiceImpl implements NewsGenerationService {
    public static final String NEWS_API = "https://newsapi.org/v2";
    public static final String API_KEY = "465feb3d1a66407bbf5c04f4c923f670";

    private final RestTemplate restTemplate;


    @Override
    public List<News> generateHeadersOnlyNewsFor(LocalDate localDate) throws NewsProviderConnectionTimedOutException {
        ResponseEntity<String> response = restTemplate.getForEntity(NEWS_API + "/top-headlines/?category=general&pageSize=5&from=" + localDate + "&to=" + localDate + "&apiKey=" + API_KEY, String.class);
        List<News> news = new ArrayList<>();
        try {
            JsonNode articlesNode = new ObjectMapper().readTree(response.getBody()).path("articles");
            for (JsonNode article : articlesNode) {
                news.add(new News(localDate,
                        article.path("author").asText(),
                        article.path("title").asText(),
                        article.path("content").asText(),
                        article.path("source").path("name").asText(),
                        NewsType.HEADERS_ONLY
                ));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return news;
    }

}
