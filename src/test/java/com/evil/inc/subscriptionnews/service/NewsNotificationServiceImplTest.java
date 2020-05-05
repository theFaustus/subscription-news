package com.evil.inc.subscriptionnews.service;

import com.evil.inc.subscriptionnews.domain.Client;
import com.evil.inc.subscriptionnews.domain.News;
import com.evil.inc.subscriptionnews.domain.NewsType;
import com.evil.inc.subscriptionnews.exceptions.NewsProviderConnectionTimedOutException;
import com.evil.inc.subscriptionnews.fixture.NewsFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.evil.inc.subscriptionnews.domain.NewsType.FULL;
import static com.evil.inc.subscriptionnews.domain.NewsType.HEADERS_ONLY;
import static org.assertj.core.api.Assertions.assertThat;

class NewsNotificationServiceImplTest {

    private NewsNotificationServiceImpl newsNotificationService;

    @BeforeEach
    void setUp() {
        //Fixture setup
    }

    class FakeNewsGenerationService implements NewsGenerationService {
        @Override
        public News generateNewsFor(LocalDate localDate) {
            return new News(localDate, "Fake news", FULL);
        }
    }


    class NewsGenerationServiceMock implements NewsGenerationService {
        private Map<LocalDate, Integer> localDateInvocationsMap = new HashMap<>();

        @Override
        public News generateNewsFor(LocalDate localDate) {
            localDateInvocationsMap.merge(localDate, 1, Integer::sum);
            return null;
        }

        public void verify(int expectedNumberOfInvocations, LocalDate expectedDate) {
            assertThat(localDateInvocationsMap.containsKey(expectedDate)).isTrue();
            assertThat(localDateInvocationsMap.get(expectedDate)).isEqualTo(expectedNumberOfInvocations);
        }
    }

    class NewsGenerationServiceSpy implements NewsGenerationService {
        private int numberOfInvocations = 0;

        @Override
        public News generateNewsFor(LocalDate localDate) {
            numberOfInvocations++;
            return null;
        }
        public int getNumberOfInvocations() {
            return numberOfInvocations;
        }
    }

    class NewsGenerationServiceSaboteurStub implements NewsGenerationService {
        @Override
        public News generateNewsFor(LocalDate localDate) {
            throw new NewsProviderConnectionTimedOutException();
        }
    }

    class DummyNewsGenerationService implements NewsGenerationService {
        @Override
        public News generateNewsFor(LocalDate localDate) {
            return null;
        }
    }

}


