package world.inetum.realdolmen.jcc.spring.meet4j.meetings;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class MeetingDetailsResponse {

    public final LocalDateTime start;
    public final Duration duration;
    public final Map<String, InvitationStatus> invitations;

    public MeetingDetailsResponse(
            LocalDateTime start,
            Duration duration,
            Map<String, InvitationStatus> invitations
    ) {
        this.start = start;
        this.duration = duration;
        this.invitations = invitations;
    }
}
