package world.inetum.realdolmen.jcc.spring.meet4j.people;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import world.inetum.realdolmen.jcc.spring.meet4j.EmailService;

import static org.hamcrest.Matchers.is;

// Showcases usage of @Sql to run custom scripts before each method
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE) // Important! Without this, @Sql on test overrides this (so truncate would not happen)
class PersonControllerIT {

    protected RequestSpecification spec;
    // Ensure no mails are sent (alternatively: set up MockRestServiceServer)
    @MockBean
    EmailService emailService;
    // RestTemplate is not auto configured, but the builder is
    @Autowired
    RestTemplateBuilder restTemplateBuilder;
    @LocalServerPort
    int port;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUpRestAssured(@LocalServerPort int port) {
        spec = RestAssured.given()
                .port(port);
    }

    // This test showcases RestTemplate instead of RestAssured
    @Test
    void create() throws Exception {
        var json = "{\"firstName\": \"Robin\", \"lastName\": \"De Mol\", \"email\": \"robin.demol@realdolmen.com\", \"password\": \"hi\"}";
        var restTemplate = restTemplateBuilder.build();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // act
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/people",
                new HttpEntity<>(json, headers),
                Void.class
        );
        // assert
        Assertions.assertEquals(201, response.getStatusCodeValue());
        var rs = jdbcTemplate.queryForRowSet(
                "select password from persons where first_name = ? and last_name = ? and email = ?",
                "Robin",
                "De Mol",
                "robin.demol@realdolmen.com");
        Assertions.assertTrue(rs.next());
        // verify that password is not stored plain text
        Assertions.assertNotEquals("hi", rs.getString("password"));
    }

    // Too trivial to IT... read-only and no domain logic is covered.
    @Test
    @Sql(statements = {
            "insert into persons (first_name, last_name, email, password) values ('Brecht', 'G', 'brecht.g@realdolmen.com', 'sdasdad');",
    })
    void getAll() throws Exception {
        // act
        var response = spec.when().get("/people");
        // assert
        response.then()
                .body("[0].firstName", is("Brecht"))
                .body("[0].lastName", is("G"))
                .body("[0].email", is("brecht.g@realdolmen.com"));
    }

    @Test
    @Disabled
    void getMeetingsForPerson() {
        // Also too trivial to IT...
    }

    @Test
    @Sql(statements = {
            // (1939730020 = encoded "sdasdad")
            "insert into persons (id, first_name, last_name, email, password) values (1, 'Brecht', 'G', 'brecht.g@realdolmen.com', '1939730020');",
            "insert into persons (id, first_name, last_name, email, password) values (2, 'Robin', 'DM', 'robin.dm@realdolmen.com', 'hi acaddemicts');",
            "insert into meetings (id, start, duration) values (1, to_timestamp('2025-01-20 16:45:05', 'YYYY-MM-DD HH24:MI:ss'), 3600000);",
    })
    void updateInvitation() throws Exception {
        var response = spec
                .auth()
                .preemptive()
                .basic("brecht.g@realdolmen.com", "sdasdad")
                .pathParam("pid", 1L)
                .pathParam("mid", 1L)
                .body("\"ACCEPTED\"")
                .contentType(ContentType.JSON)
                .when()
                .put("/people/{pid}/meetings/{mid}");
        // assert
        response.then()
                .statusCode(is(200));
        var rs = jdbcTemplate.queryForRowSet(
                "select status from invitations where meeting_id = ? and person_id = ? and status = ?",
                1L, 1L, "ACCEPTED"
        );
        Assertions.assertTrue(rs.next());
    }

}