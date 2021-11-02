package world.inetum.realdolmen.jcc.spring.meet4j.people;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import world.inetum.realdolmen.jcc.spring.meet4j.EmailService;
import world.inetum.realdolmen.jcc.spring.meet4j.RestIntegrationTestBase;

import java.util.List;

import static org.hamcrest.Matchers.is;

class PersonControllerIT extends RestIntegrationTestBase {

    @MockBean
    EmailService emailService;

    @Test
    void create() throws Exception {
        var json = "{\"firstName\": \"Robin\", \"lastName\": \"De Mol\", \"email\": \"robin.demol@realdolmen.com\", \"password\": \"hi\"}";
        // act
        var response = spec
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/people");
        // assert
        response.then()
                .statusCode(is(201));
        doInTx(c -> {
            var ps = c.prepareStatement(
                    "select password from persons where first_name = ? and last_name = ? and email = ?"
            );
            ps.setString(1, "Robin");
            ps.setString(2, "De Mol");
            ps.setString(3, "robin.demol@realdolmen.com");
            var rs = ps.executeQuery();
            Assertions.assertTrue(rs.next());
            // verify that password is not stored plain text
            Assertions.assertNotEquals("hi", rs.getString("password"));
        });
    }

    // Too trivial to IT... read-only and no domain logic is covered.
    @Test
    @Disabled
    void getAll() throws Exception {
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "sdasdad");
        // act
        var response = spec.when().get("/people");
        // assert
        response.then()
                .body("firstName", is("Brecht"))
                .body("lastName", is("G"))
                .body("email", is("brecht.g@realdolmen.com"));
    }

    @Test
    @Disabled
    void getMeetingsForPerson() {
        // Also too trivial to IT...
    }

    @Test
    void updateInvitation() throws Exception {
        // arrange (1939730020 = encoded "sdasdad")
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "1939730020");
        insertPerson(2L, "Robin", "DM", "robin.dm@realdolmen.com", "hi acaddemicts");
        insertMeeting(1L, List.of(1L, 2L));
        // act
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
        doInTx(c -> {
            var ps = c.prepareStatement(
                    "select status from invitations where meeting_id = ? and person_id = ? and status = ?");
            ps.setLong(1, 1L);
            ps.setLong(2, 1L);
            ps.setString(3, "ACCEPTED");
            var rs = ps.executeQuery();
            Assertions.assertTrue(rs.next());
        });
    }

    // Too edgy for IT, better to rework to a unit test
    @Test
    void canNotUpdateInvitationWithoutCredentials() throws Exception {
        // arrange (1939730020 = encoded "sdasdad")
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "1939730020");
        insertPerson(2L, "Robin", "DM", "robin.dm@realdolmen.com", "hi acaddemicts");
        insertMeeting(1L, List.of(1L, 2L));
        // act
        var response = spec
                // no auth
                .pathParam("pid", 1L)
                .pathParam("mid", 1L)
                .body("\"ACCEPTED\"")
                .contentType(ContentType.JSON)
                .when()
                .put("/people/{pid}/meetings/{mid}");
        // assert
        response.then()
                .statusCode(is(403));
        doInTx(c -> {
            var ps = c.prepareStatement(
                    "select status from invitations where meeting_id = ? and person_id = ? and status = ?");
            ps.setLong(1, 1L);
            ps.setLong(2, 1L);
            ps.setString(3, "ACCEPTED");
            var rs = ps.executeQuery();
            Assertions.assertFalse(rs.next());
        });
    }

    // Too edgy for IT, better to rework to a unit test
    @Test
    void canNotUpdateInvitationWithWrongCredentials() throws Exception {
        // arrange (1939730020 = encoded "sdasdad")
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "1939730020");
        insertPerson(2L, "Robin", "DM", "robin.dm@realdolmen.com", "hi acaddemicts");
        insertMeeting(1L, List.of(1L, 2L));
        // act
        var response = spec
                .auth()
                .preemptive()
                .basic("brecht.g@realdolmen.com", "wrong-password")
                .pathParam("pid", 1L)
                .pathParam("mid", 1L)
                .body("\"ACCEPTED\"")
                .contentType(ContentType.JSON)
                .when()
                .put("/people/{pid}/meetings/{mid}");
        // assert
        response.then()
                .statusCode(is(401));
        doInTx(c -> {
            var ps = c.prepareStatement(
                    "select status from invitations where meeting_id = ? and person_id = ? and status = ?");
            ps.setLong(1, 1L);
            ps.setLong(2, 1L);
            ps.setString(3, "ACCEPTED");
            var rs = ps.executeQuery();
            Assertions.assertFalse(rs.next());
        });
    }

    // Too edgy for IT, better to rework to a unit test
    @Test
    void canNotUpdateInvitationOfOtherUser() throws Exception {
        // arrange (1939730020 = encoded "sdasdad")
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "1939730020");
        insertPerson(2L, "Robin", "DM", "robin.dm@realdolmen.com", "hi acaddemicts");
        insertMeeting(1L, List.of(1L, 2L));
        // act
        var response = spec
                .auth()
                .preemptive()
                .basic("brecht.g@realdolmen.com", "sdasdad")
                .pathParam("pid", 2L) // incorrect person id!
                .pathParam("mid", 1L)
                .body("\"ACCEPTED\"")
                .contentType(ContentType.JSON)
                .when()
                .put("/people/{pid}/meetings/{mid}");
        // assert
        response.then()
                .statusCode(is(404));
        doInTx(c -> {
            var ps = c.prepareStatement(
                    "select status from invitations where meeting_id = ? and person_id = ? and status = ?");
            ps.setLong(1, 1L);
            ps.setLong(2, 2L);
            ps.setString(3, "ACCEPTED");
            var rs = ps.executeQuery();
            Assertions.assertFalse(rs.next());
        });
    }
}