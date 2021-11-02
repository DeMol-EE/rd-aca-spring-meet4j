package world.inetum.realdolmen.jcc.spring.meet4j.meetings;

import world.inetum.realdolmen.jcc.spring.meet4j.EmailService;

import java.util.List;

public class MeetingInvitationMailingException extends Exception {
    private final List<EmailService.EmailFailureException> exceptions;

    public MeetingInvitationMailingException(List<EmailService.EmailFailureException> exceptions) {
        this.exceptions = exceptions;
    }

    public List<EmailService.EmailFailureException> getExceptions() {
        return exceptions;
    }
}
