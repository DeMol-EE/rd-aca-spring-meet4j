package world.inetum.realdolmen.jcc.spring.meet4j.meetings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;

class PlanningServiceTest {

    private final PlanningService sut = new PlanningService();

    @Test
    public void findsEarliestMomentNextDay() {
        LocalDateTime begin = LocalDateTime.of(
                LocalDate.of(2021, 8, 23), // Monday,
                LocalTime.of(20, 7)
        );
        Meeting proposal = sut.proposeMeetingFor(Collections.emptyList(), Duration.ofMinutes(60), begin);
        Assertions.assertEquals(
                LocalDateTime.of(2021, 8, 24, 9, 0),
                proposal.getStart()
        );
    }

    @Test
    public void findsEarliestMomentAfterWeekend() {
        LocalDateTime begin = LocalDateTime.of(
                LocalDate.of(2021, 8, 28), // Saturday
                LocalTime.of(10, 7)
        );
        Meeting proposal = sut.proposeMeetingFor(Collections.emptyList(), Duration.ofMinutes(60), begin);
        Assertions.assertEquals(
                LocalDateTime.of(2021, 8, 30, 9, 0),
                proposal.getStart()
        );
    }

    @Test
    public void treatsAcceptedInvitationsAsNotAvailable() {
        LocalDateTime begin = LocalDateTime.of(
                LocalDate.of(2021, 8, 23), // Monday
                LocalTime.of(20, 7)
        );
        Invitation i = new Invitation(
                LocalDateTime.of(2021, 8, 24, 9, 0),
                Duration.ofMinutes(60),
                InvitationStatus.ACCEPTED
        );
        Meeting proposal = sut.proposeMeetingFor(Collections.singletonList(i), Duration.ofMinutes(60), begin);
        Assertions.assertEquals(
                LocalDateTime.of(2021, 8, 24, 10, 0),
                proposal.getStart()
        );
    }

    @Test
    public void treatsPendingInvitationsAsNotAvailable() {
        LocalDateTime begin = LocalDateTime.of(
                LocalDate.of(2021, 8, 23), // Monday
                LocalTime.of(20, 7)
        );
        Invitation i = new Invitation(
                LocalDateTime.of(2021, 8, 24, 9, 0),
                Duration.ofMinutes(60),
                InvitationStatus.NO_RESPONSE
        );
        Meeting proposal = sut.proposeMeetingFor(Collections.singletonList(i), Duration.ofMinutes(60), begin);
        Assertions.assertEquals(
                LocalDateTime.of(2021, 8, 24, 10, 0),
                proposal.getStart()
        );
    }

    @Test
    public void treatsRejectedInvitationsAsAvailable() {
        LocalDateTime begin = LocalDateTime.of(
                LocalDate.of(2021, 8, 23), // Monday
                LocalTime.of(20, 7)
        );
        Invitation i = new Invitation(
                LocalDateTime.of(2021, 8, 24, 9, 0),
                Duration.ofMinutes(60),
                InvitationStatus.REJECTED);
        Meeting proposal = sut.proposeMeetingFor(Collections.singletonList(i), Duration.ofMinutes(60), begin);
        Assertions.assertEquals(
                LocalDateTime.of(2021, 8, 24, 9, 0),
                proposal.getStart()
        );
    }

}