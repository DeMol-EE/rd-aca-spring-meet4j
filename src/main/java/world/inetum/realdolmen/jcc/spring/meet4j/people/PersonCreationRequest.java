package world.inetum.realdolmen.jcc.spring.meet4j.people;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PersonCreationRequest {

    @NotBlank
    private final String firstName;
    @NotBlank
    private final String lastName;
    @Email
    @NotNull
    private final String email;
    @NotBlank
    private final String password;

    @JsonCreator
    public PersonCreationRequest(
            String firstName,
            String lastName,
            String email,
            String password
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
