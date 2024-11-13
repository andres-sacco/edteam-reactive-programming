package com.edteam.reservations.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class BaseTest {

    static DockerComposeContainer dockerComposeContainer;

    static {
        dockerComposeContainer = new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                .waitingFor("api-reservation-db",
                        Wait.forLogMessage(".*MySQL init process done. Ready for start up.*", 1))
                .waitingFor("api-catalog-db", Wait.forLogMessage(".*MySQL init process done. Ready for start up.*", 1))
                .withLocalCompose(true);

        dockerComposeContainer.start();
    }

    /*
     * @BeforeAll static void setUp() { dockerComposeContainer.start(); }
     *
     * @AfterAll static void tearDown() { dockerComposeContainer.stop(); }
     */
}
