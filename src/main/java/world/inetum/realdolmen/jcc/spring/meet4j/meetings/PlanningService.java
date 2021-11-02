package world.inetum.realdolmen.jcc.spring.meet4j.meetings;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PlanningService {

    public Meeting proposeMeetingFor(List<Meeting> meetings, Duration duration, LocalDateTime now) {
        LocalDateTime threshold = now.plusWeeks(2);
        LocalDateTime moment = now.truncatedTo(ChronoUnit.HOURS).plusHours(1);
        do {
            // iterate over slots until a "legal" candidate is found (on workday + in work hours)
            while (isWeekend(moment.getDayOfWeek()) || !isWorkHour(moment.getHour())) {
                moment = moment.plus(duration);
            }
            // see if we haven't gone too far yet
            if (moment.isAfter(threshold)) {
                return null;
            }
            LocalDateTime start = moment;
            LocalDateTime end = moment.plus(duration);
            boolean slotIsFree = meetings.stream()
                    .noneMatch(it -> it.overlapsWith(start, end));
            if (slotIsFree) {
                Meeting proposal = new Meeting();
                proposal.setDuration(duration); // 1h
                proposal.setStart(start);
                // do no set invitations, this is postponed until the create method
                return proposal;
            } else {
                moment = moment.plus(duration);
            }
        } while (moment.isBefore(threshold));
        // in case no slot was found
        return null;
    }

    private boolean isWeekend(DayOfWeek dow) {
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }

    private boolean isWorkHour(int hour) {
        return hour >= 9 && hour <= 15;
    }
}
