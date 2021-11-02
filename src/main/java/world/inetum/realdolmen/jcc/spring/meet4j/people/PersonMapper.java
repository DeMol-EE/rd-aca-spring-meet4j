package world.inetum.realdolmen.jcc.spring.meet4j.people;

public class PersonMapper {

    public static PersonDetailsResponse toDetails(Person person) {
        if (person == null) {
            return null;
        }
        return new PersonDetailsResponse(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getEmail()
        );
    }
}
