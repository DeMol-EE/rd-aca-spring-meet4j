package world.inetum.realdolmen.jcc.spring.meet4j;

import java.sql.Connection;

public interface InTx {

    public void apply(Connection connection) throws Exception;
}
