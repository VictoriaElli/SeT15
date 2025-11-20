package adapter;

import domain.model.OperationMessage;
import domain.model.Route;
import org.springframework.stereotype.Repository;
import port.outbound.OperationMessageRepositoryPort;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OperationMessageRepositoryMYSQLAdapter implements OperationMessageRepositoryPort {

    private final DataSource dataSource;
    private final RouteRepositoryMYSQLAdapter routeRepo;

    public OperationMessageRepositoryMYSQLAdapter(DataSource dataSource, RouteRepositoryMYSQLAdapter routeRepo) {
        this.dataSource = dataSource;
        this.routeRepo = routeRepo;
    }

    // --- CRUD METHODS ---
    @Override
    public void create(OperationMessage msg) {
        String sql = "INSERT INTO operationMessage (message, published, isActive, routeId, validFrom, validTo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, msg.getMessage());
            stmt.setTimestamp(2, Timestamp.valueOf(msg.getPublished()));
            stmt.setBoolean(3, msg.isActive());
            stmt.setInt(4, msg.getRoute().getId());
            stmt.setTimestamp(5, Timestamp.valueOf(msg.getValidFrom()));
            stmt.setTimestamp(6, Timestamp.valueOf(msg.getValidTo()));

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) msg.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<OperationMessage> readById(int id) {
        String sql = "SELECT * FROM operationMessage WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRowToMessage(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<OperationMessage> readAll() {
        String sql = "SELECT * FROM operationMessage ORDER BY published DESC";
        return executeQueryList(sql, null);
    }

    @Override
    public void update(OperationMessage msg) {
        String sql = "UPDATE operationMessage SET message=?, isActive=?, routeId=?, validFrom=?, validTo=? WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, msg.getMessage());
            stmt.setBoolean(2, msg.isActive());
            stmt.setInt(3, msg.getRoute().getId());
            stmt.setTimestamp(4, Timestamp.valueOf(msg.getValidFrom()));
            stmt.setTimestamp(5, Timestamp.valueOf(msg.getValidTo()));
            stmt.setInt(6, msg.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(OperationMessage msg) {
        deleteById(msg.getId());
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM operationMessage WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // --- Spørringer ---
    @Override
    public List<OperationMessage> findByRoute(Route route) {
        String sql = "SELECT * FROM operationMessage WHERE routeId=? ORDER BY published DESC";
        return executeQueryList(sql, stmt -> stmt.setInt(1, route.getId()));
    }

    @Override
    public List<OperationMessage> findActiveByRoute(Route route) {
        String sql = "SELECT * FROM operationMessage WHERE routeId=? AND isActive=TRUE ORDER BY published DESC";
        return executeQueryList(sql, stmt -> stmt.setInt(1, route.getId()));
    }

    @Override
    public List<OperationMessage> findByRouteAndTime(Route route, LocalDateTime time) {
        String sql = "SELECT * FROM operationMessage WHERE routeId=? AND validFrom<=? AND validTo>=? ORDER BY published DESC";
        return executeQueryList(sql, stmt -> {
            stmt.setInt(1, route.getId());
            stmt.setTimestamp(2, Timestamp.valueOf(time));
            stmt.setTimestamp(3, Timestamp.valueOf(time));
        });
    }

    @Override
    public List<OperationMessage> findActiveNow() {
        String sql = "SELECT * FROM operationMessage WHERE isActive=TRUE AND validFrom<=NOW() AND validTo>=NOW() ORDER BY published DESC";
        return executeQueryList(sql, null);
    }

    @Override
    public List<OperationMessage> findByRouteAndInterval(Route route, LocalDateTime from, LocalDateTime to) {
        String sql = "SELECT * FROM operationMessage WHERE routeId=? AND validFrom<=? AND validTo>=? ORDER BY published DESC";
        return executeQueryList(sql, stmt -> {
            stmt.setInt(1, route.getId());
            stmt.setTimestamp(2, Timestamp.valueOf(to));
            stmt.setTimestamp(3, Timestamp.valueOf(from));
        });
    }

    @Override
    public void setActiveStatus(int id, boolean active) {
        String sql = "UPDATE operationMessage SET isActive=? WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, active);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // --- HELPER METHODS ---
    private OperationMessage mapRowToMessage(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String message = rs.getString("message");
        LocalDateTime published = rs.getTimestamp("published").toLocalDateTime();
        boolean isActive = rs.getBoolean("isActive");
        int routeId = rs.getInt("routeId");
        LocalDateTime validFrom = rs.getTimestamp("validFrom").toLocalDateTime();
        LocalDateTime validTo = rs.getTimestamp("validTo").toLocalDateTime();

        // Hent full Route fra RouteRepository
        Route route = routeRepo.readById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found for OperationMessage: " + routeId));

        return new OperationMessage(id, message, published, route, validFrom, validTo);
    }

    private List<OperationMessage> executeQueryList(String sql, SQLConsumer<PreparedStatement> setter) {
        List<OperationMessage> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (setter != null) setter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToMessage(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query: " + sql, e);
        }
        return list;
    }

    @FunctionalInterface
    private interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }
}
