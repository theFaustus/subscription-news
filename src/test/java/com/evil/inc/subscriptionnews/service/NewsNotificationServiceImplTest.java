package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.Client;
import com.evil.inc.subscriptionnews.domain.News;
import com.evil.inc.subscriptionnews.domain.NewsType;
import com.evil.inc.subscriptionnews.exceptions.NewsProviderConnectionTimedOutException;
import com.evil.inc.subscriptionnews.fixture.NewsFixture;
import com.evil.inc.subscriptionnews.service.contracts.NewsGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) //JUNIT 5
    //@RunWith(MockitoJunitRunner.class) // JUNIT < 5
class NewsNotificationServiceImplTest {

    @Mock
    private NewsGenerationService newsGenerationService;

    @InjectMocks
    private NewsNotificationServiceImpl newsNotificationService;

    private Client secondClient;
    private Client firstClient;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        //SUT
        newsNotificationService = new NewsNotificationServiceImpl(newsGenerationService);
        //Fixture setup
        secondClient = new Client(UUID.randomUUID().toString(), "aba2@gmail.com");
        firstClient = new Client(UUID.randomUUID().toString(), "aba@gmail.com");
        date = LocalDate.parse("2019-01-01");

    }

    @Test
    void addSubscriber_whenInvoked_increasesNumberOfClients() {
        newsNotificationService.addSubscriber(firstClient);

        assertThat(newsNotificationService.getClients()).hasSize(1);
        assertThat(newsNotificationService.getClients()).contains(firstClient);
    }

    @Test
    void removeSubscriber_whenInvoked_decreasesNumberOfClients() {

        newsNotificationService.addSubscriber(firstClient);
        newsNotificationService.addSubscriber(secondClient);
        newsNotificationService.removeSubscriber(firstClient);

        assertThat(newsNotificationService.getClients()).hasSize(1);
        assertThat(newsNotificationService.getClients()).containsOnly(secondClient);
    }

    @Test
    void sendNewsFor_withNullDate_throwsIllegalArgumentException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .describedAs("Date must not be null")
                .isThrownBy(() -> newsNotificationService.sendNewsFor(null));
    }

    @Test
    void sendNewsFor_withValidDate_sendsNews() {
        newsNotificationService.addSubscriber(firstClient);
        newsNotificationService.addSubscriber(secondClient);

        when(newsGenerationService.generateHeadersOnlyNewsFor(date))
                .thenReturn(Collections.singletonList(NewsFixture.headerOnlyNews().get()));

        newsNotificationService.sendNewsFor(date);

        verify(newsGenerationService, times(1)).generateHeadersOnlyNewsFor(date);
        assertThat(newsNotificationService.getNews())
                .isEqualTo(Collections.singletonList(NewsFixture.headerOnlyNews().get()));

    }

    @Test
    void sendNewsFor_withValidDateAndThrowingNewsProviderException_sendsErrorNews() {
        newsNotificationService.addSubscriber(firstClient);
        newsNotificationService.addSubscriber(secondClient);

        when(newsGenerationService.generateHeadersOnlyNewsFor(date))
                .thenThrow(new NewsProviderConnectionTimedOutException());

        newsNotificationService.sendNewsFor(date);

        verify(newsGenerationService).generateHeadersOnlyNewsFor(date);
        assertThat(newsNotificationService.getNews())
                .isEqualTo(Collections.singletonList(new News(date, "unknown", "unknown", "Oops.", "admin", NewsType.HEADERS_ONLY)));
    }

    @Test
    void sendNewsForDayBefore_withNullDate_throwsIllegalArgumentException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .describedAs("Date must not be null")
                .isThrownBy(() -> newsNotificationService.sendNewsForDayBefore(null));
    }

    @Test
    void sendNewsForDayBefore_withValidDate_sendsNews() {
        newsNotificationService.addSubscriber(firstClient);
        newsNotificationService.addSubscriber(secondClient);

        when(newsGenerationService.generateHeadersOnlyNewsFor(date.minusDays(1)))
                .thenReturn(Collections.singletonList(NewsFixture.headerOnlyNews().get()));

        newsNotificationService.sendNewsForDayBefore(date);

        ArgumentCaptor<LocalDate> localDateArgumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(newsGenerationService).generateHeadersOnlyNewsFor(localDateArgumentCaptor.capture());
        assertThat(localDateArgumentCaptor.getValue()).isEqualTo(date.minusDays(1));
        assertThat(newsNotificationService.getNews())
                .isEqualTo(Collections.singletonList(NewsFixture.headerOnlyNews().get()));

    }

    @Test
    void sendNewsForDayBefore_withValidDateAndThrowingNewsProviderException_sendsErrorNews() {
        newsNotificationService.addSubscriber(firstClient);
        newsNotificationService.addSubscriber(secondClient);
        when(newsGenerationService.generateHeadersOnlyNewsFor(date.minusDays(1)))
                .thenThrow(new NewsProviderConnectionTimedOutException());

        newsNotificationService.sendNewsForDayBefore(date);

        ArgumentCaptor<LocalDate> localDateArgumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(newsGenerationService).generateHeadersOnlyNewsFor(localDateArgumentCaptor.capture());
        assertThat(localDateArgumentCaptor.getValue()).isEqualTo(date.minusDays(1));
        assertThat(newsNotificationService.getNews())
                .isEqualTo(Collections.singletonList(new News(date, "unknown", "unknown", "Oops.", "admin", NewsType.HEADERS_ONLY)));
    }


    class FakeNewsGenerationService implements NewsGenerationService {
        public static final String NEWS_PATH = "src/test/java/com/evil/inc/subscriptionnews/service/news.json";

        @Override
        @SneakyThrows
        public List<News> generateHeadersOnlyNewsFor(LocalDate localDate) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return Arrays.asList(mapper.readValue(Paths.get(NEWS_PATH).toFile(), News[].class));
        }
    }


    class NewsGenerationServiceMock implements NewsGenerationService {
        private final Map<LocalDate, Integer> localDateInvocationsMap = new HashMap<>();
        private final int expectedNumberOfInvocations;
        private final LocalDate expectedDate;

        public NewsGenerationServiceMock(int expectedNumberOfInvocations, LocalDate expectedDate) {
            this.expectedNumberOfInvocations = expectedNumberOfInvocations;
            this.expectedDate = expectedDate;
        }

        @Override
        public List<News> generateHeadersOnlyNewsFor(LocalDate localDate) {
            localDateInvocationsMap.merge(localDate, 1, Integer::sum);
            return null;
        }

        public void assertIsSatisfied() {
            assertThat(localDateInvocationsMap.containsKey(expectedDate)).isTrue();
            assertThat(localDateInvocationsMap.get(expectedDate)).isEqualTo(expectedNumberOfInvocations);
        }
    }

    class NewsGenerationServiceSpy implements NewsGenerationService {
        private int numberOfInvocations = 0;

        @Override
        public List<News> generateHeadersOnlyNewsFor(LocalDate localDate) {
            numberOfInvocations++;
            return null;
        }

        public int getNumberOfInvocations() {
            return numberOfInvocations;
        }
    }

    class NewsGenerationServiceSaboteurStub implements NewsGenerationService {
        @Override
        public List<News> generateHeadersOnlyNewsFor(LocalDate localDate) {
            throw new NewsProviderConnectionTimedOutException();
        }
    }

    class DummyNewsGenerationService implements NewsGenerationService {
        @Override
        public List<News> generateHeadersOnlyNewsFor(LocalDate localDate) {
            return null;
        }
    }

}


