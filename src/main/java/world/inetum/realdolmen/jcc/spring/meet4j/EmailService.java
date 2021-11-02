package world.inetum.realdolmen.jcc.spring.meet4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    // RestTemplate is not autoconfigured by SB, but rest template builder is!
    private final RestTemplateBuilder restTemplateBuilder;
    @Value("${meet4j.email.username}")
    String email;
    @Value("${meet4j.email.password}")
    String password;
    @Value("${meet4j.email.url}")
    String url;

    public EmailService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    // add validation to these params?
    public void sendEmail(String email, LocalDateTime start) throws EmailFailureException {
        var http = restTemplateBuilder
                .basicAuthentication(email, password)
                .build();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var body = new HttpEntity<>(
                "You are invited to attend a meeting at " + start.toString(),
                headers);
        var response = http.postForEntity(
                url,
                body,
                Void.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new EmailFailureException(response.getStatusCodeValue(), email);
        }
    }

    public static class EmailFailureException extends Exception {
        private final int status;
        private final String email;

        public EmailFailureException(int status, String email) {
            this.status = status;
            this.email = email;
        }

        public int getStatus() {
            return status;
        }

        public String getEmail() {
            return email;
        }
    }
}
