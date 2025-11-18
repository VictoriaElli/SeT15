package adapter;

import domain.model.Route;
import domain.model.RouteStops;
import domain.model.Stops;
import org.springframework.stereotype.Repository;
import port.outbound.RouteStopsRepositoryPort;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RouteStopsRepositoryMYSQLAdapter implements RouteStopsRepositoryPort {

    private final DataSource dataSource;
    private final RouteRepositoryMYSQLAdapter routeRepo;
    private final StopsRepositoryMYSQLAdapter stopsRepo;

    public RouteStopsRepositoryMYSQLAdapter(DataSource dataSource,
                                            RouteRepositoryMYSQLAdapter routeRepo,
                                            StopsRepositoryMYSQLAdapter stopsRepo) {
        this.dataSource = dataSource;
        this.routeRepo = routeRepo;
        this.stopsRepo = stopsRepo;
    }

    // --- CRUD METHODS ---
    @Override
    public void create(RouteStops entity) {
        String sql = "INSERT INTO routeStops (routeId, stopId, routeOrder, timeFromStart, distanceFromPrevious) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, entity.getRoute().getId());
            stmt.setInt(2, entity.getStop().getId());
            stmt.setInt(3, entity.getRouteOrder());
            stmt.setInt(4, entity.getTimeFromStart());
            stmt.setDouble(5, entity.getDistanceFromPrevious());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) entity.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create RouteStops", e);
        }
    }

    @Override
    public Optional<RouteStops> readById(int id) {
        String sql = "SELECT * FROM routeStops WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRowToRouteStop(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read RouteStops by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<RouteStops> readAll() {
        List<RouteStops> list = new ArrayList<>();
        String sql = "SELECT * FROM routeStops";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapRowToRouteStop(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read all RouteStops", e);
        }
        return list;
    }

    @Override
    public void update(RouteStops entity) {
        String sql = "UPDATE routeStops SET routeId = ?, stopId = ?, routeOrder = ?, timeFromStart = ?, distanceFromPrevious = ? " +
                "WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entity.getRoute().getId());
            stmt.setInt(2, entity.getStop().getId());
            stmt.setInt(3, entity.getRouteOrder());
            stmt.setInt(4, entity.getTimeFromStart());
            stmt.setDouble(5, entity.getDistanceFromPrevious());
            stmt.setInt(6, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update RouteStops", e);
        }
    }

    @Override
    public void delete(RouteStops entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM routeStops WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete RouteStops", e);
        }
    }

    @Override
    public List<RouteStops> findAllActive() {
        List<RouteStops> list = new ArrayList<>();
        String sql = """
            SELECT rs.* FROM routeStops rs
            JOIN route r ON rs.routeId = r.id
            JOIN stops s ON rs.stopId = s.id
            WHERE r.isActive = TRUE AND s.isActive = TRUE
        """;
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapRowToRouteStop(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all active RouteStops", e);
        }
        return list;
    }

    @Override
    public List<RouteStops> findByRoute(Route route) {
        List<RouteStops> list = new ArrayList<>();
        String sql = "SELECT * FROM routeStops WHERE routeId = ? ORDER BY routeOrder";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, route.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRowToRouteStop(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find RouteStops by Route", e);
        }
        return list;
    }

    @Override
    public List<RouteStops> findByStop(Stops stop) {
        List<RouteStops> list = new ArrayList<>();
        String sql = "SELECT * FROM routeStops WHERE stopId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, stop.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRowToRouteStop(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find RouteStops by Stop", e);
        }
        return list;
    }

    // --- HELPER METHODS ---
    private RouteStops mapRowToRouteStop(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int routeId = rs.getInt("routeId");
        int stopId = rs.getInt("stopId");
        int routeOrder = rs.getInt("routeOrder");
        int timeFromStart = rs.getInt("timeFromStart");
        double distanceFromPrevious = rs.getDouble("distanceFromPrevious");

        Route route = routeRepo.readById(routeId)
                .orElseThrow(() -> new SQLException("Route with ID " + routeId + " not found"));
        Stops stop = stopsRepo.readById(stopId)
                .orElseThrow(() -> new SQLException("Stop with ID " + stopId + " not found"));

        return new RouteStops(id, route, stop, routeOrder, timeFromStart, distanceFromPrevious);
    }
}
