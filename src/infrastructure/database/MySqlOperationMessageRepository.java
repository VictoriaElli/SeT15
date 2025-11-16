package database;

import domain.model.OperationMessage;
import domain.model.Route;
import domain.repository.OperationMessageRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Dette er et repository som lagrer driftsmeldinger i databasen
// Klassen bruker DatabaseConnector som skal koble seg på databasen
public class MySqlOperationMessageRepository implements OperationMessageRepository {

    // Dette gjør om en rad i databasen til et operationmessage objekt
    private OperationMessage mapRow(ResultSet rs) throws Exception {

        // Dette henter id og tekst
        int id = rs.getInt("id");
        String message = rs.getString("message");

        // Dette henter aktiv status
        boolean isActive = rs.getBoolean("isActive");

        // Dette henter ruteId fra databasen og lager et route objekt
        int routeId = rs.getInt("routeId");
        Route route = new Route(routeId);

        // Dette henter datoen for når meldingen skal publiseres
        Date publishedDate = rs.getDate("published");
        LocalDateTime published = null;
        if (publishedDate != null) {
            published = publishedDate.toLocalDate().atStartOfDay();
        }

        // Dette henter startdato
        Date fromDate = rs.getDate("validFrom");
        LocalDateTime validFrom = null;
        if (fromDate != null) {
            validFrom = fromDate.toLocalDate().atStartOfDay();
        }

        // Dette henter sluttdato
        Date toDate = rs.getDate("validTo");
        LocalDateTime validTo = null;
        if (toDate != null) {
            validTo = toDate.toLocalDate().atTime(23, 59);
        }

        OperationMessage msg = new OperationMessage(id, message, published, route, validFrom, validTo);

        msg.setActive(isActive);

        return msg;
    }

    // Dette er for å lagre en ny driftsmelding i databasen
    @Override
    public void create(OperationMessage msg) {
        try (Connection conn = DatabaseConnector.connect()) {

            conn.setAutoCommit(false);

            // Dette er SQL som skal sette inn en ny driftsmelding i tabellen operationMessage
            String sql = "INSERT INTO operationMessage (message, published, routeId, isActive, validFrom, validTo, createdBy) " +
                    "VALUES (?, CURDATE(), ?, ?, ?, ?, ?)";

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

                // Denne setter hvem som lagret meldingen
                ps.setString(6, msg.getCreatedBy());

                ps.executeUpdate();

                // Dette henter id til den nye driftsmeldingen
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        msg.setId(rs.getInt(1));
                    }
                }
            }

            conn.commit();

            System.out.println("INFO: Driftsmelding ble lagret i databasen.");

        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å lagre driftsmelding (" + e.getMessage() + ")");
            throw new RuntimeException("Databasefeil ved lagring av melding");
        }
    }

    // Dette henter en driftsmelding basert på id
    @Override
    public OperationMessage read(int id) {

        String sql = "SELECT * FROM operationMessage WHERE id = ?";

        try (Connection conn = DatabaseConnector.connect();
             var ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

            return null;

        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å hente driftsmelding (" + e.getMessage() + ")");
            throw new RuntimeException("Databasefeil ved henting av melding");
        }
    }

    // Dette henter alle driftsmeldinger
    @Override
    public List<OperationMessage> readAll() {

        List<OperationMessage> list = new ArrayList<>();
        String sql = "SELECT * FROM operationMessage ORDER BY published DESC";

        try (Connection conn = DatabaseConnector.connect();
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {

            // Dette legger til alle meldinger i listen
            while (rs.next()) {
                list.add(mapRow(rs));
            }

            return list;

        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å hente alle driftsmeldinger (" + e.getMessage() + ")");
            throw new RuntimeException("Databasefeil ved henting av meldinger");
        }
    }

    // Dette oppdaterer en driftsmelding i databasen
    @Override
    public void update(OperationMessage msg) {

        // Dette er SQL som skal oppdatere en eksisterende driftsmelding
        String sql = "UPDATE operationMessage SET message = ?, routeId = ?, isActive = ?, validFrom = ?, validTo = ?, createdBy = ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.connect()) {

            // Bruker var for å slippe å skrive ut type
            try (var ps = conn.prepareStatement(sql)) {

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

                // For å oppdatere createdBy
                ps.setString(6, msg.getCreatedBy());

                // Dette setter en id til meldingen som skal oppdateres
                ps.setInt(7, msg.getId());

                ps.executeUpdate();
            }

            System.out.println("INFO: Driftsmelding ble oppdatert i databasen");

        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å oppdatere driftsmelding (" + e.getMessage() + ")");
            throw new RuntimeException("Databasefeil ved oppdatering av melding");
        }
    }

    // Dette er for å slette en driftsmelding basert på en id
    @Override
    public void delete(int id) {

        String sql = "DELETE FROM operationMessage WHERE id = ?";

        try (Connection conn = DatabaseConnector.connect();
             var ps = conn.prepareStatement(sql)) {

            // Dette setter id til meldingen som skal slettes
            ps.setInt(1, id);

            ps.executeUpdate();

            System.out.println("INFO: Driftsmelding ble slettet fra databasen.");

        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å slette driftsmelding (" + e.getMessage() + ")");
            throw new RuntimeException("Databasefeil ved sletting av melding");
        }
    }
}
