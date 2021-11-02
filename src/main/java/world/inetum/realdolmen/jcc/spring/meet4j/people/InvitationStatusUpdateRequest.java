package world.inetum.realdolmen.jcc.spring.meet4j.people;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import world.inetum.realdolmen.jcc.spring.meet4j.meetings.InvitationStatus;

import javax.validation.constraints.NotNull;

public class InvitationStatusUpdateRequest {
    @NotNull
    private final InvitationStatus status;

    @JsonCreator
    public InvitationStatusUpdateRequest(
            @JsonProperty InvitationStatus status
    ) {
        this.status = status;
    }

    public InvitationStatus getStatus() {
        return status;
    }
}
