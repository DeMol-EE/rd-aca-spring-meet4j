package world.inetum.realdolmen.jcc.spring.meet4j.people;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import world.inetum.realdolmen.jcc.spring.meet4j.meetings.MeetingRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PersonRepository personRepository;

    // necessary to instantiate the controller, even if not exercised...
    @MockBean
    MeetingRepository meetingRepository;

    // necessary to instantiate the controller, even if not exercised...
    @MockBean
    PasswordEncoder passwordEncoder;

    @ParameterizedTest
    @ValueSource(strings = {
            // invalid email
            "{\"firstName\": \"Robin\", \"lastName\": \"De Mol\", \"email\": \"robin.demol.realdolmen.com\", \"password\": \"hi\"}",
            // no email
            "{\"firstName\": \"Robin\", \"lastName\": \"De Mol\", \"password\": \"hi\"}",
            // no first name
            "{\"lastName\": \"De Mol\", \"email\": \"robin.demol@realdolmen.com\", \"password\": \"hi\"}",
            // no last name
            "{\"firstName\": \"Robin\", \"email\": \"robin.demol@realdolmen.com\", \"password\": \"hi\"}",
            // no password
            "{\"firstName\": \"Robin\", \"lastName\": \"De Mol\", \"email\": \"robin.demol@realdolmen.com\"}",
    })
    void createInvalidPerson(String json) throws Exception {
        mockMvc.perform(post("/people")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
        // not necessary...
        Mockito.verify(personRepository, Mockito.times(0))
                .save(ArgumentMatchers.any(Person.class));
    }
}
