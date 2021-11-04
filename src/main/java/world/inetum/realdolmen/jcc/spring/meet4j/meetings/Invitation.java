package world.inetum.realdolmen.jcc.spring.meet4j.meetings;

import java.time.Duration;
import java.time.LocalDateTime;

public class Invitation {
    private final LocalDateTime start;
    private final Duration duration;
    private final InvitationStatus status;

    public Invitation(
            LocalDateTime start,
            Duration duration,
            InvitationStatus status
    ) {
        this.start = start;
        this.duration = duration;
        this.status = status;
    }

    public boolean overlapsWith(LocalDateTime start, LocalDateTime end) {
        // rejected invitations are ignored
        if (status == InvitationStatus.REJECTED) {
            return false;
        }
        LocalDateTime meetingStart = this.start;
        LocalDateTime meetingEnd = this.start.plus(duration);
        return end.isAfter(meetingStart) && start.isBefore(meetingEnd);
    }

}
