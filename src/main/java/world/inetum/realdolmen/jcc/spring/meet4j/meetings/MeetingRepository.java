package world.inetum.realdolmen.jcc.spring.meet4j.meetings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("select m from Meeting m join fetch m.invitations where m.id = :id")
    Meeting getById(@Param("id") long id);

    @Query("select m from Meeting m join fetch m.invitations i where key(i).id = :pId order by m.start ASC")
    List<Meeting> getMeetingsForPerson(@Param("pId") long personId);

    @Query("select m from Meeting m join fetch m.invitations i where key(i).id in :ids and m.start >= :now")
    List<Meeting> getFutureMeetingsForPeople(
            @Param("ids") List<Long> ids,
            @Param("now") LocalDateTime now
    );
}
