package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.Client;
import com.evil.inc.subscriptionnews.repository.ClientRepository;
import com.evil.inc.subscriptionnews.service.dto.News;
import com.evil.inc.subscriptionnews.service.dto.NewsType;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) //JUNIT 5
        //@RunWith(MockitoJunitRunner.class) // JUNIT < 5
class NewsNotificationServiceImplTest {

    @Mock
    private NewsGenerationService newsGenerationService;
    @Mock
    private ClientRepository clientRepository;

    private NewsNotificationServiceImpl newsNotificationService;

    private LocalDate date;
    private String clientEmail;
    private Client client;

    @BeforeEach
    void setUp() {
        //SUT
        newsNotificationService = new NewsNotificationServiceImpl(newsGenerationService, clientRepository);
        //Fixture setup
        clientEmail = "aba@gmail.com";
        client = Client.builder().id("123-456-789").email(clientEmail).build();
        date = LocalDate.parse("2019-01-01");

    }

    @Test
    void addSubscriber_whenInvoked_returnsClientId() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        final String id = newsNotificationService.addSubscriber(clientEmail);

        assertThat(id).isEqualTo(client.getId());
    }

    @Test
    void removeSubscriber_whenInvoked_deletesClient() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        final String firstClientId = newsNotificationService.addSubscriber(clientEmail);

        newsNotificationService.removeSubscriber(firstClientId);

        verify(clientRepository).deleteById(firstClientId);
    }

    @Test
    void sendNewsFor_withNullDate_throwsIllegalArgumentException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .describedAs("Date must not be null")
                .isThrownBy(() -> newsNotificationService.sendNewsFor(null));
    }

    @Test
    void sendNewsFor_withValidDate_sendsNews() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        newsNotificationService.addSubscriber(clientEmail);
        when(newsGenerationService.generateHeadersOnlyNewsFor(date))
                .thenReturn(Collections.singletonList(NewsFixture.headerOnlyNews().build()));

        newsNotificationService.sendNewsFor(date);

        verify(newsGenerationService, times(1)).generateHeadersOnlyNewsFor(date);
        assertThat(newsNotificationService.getNews())
                .isEqualTo(Collections.singletonList(NewsFixture.headerOnlyNews().build()));

    }

    @Test
    void sendNewsFor_withValidDateAndThrowingNewsProviderException_sendsErrorNews() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        newsNotificationService.addSubscriber(clientEmail);
        when(newsGenerationService.generateHeadersOnlyNewsFor(date))
                .thenThrow(new NewsProviderConnectionTimedOutException());

        newsNotificationService.sendNewsFor(date);

        verify(newsGenerationService).generateHeadersOnlyNewsFor(date);
        assertThat(newsNotificationService.getNews())
                .isEqualTo(Collections.singletonList(
                        new News(date, "unknown", "unknown", "Oops.", "admin", NewsType.HEADERS_ONLY)));
    }

    @Test
    void sendNewsForDayBefore_withNullDate_throwsIllegalArgumentException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .describedAs("Date must not be null")
                .isThrownBy(() -> newsNotificationService.sendNewsForDayBefore(null));
    }

    @Test
    void sendNewsForDayBefore_withValidDate_sendsNews() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        newsNotificationService.addSubscriber(clientEmail);
        when(newsGenerationService.generateHeadersOnlyNewsFor(date.minusDays(1)))
                .thenReturn(Collections.singletonList(NewsFixture.headerOnlyNews().build()));

        newsNotificationService.sendNewsForDayBefore(date);

        ArgumentCaptor<LocalDate> localDateArgumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(newsGenerationService).generateHeadersOnlyNewsFor(localDateArgumentCaptor.capture());
        assertThat(localDateArgumentCaptor.getValue()).isEqualTo(date.minusDays(1));
        assertThat(newsNotificationService.getNews())
                .isEqualTo(Collections.singletonList(NewsFixture.headerOnlyNews().build()));

    }

    @Test
    void sendNewsForDayBefore_withValidDateAndThrowingNewsProviderException_sendsErrorNews() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        newsNotificationService.addSubscriber(clientEmail);
        when(newsGenerationService.generateHeadersOnlyNewsFor(date.minusDays(1)))
                .thenThrow(new NewsProviderConnectionTimedOutException());

        newsNotificationService.sendNewsForDayBefore(date);

        ArgumentCaptor<LocalDate> localDateArgumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(newsGenerationService).generateHeadersOnlyNewsFor(localDateArgumentCaptor.capture());
        assertThat(localDateArgumentCaptor.getValue()).isEqualTo(date.minusDays(1));
        assertThat(newsNotificationService.getNews())
                .isEqualTo(Collections.singletonList(
                        new News(date, "unknown", "unknown", "Oops.", "admin", NewsType.HEADERS_ONLY)));
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


