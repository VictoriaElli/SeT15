package adapter;

import domain.model.Route;
import domain.model.RouteStops;
import domain.model.Stops;
import port.outbound.RouteStopsRepositoryPort;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RouteStopsRepositoryMYSQLAdapter implements RouteStopsRepositoryPort {

    private final Connection connection;
    private final RouteRepositoryMYSQLAdapter routeRepo;
    private final StopsRepositoryMYSQLAdapter stopsRepo;

    public RouteStopsRepositoryMYSQLAdapter(Connection connection,
                                            RouteRepositoryMYSQLAdapter routeRepo,
                                            StopsRepositoryMYSQLAdapter stopsRepo) {
        this.connection = connection;
        this.routeRepo = routeRepo;
        this.stopsRepo = stopsRepo;
    }

    // --- CRUD METHODS ---
    @Override
    public void create(RouteStops entity) {
        String sql = "INSERT INTO routeStops (routeId, stopId, routeOrder, timeFromStart, distanceFromPrevious) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entity.getRoute().getId());
            stmt.setInt(2, entity.getStop().getId());
            stmt.setInt(3, entity.getRouteOrder());
            stmt.setInt(4, entity.getTimeFromStart());
            stmt.setDouble(5, entity.getDistanceFromPrevious());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<RouteStops> readById(int id) {
        String sql = "SELECT * FROM routeStops WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToRouteStop(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<RouteStops> readAll() {
        List<RouteStops> list = new ArrayList<>();
        String sql = "SELECT * FROM routeStops";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapRowToRouteStop(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void update(RouteStops entity) {
        String sql = "UPDATE routeStops SET routeId = ?, stopId = ?, routeOrder = ?, timeFromStart = ?, distanceFromPrevious = ? " +
                "WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entity.getRoute().getId());
            stmt.setInt(2, entity.getStop().getId());
            stmt.setInt(3, entity.getRouteOrder());
            stmt.setInt(4, entity.getTimeFromStart());
            stmt.setDouble(5, entity.getDistanceFromPrevious());
            stmt.setInt(6, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(RouteStops entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM routeStops WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapRowToRouteStop(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<RouteStops> findByRoute(Route route) {
        List<RouteStops> list = new ArrayList<>();
        String sql = "SELECT * FROM routeStops WHERE routeId = ? ORDER BY routeOrder";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, route.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToRouteStop(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<RouteStops> findByStop(Stops stop) {
        List<RouteStops> list = new ArrayList<>();
        String sql = "SELECT * FROM routeStops WHERE stopId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, stop.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToRouteStop(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
