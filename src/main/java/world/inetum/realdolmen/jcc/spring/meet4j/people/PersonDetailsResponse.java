package world.inetum.realdolmen.jcc.spring.meet4j.people;

import java.util.Objects;

public class PersonDetailsResponse {

    private final long id;
    private final String firstName;
    private final String lastName;
    private final String email;

    public PersonDetailsResponse(
            long id,
            String firstName,
            String lastName,
            String email
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public long getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PersonDetailsResponse that = (PersonDetailsResponse) o;
        return id == that.id && Objects.equals(firstName, that.firstName) && Objects.equals(
                lastName, that.lastName) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email);
    }
}
