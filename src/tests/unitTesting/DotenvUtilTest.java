package unitTesting;

import domain.model.util.DotenvUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DotenvUtilTest {

    @Test
    public void testDotenvUtil() throws Exception {
        DotenvUtil dotenv = new DotenvUtil("database.env");

        String dbUrl = dotenv.getDatabaseUrl();
        String dbUsername = dotenv.getDatabaseUsername();
        String dbPassword = dotenv.getDatabasePassword();

        assertNotNull(dbUrl, "DB_URL should not be null");
        assertNotNull(dbUsername, "DB_USER should not be null");
        assertNotNull(dbPassword, "DB_PASSWORD should not be null");

        System.out.println("DB_URL: " + dbUrl);
        System.out.println("DB_USER: " + dbUsername);
        System.out.println("DB_PASSWORD: " + dbPassword);
    }
}
