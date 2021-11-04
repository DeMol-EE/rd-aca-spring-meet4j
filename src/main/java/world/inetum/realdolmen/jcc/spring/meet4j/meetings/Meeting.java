package world.inetum.realdolmen.jcc.spring.meet4j.meetings;

import world.inetum.realdolmen.jcc.spring.meet4j.people.Person;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "meetings")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    @FutureOrPresent
    @NotNull
    @Column(name = "start", nullable = false)
    private LocalDateTime start;
    @NotNull
    @Column(name = "duration", nullable = false)
    private Duration duration;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "invitations",
            joinColumns = @JoinColumn(name = "meeting_id", nullable = false))
    @MapKeyJoinColumn(name = "person_id")
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Size(min = 2)
    @NotNull
    private Map<Person, InvitationStatus> invitations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Map<Person, InvitationStatus> getInvitations() {
        return invitations;
    }

    public void setInvitations(Map<Person, InvitationStatus> invitations) {
        this.invitations = invitations;
    }

}
