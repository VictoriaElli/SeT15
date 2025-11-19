package adapter;

import domain.model.Stops;
import domain.model.environment.DistanceBetweenStops;
import org.springframework.stereotype.Repository;
import port.outbound.StopDistanceRepositoryPort;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StopDistanceRepositoryMYSQLAdapter implements StopDistanceRepositoryPort {

    private final DataSource dataSource;
    private final StopsRepositoryMYSQLAdapter stopsRepo;

    public StopDistanceRepositoryMYSQLAdapter(DataSource dataSource, StopsRepositoryMYSQLAdapter stopsRepo) {
        this.dataSource = dataSource;
        this.stopsRepo = stopsRepo;
    }

    @Override
    public void create(DistanceBetweenStops distance) {
        String sql = "INSERT INTO stopDistance (fromStopId, toStopId, distance, tollgate) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, distance.getFromStop().getId());
            stmt.setInt(2, distance.getToStop().getId());
            stmt.setDouble(3, distance.getDistance());
            stmt.setBoolean(4, distance.isTollgate());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) distance.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create stop distance", e);
        }
    }

    @Override
    public Optional<DistanceBetweenStops> readById(int id) {
        String sql = "SELECT * FROM stopDistance WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRowToDistance(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read stop distance by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<DistanceBetweenStops> readAll() {
        List<DistanceBetweenStops> distances = new ArrayList<>();
        String sql = "SELECT * FROM stopDistance";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) distances.add(mapRowToDistance(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read all stop distances", e);
        }
        return distances;
    }

    @Override
    public void update(DistanceBetweenStops distance) {
        String sql = "UPDATE stopDistance SET fromStopId = ?, toStopId = ?, distance = ?, tollgate = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, distance.getFromStop().getId());
            stmt.setInt(2, distance.getToStop().getId());
            stmt.setDouble(3, distance.getDistance());
            stmt.setBoolean(4, distance.isTollgate());
            stmt.setInt(5, distance.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update stop distance", e);
        }
    }

    @Override
    public void delete(DistanceBetweenStops distance) {
        deleteById(distance.getId());
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM stopDistance WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete stop distance", e);
        }
    }

    @Override
    public DistanceBetweenStops findByFromAndTo(int fromStopId, int toStopId) {
        String sql = "SELECT * FROM stopDistance WHERE fromStopId = ? AND toStopId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fromStopId);
            stmt.setInt(2, toStopId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRowToDistance(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find stop distance by from/to", e);
        }
        return null;
    }

    // --- HELPER ---
    private DistanceBetweenStops mapRowToDistance(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int fromId = rs.getInt("fromStopId");
        int toId = rs.getInt("toStopId");
        double distance = rs.getDouble("distance");
        boolean tollgate = rs.getBoolean("tollgate");

        Stops fromStopObj = stopsRepo.readById(fromId)
                .orElse(new Stops(fromId, "Unknown Stop"));
        Stops toStopObj = stopsRepo.readById(toId)
                .orElse(new Stops(toId, "Unknown Stop"));

        return new DistanceBetweenStops(id, fromStopObj, toStopObj, distance, tollgate);
    }
}
