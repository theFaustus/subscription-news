package com.evil.inc.subscriptionnews.web.controller;

import com.evil.inc.subscriptionnews.domain.News;
import com.evil.inc.subscriptionnews.domain.NewsType;
import com.evil.inc.subscriptionnews.fixture.NewsFixture;
import com.evil.inc.subscriptionnews.service.contracts.NewsGenerationService;
import com.evil.inc.subscriptionnews.service.contracts.NewsNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NewsControllerTest {

    @Mock
    private NewsNotificationService newsNotificationService;

    @Mock
    private NewsGenerationService newsGenerationService;

    private MockMvc mockMvc;

    @InjectMocks
    private NewsController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void subscribe_whenInvoked_returns201() throws Exception {
        mockMvc.perform(post("/news/subscribe")
                .content("aba@gmail.com"))
                .andExpect(status().isCreated());
    }

    @Test
    void unsubscribe_whenInvoked_returns204() throws Exception {
//        mockMvc.perform(delete("/news/unsubscribe/123"))
//                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/news/unsubscribe/{id}", "123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void send_whenInvoked_returns200() throws Exception {
        mockMvc.perform(post("/news/send/{date}", "2019-01-01"))
                .andExpect(status().isOk());
    }

    @Test
    void getNews_whenInvoked_returnsNewsForToday() throws Exception {
        final LocalDate date = LocalDate.parse("2019-01-01");
        final News news = NewsFixture.builder()
                .date(date)
                .content("Hoba")
                .author("Nobody")
                .type(NewsType.HEADERS_ONLY)
                .build()
                .get();
        when(newsGenerationService.generateHeadersOnlyNewsFor(date))
                .thenReturn(Collections.singletonList(news));

        mockMvc.perform(get("/news/{date}", "2019-01-01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               // don`t do that - or i`ll find you! .andExpect(content().json("[{\"date\":\"2019-01-01\",\"author\":\"Nobody\",\"title\":null,\"content\":\"Hoba\",\"source\":null,\"type\":\"HEADERS_ONLY\"}]"))
                .andExpect(jsonPath("$[0].date").value(news.getDate().toString()))
                .andExpect(jsonPath("$[0].author").value(news.getAuthor()))
                .andExpect(jsonPath("$[0].title").value(news.getTitle()))
                .andExpect(jsonPath("$[0].content").value(news.getContent()))
                .andExpect(jsonPath("$[0].source").value(news.getSource()))
                .andExpect(jsonPath("$[0].type").value(news.getType().toString()));
    }
}