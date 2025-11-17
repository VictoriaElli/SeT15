package adapter;

import domain.model.Route;
import domain.model.Stops;
import port.outbound.RouteRepositoryPort;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RouteRepositoryMYSQLAdapter implements RouteRepositoryPort {

    private final Connection connection;
    private final StopsRepositoryMYSQLAdapter stopsRepo;

    public RouteRepositoryMYSQLAdapter(Connection connection) {
        this.connection = connection;
        this.stopsRepo = new StopsRepositoryMYSQLAdapter(connection);
    }

    // --- CRUD METHODS ---
    @Override
    public void create(Route route) {
        String sql = "INSERT INTO route (num, fromStopId, toStopId, isActive) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, route.getRouteNum());
            stmt.setInt(2, route.getFromStop().getId());
            stmt.setInt(3, route.getToStop().getId());
            stmt.setBoolean(4, route.isActive());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                route.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create route", e);
        }
    }

    @Override
    public Optional<Route> readById(int id) {
        String sql = "SELECT * FROM route WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToRoute(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read route by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Route> readAll() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT * FROM route";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                routes.add(mapRowToRoute(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read all routes", e);
        }
        return routes;
    }

    @Override
    public void update(Route route) {
        String sql = "UPDATE route SET num = ?, fromStopId = ?, toStopId = ?, isActive = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, route.getRouteNum());
            stmt.setInt(2, route.getFromStop().getId());
            stmt.setInt(3, route.getToStop().getId());
            stmt.setBoolean(4, route.isActive());
            stmt.setInt(5, route.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update route", e);
        }
    }

    @Override
    public void delete(Route route) {
        deleteById(route.getId());
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM route WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete route", e);
        }
    }

    @Override
    public Optional<Route> findByRouteName(String routeName) {
        String sql = "SELECT * FROM route WHERE CONCAT(num, ' ', fromStopId, ' - ', toStopId) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, routeName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToRoute(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find route by name", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Route> findAllActive() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT * FROM route WHERE isActive = true";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                routes.add(mapRowToRoute(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all active routes", e);
        }
        return routes;
    }

    // --- HELPER METHODS ---
    private Route mapRowToRoute(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int num = rs.getInt("num");
        int fromStopId = rs.getInt("fromStopId");
        int toStopId = rs.getInt("toStopId");
        boolean isActive = rs.getBoolean("isActive");

        Stops fromStop = stopsRepo.readById(fromStopId)
                .orElse(new Stops(fromStopId, "Unknown Stop"));
        Stops toStop = stopsRepo.readById(toStopId)
                .orElse(new Stops(toStopId, "Unknown Stop"));

        return new Route(id, num, fromStop, toStop, isActive);
    }
}
