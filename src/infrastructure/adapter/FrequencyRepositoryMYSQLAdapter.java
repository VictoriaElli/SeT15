package adapter;

import domain.model.*;
import org.springframework.stereotype.Repository;
import port.outbound.FrequencyRepositoryPort;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FrequencyRepositoryMYSQLAdapter implements FrequencyRepositoryPort {

    private final DataSource dataSource;
    private final RouteRepositoryMYSQLAdapter routeRepo;
    private final SeasonRepositoryMYSQLAdapter seasonRepo;

    public FrequencyRepositoryMYSQLAdapter(DataSource dataSource,
                                           RouteRepositoryMYSQLAdapter routeRepo,
                                           SeasonRepositoryMYSQLAdapter seasonRepo) {
        this.dataSource = dataSource;
        this.routeRepo = routeRepo;
        this.seasonRepo = seasonRepo;
    }

    // --- CRUD METHODS ---
    @Override
    public void create(Frequency freq) {
        String sql = "INSERT INTO frequency (routeId, weekday, seasonId, firstDeparture, lastDeparture, intervalMinutes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, freq.getRoute().getId());
            stmt.setString(2, freq.getWeekday().name());
            stmt.setInt(3, freq.getSeason().getId());
            stmt.setTime(4, Time.valueOf(freq.getFirstDeparture()));
            stmt.setTime(5, Time.valueOf(freq.getLastDeparture()));
            stmt.setInt(6, freq.getIntervalMinutes());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) freq.setId(keys.getInt(1));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Frequency> readById(int id) {
        String sql = "SELECT * FROM frequency WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToFrequency(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Frequency> readAll() {
        String sql = "SELECT * FROM frequency ORDER BY firstDeparture";
        return executeQueryList(sql, null);
    }

    @Override
    public void update(Frequency freq) {
        String sql = "UPDATE frequency SET routeId=?, weekday=?, seasonId=?, firstDeparture=?, lastDeparture=?, intervalMinutes=? WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, freq.getRoute().getId());
            stmt.setString(2, freq.getWeekday().name());
            stmt.setInt(3, freq.getSeason().getId());
            stmt.setTime(4, Time.valueOf(freq.getFirstDeparture()));
            stmt.setTime(5, Time.valueOf(freq.getLastDeparture()));
            stmt.setInt(6, freq.getIntervalMinutes());
            stmt.setInt(7, freq.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Frequency freq) {
        deleteById(freq.getId());
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM frequency WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // --- Finn frekvenser etter route ---
    @Override
    public List<Frequency> findByRoute(Route route) {
        if (route == null) return List.of();
        String sql = "SELECT * FROM frequency WHERE routeId=? ORDER BY firstDeparture";
        return executeQueryList(sql, stmt -> stmt.setInt(1, route.getId()));
    }

    @Override
    public List<Frequency> findByRouteAndDate(Route route, LocalDate date) {
        if (date == null) return List.of();
        return findByRouteAndWeekday(route, Weekday.fromLocalDate(date));
    }

    @Override
    public List<Frequency> findByRouteAndWeekday(Route route, Weekday weekday) {
        if (weekday == null) return List.of();
        String sql = "SELECT * FROM frequency WHERE weekday=?" + (route != null ? " AND routeId=?" : "") + " ORDER BY firstDeparture";
        return executeQueryList(sql, stmt -> {
            stmt.setString(1, weekday.name());
            if (route != null) stmt.setInt(2, route.getId());
        });
    }

    // --- Finn alle frekvenser på en dato ---
    @Override
    public List<Frequency> findAllOnDate(LocalDate date) {
        return findByRouteAndWeekday(null, Weekday.fromLocalDate(date));
    }

    @Override
    public List<Frequency> findAllActiveOnDate(LocalDate date) {
        return findActiveForRouteAndDate(null, date);
    }

    @Override
    public List<Frequency> findAllInactiveOnDate(LocalDate date) {
        return findInactiveForRouteAndDate(null, date);
    }

    // --- Finn alle frekvenser på en ukedag ---
    @Override
    public List<Frequency> findAllOnWeekday(Weekday weekday) {
        return findByRouteAndWeekday(null, weekday);
    }

    @Override
    public List<Frequency> findAllActiveOnWeekday(Weekday weekday) {
        String sql = "SELECT f.* FROM frequency f JOIN route r ON f.routeId=r.id WHERE f.weekday=? AND r.isActive=TRUE ORDER BY f.firstDeparture";
        return executeQueryList(sql, stmt -> stmt.setString(1, weekday.name()));
    }

    @Override
    public List<Frequency> findAllInactiveOnWeekday(Weekday weekday) {
        String sql = "SELECT f.* FROM frequency f JOIN route r ON f.routeId=r.id WHERE f.weekday=? AND r.isActive=FALSE ORDER BY f.firstDeparture";
        return executeQueryList(sql, stmt -> stmt.setString(1, weekday.name()));
    }

    @Override
    public List<Frequency> findActiveForRouteAndDate(Route route, LocalDate date) {
        return findByRouteAndDateActiveStatus(route, date, true);
    }

    @Override
    public List<Frequency> findInactiveForRouteAndDate(Route route, LocalDate date) {
        return findByRouteAndDateActiveStatus(route, date, false);
    }

    private List<Frequency> findByRouteAndDateActiveStatus(Route route, LocalDate date, boolean active) {
        if (date == null) return List.of();
        Weekday weekday = Weekday.fromLocalDate(date);
        String sql = "SELECT f.* FROM frequency f JOIN route r ON f.routeId=r.id WHERE f.weekday=? AND r.isActive=?" + (route != null ? " AND r.id=?" : "") + " ORDER BY f.firstDeparture";
        return executeQueryList(sql, stmt -> {
            stmt.setString(1, weekday.name());
            stmt.setBoolean(2, active);
            if (route != null) stmt.setInt(3, route.getId());
        });
    }

    // --- Finn frekvenser etter season ---
    @Override
    public List<Frequency> findBySeason(Season season) {
        if (season == null) return List.of();
        String sql = "SELECT * FROM frequency WHERE seasonId=? ORDER BY firstDeparture";
        return executeQueryList(sql, stmt -> stmt.setInt(1, season.getId()));
    }

    // --- HELPER METHODS ---
    private Frequency mapRowToFrequency(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");

        int routeId = rs.getInt("routeId");
        Route route = routeRepo.readById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found: " + routeId));

        Weekday weekday = Weekday.valueOf(rs.getString("weekday"));

        int seasonId = rs.getInt("seasonId");
        Season season = seasonRepo.readById(seasonId)
                .orElseThrow(() -> new RuntimeException("Season not found: " + seasonId));

        LocalTime first = rs.getTime("firstDeparture").toLocalTime();
        LocalTime last = rs.getTime("lastDeparture").toLocalTime();
        int interval = rs.getInt("intervalMinutes");

        return new Frequency(id, route, weekday, season, first, last, interval);
    }

    private List<Frequency> executeQueryList(String sql, SQLConsumer<PreparedStatement> setter) {
        List<Frequency> list = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            if (setter != null) setter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRowToFrequency(rs));
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
