package database;

import domain.model.OperationMessage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.sql.Types;

// Dette er et repository som lagrer driftsmeldinger i databasen
// Klassen bruker DatabaseConnector som skal koble seg på databasen
public class MySqlOperationMessageRepository {

    // Dette er for å lagre en ny driftsmelding i databasen
    public void create(OperationMessage msg) {
        try (Connection conn = DatabaseConnector.connect()) {

            // Dette er SQL som skal sette inn en ny driftsmelding i tabellen operationMessage
            String sql = "INSERT INTO operationMessage (message, published, routeId, isActive, validFrom, validTo) " +
                    "VALUES (?, CURDATE(), ?, ?, ?, ?)";

            // Bruker var for å slippe å skrive ut type
            try (var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                // Dette er for tekstfeltet til meldingen
                ps.setString(1, msg.getMessage());

                // Dette er for ruten meldingen gjelder
                ps.setInt(2, msg.getRoute().getId());

                // Dette setter om meldingen skal være aktiv
                ps.setBoolean(3, msg.isActive());

                // Dette setter startdato for meldingen gjelder
                ps.setDate(4, Date.valueOf(msg.getValidFrom().toLocalDate()));

                // Dette setter sluttdato om det finnes
                if (msg.getValidTo() != null) {
                    ps.setDate(5, Date.valueOf(msg.getValidTo().toLocalDate()));
                } else {
                    ps.setNull(5, Types.DATE);
                }

                ps.executeUpdate();

                // Dette henter id til den nye driftsmeldingen
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        msg.setId(rs.getInt(1));
                    }
                }
            }

            System.out.println("INFO: Driftsmelding ble lagret i databasen.");

        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å lagre driftsmelding (" + e.getMessage() + ")");
        }
    }
}
