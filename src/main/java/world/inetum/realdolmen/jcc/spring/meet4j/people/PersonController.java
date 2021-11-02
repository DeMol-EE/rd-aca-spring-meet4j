package world.inetum.realdolmen.jcc.spring.meet4j.people;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import world.inetum.realdolmen.jcc.spring.meet4j.NotFoundException;
import world.inetum.realdolmen.jcc.spring.meet4j.meetings.InvitationStatus;
import world.inetum.realdolmen.jcc.spring.meet4j.meetings.MeetingDetailsResponse;
import world.inetum.realdolmen.jcc.spring.meet4j.meetings.MeetingMapper;
import world.inetum.realdolmen.jcc.spring.meet4j.meetings.MeetingRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "people")
public class PersonController {

    private final PersonRepository personRepository;
    private final MeetingRepository meetingRepository;
    private final PasswordEncoder passwordEncoder;
    Logger logger = Logger.getLogger(PersonController.class.getName());

    public PersonController(
            PersonRepository personRepository,
            MeetingRepository meetingRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.personRepository = personRepository;
        this.meetingRepository = meetingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<PersonDetailsResponse> getAll() {
        return personRepository
                .findAll()
                .stream()
                .map(PersonMapper::toDetails)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid @NotNull PersonCreationRequest dto) {
        var person = new Person();
        person.setFirstName(dto.getFirstName());
        person.setLastName(dto.getLastName());
        person.setEmail(dto.getEmail());
        person.setPassword(passwordEncoder.encode(dto.getPassword()));
        personRepository.save(person);
    }

    @GetMapping("{personId}/meetings")
    // add (optional) query params: "after date"
    public List<MeetingDetailsResponse> getMeetingsForPerson(@PathVariable long personId) {
        return meetingRepository
                .getMeetingsForPerson(personId)
                .stream()
                .map(MeetingMapper::toDetails)
                .collect(Collectors.toList());
    }

    //    @PreAuthorize("#personId == authentication.name")
    @PutMapping("{personId}/meetings/{meetingId}")
    public void updateInvitation(
            @PathVariable long personId,
            @PathVariable long meetingId,
            @RequestBody InvitationStatus status,
            Principal principal
    ) throws NotFoundException {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        String claimedPerson = String.valueOf(personId);
        String actualPerson = principal.getName();
        if (!actualPerson.equals(claimedPerson)) {
            logger.log(
                    Level.WARNING,
                    "Detected possible intrusion: user authenticated as {0} tried to access the invitation of user {1}",
                    new Object[]{actualPerson, claimedPerson}
            );
            // Pretend the request failed "normally"
            throw new NotFoundException();
        }
        var person = personRepository
                .findById(personId)
                .orElseThrow(NotFoundException::new);
        var meeting = meetingRepository.findById(meetingId)
                .orElseThrow(NotFoundException::new);
        meeting.getInvitations()
                .put(person, status);
        // poor spring
        meetingRepository.save(meeting);
    }

}
