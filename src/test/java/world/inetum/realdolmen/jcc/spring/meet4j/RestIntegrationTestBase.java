package world.inetum.realdolmen.jcc.spring.meet4j;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class RestIntegrationTestBase extends IntegrationTestBase {
    protected RequestSpecification spec;

    @BeforeEach
    void setUpRestAssured(@LocalServerPort int port) {
        spec = RestAssured.given()
                .port(port);
    }
}
