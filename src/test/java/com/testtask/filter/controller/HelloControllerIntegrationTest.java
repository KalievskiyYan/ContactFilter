package com.testtask.filter.controller;

import com.testtask.filter.data.dto.ContactDto;
import com.testtask.filter.data.dto.ContactsResponseDto;
import io.r2dbc.spi.ConnectionFactory;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {HelloControllerIntegrationTest.Initializer.class})
@Testcontainers
public class HelloControllerIntegrationTest {
    private static WebTestClient webClient;

    @Autowired
    @Qualifier("connectionFactory")
    private ConnectionFactory connectionFactory;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("user")
            .withPassword("pass");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            postgreSQLContainer.start();
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.r2dbc.url=r2dbc:postgresql://localhost:" + postgreSQLContainer.getMappedPort(POSTGRESQL_PORT) + "/" + postgreSQLContainer.getDatabaseName(),
                    "spring.r2dbc.username=" + postgreSQLContainer.getUsername(),
                    "spring.r2dbc.password=" + postgreSQLContainer.getPassword()
            );
        }
    }

    @BeforeAll
    public static void before(@Value("${local.server.port}") int randomServerPort) {
        webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + randomServerPort + "/hello/contacts")
                .build();
    }

    @BeforeEach
    public void rollOutTestData(@Value("classpath:/sql/insertAllContacts.sql") Resource script) {
        executeScriptBlocking(script);
    }

    @AfterEach
    public void cleanUpTestData(@Value("classpath:/sql/deleteAllContacts.sql") Resource script) {
        executeScriptBlocking(script);
    }

    private void executeScriptBlocking(final Resource sqlScript) {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, sqlScript))
                .block();
    }

    @Test
    public void testContainer() {
        Assertions.assertTrue(postgreSQLContainer.isRunning());
    }

    @Test
    public void shouldFindFilteredContacts() {
        String regex = "^A.*$";
        FluxExchangeResult<ContactsResponseDto> exchangeResult = webClient
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("nameFilter", regex).build())
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ContactsResponseDto.class);

        ContactsResponseDto body = exchangeResult.getResponseBody().blockLast();
        Assertions.assertNotNull(body);

        List<ContactDto> contacts = body.getContacts();
        Assertions.assertNotNull(contacts);
        Assertions.assertEquals(6, contacts.size());
        Assertions.assertFalse(contacts.stream().anyMatch(contact -> contact.getName().matches(regex)));
    }

    @Test
    public void shouldReturnEmptyListFromFindFilteredContacts() {
        String regex = "[A-z]*";
        FluxExchangeResult<ContactsResponseDto> exchangeResult = webClient
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("nameFilter", regex).build())
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ContactsResponseDto.class);

        ContactsResponseDto body = exchangeResult.getResponseBody().blockFirst();
        Assertions.assertNotNull(body);

        List<ContactDto> contacts = body.getContacts();
        Assertions.assertNotNull(contacts);
        Assertions.assertTrue(contacts.isEmpty());
    }

    @Test
    void shouldReturnBadRequestForInvalidNameFilter() {
        webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("nameFilter", "[[").build())
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void shouldReturnBadRequestForMissedNameFilter() {
        webClient.get()
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}