package world.inetum.realdolmen.jcc.spring.meet4j;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeProducer {

    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
