package com.evil.inc.subscriptionnews.repository;

import com.evil.inc.subscriptionnews.domain.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientRepositoryIntegrationTest {
    public static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:13.3"));

    static {
        postgres.start();
    }

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    //Alternative to EntityManager for use in JPA tests.
    // Provides a subset of EntityManager methods that are useful for tests as well as helper methods for common testing tasks such as persist/flush/find.
    private TestEntityManager entityManager; //convenient for not injecting other repositories

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void findByEmail_withExistingEmail_returnsListOfClients() {
        final Client expectedClient = Client.builder()
                .id("d80c971c-4a48-45cc-9301-d9dd072ac14d")
                .email("jhoonnyc@gmail.com")
                .createdAt(LocalDateTime.parse("2021-12-28T17:06:48.230205"))
                .updatedAt(LocalDateTime.parse("2021-12-28T17:06:48.230205")).build();

        final List<Client> byEmail = clientRepository.findByEmail("jhoonnyc@gmail.com");
        assertThat(byEmail).hasSize(1);
        assertThat(byEmail.get(0)).usingRecursiveComparison().isEqualTo(expectedClient);

    }

    @Test
    @Sql("/test-data/clients/clients.sql")
    void findByCreatedAtAfter_returnsListOfClients() {
        final Client expectedClient = Client.builder()
                .id("d90c971c-4a48-45cc-9301-d9dd072ac14d")
                .email("peter@gmail.com")
                .createdAt(LocalDateTime.parse("2022-12-20T17:06:48.230205"))
                .updatedAt(LocalDateTime.parse("2022-12-20T17:06:48.230205")).build();

        final List<Client> byEmail = clientRepository.findByCreatedAtAfter(LocalDateTime.parse("2022-12-19T17:06:48.230205"));
        assertThat(byEmail).hasSize(1);
        assertThat(byEmail.get(0)).usingRecursiveComparison().isEqualTo(expectedClient);
    }
}
