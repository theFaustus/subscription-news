package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.News;
import com.evil.inc.subscriptionnews.domain.NewsType;
import com.evil.inc.subscriptionnews.exceptions.NewsProviderConnectionTimedOutException;
import com.evil.inc.subscriptionnews.service.contracts.NewsGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class NewsNotificationServiceImplTest {

    @BeforeEach
    void setUp() {
        //Fixture setup
    }

    class FakeNewsGenerationService implements NewsGenerationService {
        @Override
        public List<News> generateHeadersOnlyNewsFor(LocalDate localDate) {
            return Collections.singletonList(new News(localDate, "unknown", "unknown", "Fake news", "admin", NewsType.HEADERS_ONLY));
        }
    }


    class NewsGenerationServiceMock implements NewsGenerationService {
        private Map<LocalDate, Integer> localDateInvocationsMap = new HashMap<>();
        private int expectedNumberOfInvocations;
        private LocalDate expectedDate;

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


