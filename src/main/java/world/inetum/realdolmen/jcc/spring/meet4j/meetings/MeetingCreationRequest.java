package world.inetum.realdolmen.jcc.spring.meet4j.meetings;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class MeetingCreationRequest {

    private LocalDateTime start;
    private Duration duration;
    private List<Long> inviteeIds;

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

    public List<Long> getInviteeIds() {
        return inviteeIds;
    }

    public void setInviteeIds(List<Long> inviteeIds) {
        this.inviteeIds = inviteeIds;
    }
}
