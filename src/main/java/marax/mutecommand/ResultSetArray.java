package marax.mutecommand;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetArray {
    void run(ResultSet resultset) throws SQLException;
}
