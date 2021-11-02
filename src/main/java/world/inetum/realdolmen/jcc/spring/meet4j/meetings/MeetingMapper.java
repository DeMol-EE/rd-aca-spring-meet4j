package world.inetum.realdolmen.jcc.spring.meet4j.meetings;

import java.util.Map;
import java.util.stream.Collectors;

public class MeetingMapper {

    public static MeetingDetailsResponse toDetails(Meeting meeting) {
        return new MeetingDetailsResponse(
                meeting.getStart(),
                meeting.getDuration(),
                meeting.getInvitations()
                        .entrySet()
                        .stream()
                        .map(e -> Map.entry(e.getKey().getFullName(), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
