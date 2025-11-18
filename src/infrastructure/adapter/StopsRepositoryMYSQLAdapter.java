package adapter;

import domain.model.Stops;
import org.springframework.stereotype.Repository;
import port.outbound.StopsRepositoryPort;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StopsRepositoryMYSQLAdapter implements StopsRepositoryPort {

    private final DataSource dataSource;

    public StopsRepositoryMYSQLAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // --- CRUD METHODS ---
    @Override
    public void create(Stops stop) {
        String sql = "INSERT INTO stops (name, latitude, longitude, isActive) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, stop.getName());
            stmt.setDouble(2, stop.getLatitude());
            stmt.setDouble(3, stop.getLongitude());
            stmt.setBoolean(4, stop.isActive());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) stop.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create stop", e);
        }
    }

    @Override
    public Optional<Stops> readById(int id) {
        String sql = "SELECT * FROM stops WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRowToStop(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read stop by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Stops> readAll() {
        List<Stops> stops = new ArrayList<>();
        String sql = "SELECT * FROM stops";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) stops.add(mapRowToStop(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read all stops", e);
        }
        return stops;
    }

    @Override
    public void update(Stops stop) {
        String sql = "UPDATE stops SET name = ?, latitude = ?, longitude = ?, isActive = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stop.getName());
            stmt.setDouble(2, stop.getLatitude());
            stmt.setDouble(3, stop.getLongitude());
            stmt.setBoolean(4, stop.isActive());
            stmt.setInt(5, stop.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update stop", e);
        }
    }

    @Override
    public void delete(Stops stop) {
        deleteById(stop.getId());
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM stops WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete stop", e);
        }
    }

    @Override
    public Optional<Stops> findByName(String stopName) {
        String sql = "SELECT * FROM stops WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stopName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRowToStop(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find stop by name", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Stops> findAllActive() {
        List<Stops> stops = new ArrayList<>();
        String sql = "SELECT * FROM stops WHERE isActive = true";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) stops.add(mapRowToStop(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all active stops", e);
        }
        return stops;
    }

    @Override
    public List<Stops> findNear(double latitude, double longitude, double radiusKm) {
        List<Stops> stops = new ArrayList<>();
        double latDiff = radiusKm / 111.0;
        double lonDiff = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));

        String sql = """
            SELECT * FROM stops
            WHERE isActive = true
              AND latitude BETWEEN ? AND ?
              AND longitude BETWEEN ? AND ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, latitude - latDiff);
            stmt.setDouble(2, latitude + latDiff);
            stmt.setDouble(3, longitude - lonDiff);
            stmt.setDouble(4, longitude + lonDiff);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) stops.add(mapRowToStop(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find nearby stops", e);
        }
        return stops;
    }

    // --- HELPER METHODS ---
    private Stops mapRowToStop(ResultSet rs) throws SQLException {
        return new Stops(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("latitude"),
                rs.getDouble("longitude"),
                rs.getBoolean("isActive")
        );
    }
}
