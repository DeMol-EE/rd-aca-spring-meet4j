package world.inetum.realdolmen.jcc.spring.meet4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import world.inetum.realdolmen.jcc.spring.meet4j.meetings.MeetingInvitationMailingException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void notFound(NotFoundException nfe) {
    }

    @ExceptionHandler(MeetingInvitationMailingException.class)
    public ResponseEntity<String> meetingInvitationMails(MeetingInvitationMailingException mime) {
        String body = "Failed to send email to: " + mime
                .getExceptions()
                .stream()
                .map(e -> e.getEmail() + " (status: " + e.getStatus() + ")")
                .collect(Collectors.joining());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }
}
