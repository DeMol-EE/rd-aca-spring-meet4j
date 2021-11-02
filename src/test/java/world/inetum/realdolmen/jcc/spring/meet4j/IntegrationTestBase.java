package world.inetum.realdolmen.jcc.spring.meet4j;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class IntegrationTestBase {

    private DataSource dataSource;

    @BeforeEach
    void clearDatabase(@Autowired DataSource dataSource) throws Exception {
        this.dataSource = dataSource;
        doInTx(con -> {
            con.prepareStatement("delete from invitations;").execute();
            con.prepareStatement("delete from meetings;").execute();
            con.prepareStatement("delete from persons;").execute();
        });
    }

    public void doInTx(InTx c) throws Exception {
        try (var con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            c.apply(con);
            con.commit();
        }
    }

    protected void insertPerson(
            long id,
            String firstName,
            String lastName,
            String email,
            String password
    ) throws Exception {
        doInTx(c -> {
            var ps = c.prepareStatement(
                    "insert into persons (id, first_name, last_name, email, password) values (?, ?, ?, ?, ?)"
            );
            ps.setLong(1, id);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, email);
            ps.setString(5, password);
            ps.executeUpdate();
        });
    }


    protected void insertMeeting(
            long meetingId,
            List<Long> invitees
    ) throws Exception {
        doInTx(c -> {
            var ps = c.prepareStatement("insert into meetings (id, start, duration) values (?, ?, ?)");
            ps.setLong(1, meetingId);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, Duration.ofHours(1).toMillis());
            ps.executeUpdate();
            for (Long invitee : invitees) {
                var ips = c.prepareStatement(
                        "insert into invitations (meeting_id, person_id, status) values (?, ?, ?)");
                ips.setLong(1, meetingId);
                ips.setLong(2, invitee);
                ips.setString(3, "NO_RESPONSE");
                ips.executeUpdate();
            }
        });
    }
}
