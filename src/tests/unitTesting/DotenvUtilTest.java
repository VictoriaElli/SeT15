package unitTesting;

import domain.model.util.DotenvUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DotenvUtilTest {

    // Testmetode for å validere funksjonaliteten til DotenvUtil
    @Test
    public void testDotenvUtil() throws Exception {
        // Opprett en instans av DotenvUtil som peker til "database.env"-filen.
        DotenvUtil dotenv = new DotenvUtil("database.env");

        // Hent database-tilkoblingsdetaljer ved hjelp av DotenvUtil sine metoder.
        String dbUrl = dotenv.getDatabaseUrl();  // Hent database-URL
        String dbUsername = dotenv.getDatabaseUsername();  // Hent database-brukernavn
        String dbPassword = dotenv.getDatabasePassword();  // Hent database-passord

        // Verifiser at database-URL ikke er null (den skal alltid være definert i .env-filen)
        assertNotNull(dbUrl, "DB_URL skal ikke være null");

        // Verifiser at database-brukernavn ikke er null (det skal alltid være definert i .env-filen)
        assertNotNull(dbUsername, "DB_USER skal ikke være null");

        // Verifiser at database-passord ikke er null (det skal alltid være definert i .env-filen)
        assertNotNull(dbPassword, "DB_PASSWORD skal ikke være null");

        // Skriv ut de hentede verdiene for visuell inspeksjon (ikke anbefalt i produksjonskode, kun for testing)
        System.out.println("DB_URL: " + dbUrl);
        System.out.println("DB_USER: " + dbUsername);
        System.out.println("DB_PASSWORD: " + dbPassword);
    }
}
