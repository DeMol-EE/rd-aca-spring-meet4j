package world.inetum.realdolmen.jcc.spring.meet4j.meetings;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import world.inetum.realdolmen.jcc.spring.meet4j.DateTimeProducer;
import world.inetum.realdolmen.jcc.spring.meet4j.EmailService;
import world.inetum.realdolmen.jcc.spring.meet4j.people.Person;
import world.inetum.realdolmen.jcc.spring.meet4j.people.PersonRepository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("meetings")
public class MeetingController {

    private final MeetingRepository meetingRepository;
    private final PersonRepository personRepository;
    private final PlanningService planningService;
    private final DateTimeProducer dateTimeProducer;
    private final EmailService emailService;

    public MeetingController(
            MeetingRepository meetingRepository,
            PersonRepository personRepository,
            PlanningService planningService,
            DateTimeProducer dateTimeProducer,
            EmailService emailService
    ) {
        this.meetingRepository = meetingRepository;
        this.personRepository = personRepository;
        this.planningService = planningService;
        this.dateTimeProducer = dateTimeProducer;
        this.emailService = emailService;
    }

    @GetMapping("{id}")
    public MeetingDetailsResponse getById(@PathVariable long id) {
        var meeting = meetingRepository.getById(id);
        return MeetingMapper.toDetails(meeting);
    }

    @GetMapping
    public MeetingProposalResponse getProposal(
            @RequestParam("inv") @NotNull @Size(min = 2) List<Long> inviteeIds
    ) {
        var now = dateTimeProducer.now();
        var meetings = meetingRepository
                .getFutureMeetingsForPeople(
                        inviteeIds,
                        now);
        var invitations = meetings
                .stream()
                .flatMap(meeting -> meeting
                        .getInvitations()
                        .entrySet()
                        .stream()
                        .filter(inv -> inviteeIds.contains(inv.getKey().getId()))
                        .map(inv -> new Invitation(
                                meeting.getStart(),
                                meeting.getDuration(),
                                inv.getValue()
                        )))
                .collect(Collectors.toList());
        var meeting = planningService.proposeMeetingFor(
                invitations,
                Duration.ofHours(1),
                now);
        return new MeetingProposalResponse(
                meeting.getStart(),
                meeting.getDuration(),
                inviteeIds);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(
            @RequestBody @NotNull MeetingCreationRequest dto
    ) throws MeetingInvitationMailingException {
        var meeting = new Meeting();
        meeting.setStart(dto.getStart());
        meeting.setDuration(dto.getDuration());
        meeting.setInvitations(dto.getInviteeIds()
                .stream()
                .map(personRepository::getById)
                .collect(Collectors.toMap(p -> p, p -> InvitationStatus.NO_RESPONSE)));
        meetingRepository.save(meeting);
        var exceptions = new ArrayList<EmailService.EmailFailureException>();
        for (Person invitee : meeting.getInvitations().keySet()) {
            try {
                emailService.sendEmail(invitee.getEmail(), meeting.getStart());
            } catch (EmailService.EmailFailureException e) {
                exceptions.add(e);
            }
        }
        if (!exceptions.isEmpty()) {
            throw new MeetingInvitationMailingException(exceptions);
        }
    }
}
