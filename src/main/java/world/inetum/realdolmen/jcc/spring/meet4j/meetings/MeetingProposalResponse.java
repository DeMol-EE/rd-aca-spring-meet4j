package world.inetum.realdolmen.jcc.spring.meet4j.meetings;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class MeetingProposalResponse {

    public final LocalDateTime start;
    public final Duration duration;
    public final List<Long> inviteeIds;

    public MeetingProposalResponse(
            LocalDateTime start,
            Duration duration,
            List<Long> inviteeIds
    ) {
        this.start = start;
        this.duration = duration;
        this.inviteeIds = inviteeIds;
    }
}
