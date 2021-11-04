package world.inetum.realdolmen.jcc.spring.meet4j.meetings;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import world.inetum.realdolmen.jcc.spring.meet4j.DateTimeProducer;
import world.inetum.realdolmen.jcc.spring.meet4j.EmailService;
import world.inetum.realdolmen.jcc.spring.meet4j.RestIntegrationTestBase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;

@AutoConfigureMockMvc
class MeetingControllerIT extends RestIntegrationTestBase {

    @MockBean
    DateTimeProducer dateTimeProducer;

    @MockBean
    EmailService emailService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @Disabled
    void getById() throws Exception {
        // too trivial to test
    }

    // GET that is NOT too trivial for an IT, covers business logic!
    // This test shows MockMvc instead of RestAssured
    @Test
    void getProposal() throws Exception {
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "hello");
        insertPerson(2L, "Robin", "DM", "robin.dm@realdolmen.com", "hi");
        var now = LocalDateTime.of(
                LocalDate.of(2021, 5, 20), // Thursday
                LocalTime.of(14, 30)
        );
        // This is tricky! Only way to avoid this is to basically reimplement the algo here... which is an anti-pattern
        Mockito.doReturn(now)
                .when(dateTimeProducer)
                .now();
        // act
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/meetings")
                .queryParam("inv", "1", "2"));
        // assert
        response.andExpect(MockMvcResultMatchers.jsonPath("$.start", equalTo("2021-05-20T15:00:00")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration", equalTo("PT1H")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.inviteeIds", containsInAnyOrder(1, 2)));
    }

    @Test
    void create() throws Exception {
        // arrange
        insertPerson(1L, "Brecht", "G", "brecht.g@realdolmen.com", "hello");
        insertPerson(2L, "Robin", "DM", "robin.dm@realdolmen.com", "hi");
        var tomorrowAtNine = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0);
        // act
        var response = spec
                .body("{\"start\":\"" + tomorrowAtNine + "\", \"duration\": \"PT1H\", \"inviteeIds\": [1, 2]}")
                .contentType(ContentType.JSON)
                .post("/meetings");
        // assert
        response.then()
                .statusCode(is(201));
        Mockito.verify(emailService)
                .sendEmail("brecht.g@realdolmen.com", tomorrowAtNine);
        Mockito.verify(emailService)
                .sendEmail("robin.dm@realdolmen.com", tomorrowAtNine);
        doInTx(c -> {
            var ps = c.prepareStatement("select 1 from meetings");
            var rs = ps.executeQuery();
            // Verify there is a meeting
            Assertions.assertTrue(rs.next());
            // should also verify that invitations are created...
            var ips = c.prepareStatement("select person_id, status from invitations where meeting_id = ?");
            ips.setLong(1, 1L);
            var irs = ips.executeQuery();
            var ids = new HashSet<Long>();
            while (irs.next()) {
                ids.add(irs.getLong("person_id"));
                // verify that default status is NO_RESPONSE
                Assertions.assertEquals("NO_RESPONSE", irs.getString("status"));
            }
            Assertions.assertEquals(Set.of(1L, 2L), ids);
        });
    }
}