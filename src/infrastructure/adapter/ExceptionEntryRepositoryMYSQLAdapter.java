package adapter;

import domain.model.*;
import port.outbound.ExceptionEntryRepositoryPort;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ExceptionEntryRepositoryMYSQLAdapter implements ExceptionEntryRepositoryPort {

    private final Connection connection;
    private final RouteRepositoryMYSQLAdapter routeRepo;
    private final StopsRepositoryMYSQLAdapter stopsRepo;
    private final SeasonRepositoryMYSQLAdapter seasonRepo;
    private final OperationMessageRepositoryMYSQLAdapter msgRepo;

    public ExceptionEntryRepositoryMYSQLAdapter(Connection connection,
                                                RouteRepositoryMYSQLAdapter routeRepo,
                                                StopsRepositoryMYSQLAdapter stopsRepo,
                                                SeasonRepositoryMYSQLAdapter seasonRepo,
                                                OperationMessageRepositoryMYSQLAdapter msgRepo) {
        this.connection = connection;
        this.routeRepo = routeRepo;
        this.stopsRepo = stopsRepo;
        this.seasonRepo = seasonRepo;
        this.msgRepo = msgRepo;
    }

    // --- CRUD METHODS ---
    @Override
    public void create(ExceptionEntry entry) {
        String sql = "INSERT INTO exceptionEntry " +
                "(routeId, stopId, validDate, weekday, seasonId, departureTime, type, isActive, operationMessageId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entry.getRoute().getId());
            stmt.setObject(2, entry.getStop() != null ? entry.getStop().getId() : null);
            stmt.setDate(3, entry.getValidDate() != null ? java.sql.Date.valueOf(entry.getValidDate()) : null);
            stmt.setString(4, entry.getWeekday() != null ? entry.getWeekday().name() : null);
            stmt.setObject(5, entry.getSeason() != null ? entry.getSeason().getId() : null);
            stmt.setTime(6, Time.valueOf(entry.getDepartureTime()));
            stmt.setString(7, entry.getType() != null ? entry.getType().name() : null);
            stmt.setBoolean(8, entry.isActive());
            stmt.setObject(9, entry.getOperationMessage() != null ? entry.getOperationMessage().getId() : null);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) entry.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExceptionEntry> readById(int id) {
        String sql = "SELECT * FROM exceptionEntry WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRowToEntry(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<ExceptionEntry> readAll() {
        String sql = "SELECT * FROM exceptionEntry ORDER BY departureTime";
        return executeQueryList(sql, null);
    }

    @Override
    public void update(ExceptionEntry entry) {
        String sql = "UPDATE exceptionEntry SET routeId=?, stopId=?, validDate=?, weekday=?, seasonId=?, " +
                "departureTime=?, type=?, isActive=?, operationMessageId=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entry.getRoute().getId());
            stmt.setObject(2, entry.getStop() != null ? entry.getStop().getId() : null);
            stmt.setDate(3, entry.getValidDate() != null ? java.sql.Date.valueOf(entry.getValidDate()) : null);
            stmt.setString(4, entry.getWeekday() != null ? entry.getWeekday().name() : null);
            stmt.setObject(5, entry.getSeason() != null ? entry.getSeason().getId() : null);
            stmt.setTime(6, Time.valueOf(entry.getDepartureTime()));
            stmt.setString(7, entry.getType() != null ? entry.getType().name() : null);
            stmt.setBoolean(8, entry.isActive());
            stmt.setObject(9, entry.getOperationMessage() != null ? entry.getOperationMessage().getId() : null);
            stmt.setInt(10, entry.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ExceptionEntry entry) {
        deleteById(entry.getId());
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM exceptionEntry WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // --- Finn etter route ---
    @Override
    public List<ExceptionEntry> findByRoute(Route route) {
        if (route == null) return List.of();
        String sql = "SELECT * FROM exceptionEntry WHERE routeId=? ORDER BY departureTime";
        return executeQueryList(sql, stmt -> stmt.setInt(1, route.getId()));
    }

    @Override
    public List<ExceptionEntry> findByRouteAndDate(Route route, LocalDate date) {
        if (date == null) return List.of();
        String sql = "SELECT * FROM exceptionEntry WHERE validDate=?" + (route != null ? " AND routeId=?" : "") + " ORDER BY departureTime";
        return executeQueryList(sql, stmt -> {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            if (route != null) stmt.setInt(2, route.getId());
        });
    }

    @Override
    public List<ExceptionEntry> findByRouteAndWeekday(Route route, Weekday weekday) {
        if (weekday == null) return List.of();
        String sql = "SELECT * FROM exceptionEntry WHERE weekday=?" + (route != null ? " AND routeId=?" : "") + " ORDER BY departureTime";
        return executeQueryList(sql, stmt -> {
            stmt.setString(1, weekday.name());
            if (route != null) stmt.setInt(2, route.getId());
        });
    }

    // --- Finn etter stop ---
    @Override
    public List<ExceptionEntry> findByStop(Stops stop) {
        if (stop == null) return List.of();
        String sql = "SELECT * FROM exceptionEntry WHERE stopId=? ORDER BY departureTime";
        return executeQueryList(sql, stmt -> stmt.setInt(1, stop.getId()));
    }

    @Override
    public List<ExceptionEntry> findByRouteAndStop(Route route, Stops stop) {
        if (route == null && stop == null) return List.of();
        String sql = "SELECT * FROM exceptionEntry WHERE 1=1" +
                (route != null ? " AND routeId=?" : "") +
                (stop != null ? " AND stopId=?" : "") +
                " ORDER BY departureTime";
        return executeQueryList(sql, stmt -> {
            int index = 1;
            if (route != null) stmt.setInt(index++, route.getId());
            if (stop != null) stmt.setInt(index, stop.getId());
        });
    }

    @Override
    public List<ExceptionEntry> findByStopAndDate(Stops stop, LocalDate date) {
        if (date == null) return List.of();
        String sql = "SELECT * FROM exceptionEntry WHERE validDate=?" + (stop != null ? " AND stopId=?" : "") + " ORDER BY departureTime";
        return executeQueryList(sql, stmt -> {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            if (stop != null) stmt.setInt(2, stop.getId());
        });
    }

    @Override
    public List<ExceptionEntry> findByStopAndWeekday(Stops stop, Weekday weekday) {
        if (weekday == null) return List.of();
        String sql = "SELECT * FROM exceptionEntry WHERE weekday=?" + (stop != null ? " AND stopId=?" : "") + " ORDER BY departureTime";
        return executeQueryList(sql, stmt -> {
            stmt.setString(1, weekday.name());
            if (stop != null) stmt.setInt(2, stop.getId());
        });
    }

    // --- Finn alle på dato ---
    @Override
    public List<ExceptionEntry> findAllOnDate(LocalDate date) {
        return findAllForDate(date,
                d -> findByRouteAndDate(null, d),
                wd -> findByRouteAndWeekday(null, wd),
                null);
    }

    @Override
    public List<ExceptionEntry> findAllActiveOnDate(LocalDate date) {
        return findAllForDate(date,
                d -> findByRouteAndDate(null, d),
                wd -> findByRouteAndWeekday(null, wd),
                ExceptionEntry::isActive);
    }

    @Override
    public List<ExceptionEntry> findAllInactiveOnDate(LocalDate date) {
        return findAllForDate(date,
                d -> findByRouteAndDate(null, d),
                wd -> findByRouteAndWeekday(null, wd),
                e -> !e.isActive());
    }

    // --- Finn alle på ukedag ---
    @Override
    public List<ExceptionEntry> findAllOnWeekday(Weekday weekday) {
        return findAllForWeekday(weekday, wd -> findByRouteAndWeekday(null, wd), null);
    }

    @Override
    public List<ExceptionEntry> findAllActiveOnWeekday(Weekday weekday) {
        return findAllForWeekday(weekday, wd -> findByRouteAndWeekday(null, wd), ExceptionEntry::isActive);
    }

    @Override
    public List<ExceptionEntry> findAllInactiveOnWeekday(Weekday weekday) {
        return findAllForWeekday(weekday, wd -> findByRouteAndWeekday(null, wd), e -> !e.isActive());
    }

    // --- Aktive/inaktive for route + dato ---
    @Override
    public List<ExceptionEntry> findActiveForRouteAndDate(Route route, LocalDate date) {
        return findAllForDate(date, d -> findByRouteAndDate(route, d), wd -> findByRouteAndWeekday(route, wd), ExceptionEntry::isActive);
    }

    @Override
    public List<ExceptionEntry> findInactiveForRouteAndDate(Route route, LocalDate date) {
        return findAllForDate(date, d -> findByRouteAndDate(route, d), wd -> findByRouteAndWeekday(route, wd), e -> !e.isActive());
    }

    // --- Aktive/inaktive for stop + dato ---
    @Override
    public List<ExceptionEntry> findActiveForStopAndDate(Stops stop, LocalDate date) {
        return findAllForDate(date, d -> findByStopAndDate(stop, d), wd -> findByStopAndWeekday(stop, wd), ExceptionEntry::isActive);
    }

    @Override
    public List<ExceptionEntry> findInactiveForStopAndDate(Stops stop, LocalDate date) {
        return findAllForDate(date, d -> findByStopAndDate(stop, d), wd -> findByStopAndWeekday(stop, wd), e -> !e.isActive());
    }

    // --- Aktive/inaktive for route + ukedag ---
    @Override
    public List<ExceptionEntry> findActiveForRouteAndWeekday(Route route, Weekday weekday) {
        return findAllForWeekday(weekday, wd -> findByRouteAndWeekday(route, wd), ExceptionEntry::isActive);
    }

    @Override
    public List<ExceptionEntry> findInactiveForRouteAndWeekday(Route route, Weekday weekday) {
        return findAllForWeekday(weekday, wd -> findByRouteAndWeekday(route, wd), e -> !e.isActive());
    }

    // --- Aktive/inaktive for stop + ukedag ---
    @Override
    public List<ExceptionEntry> findActiveForStopAndWeekday(Stops stop, Weekday weekday) {
        return findAllForWeekday(weekday, wd -> findByStopAndWeekday(stop, wd), ExceptionEntry::isActive);
    }

    @Override
    public List<ExceptionEntry> findInactiveForStopAndWeekday(Stops stop, Weekday weekday) {
        return findAllForWeekday(weekday, wd -> findByStopAndWeekday(stop, wd), e -> !e.isActive());
    }

    // --- Finn etter season ---
    @Override
    public List<ExceptionEntry> findBySeason(Season season) {
        if (season == null) return List.of();
        String sql = "SELECT * FROM exceptionEntry WHERE seasonId=? ORDER BY departureTime";
        return executeQueryList(sql, stmt -> stmt.setInt(1, season.getId()));
    }

    // --- HELPER METHODS ---
    private List<ExceptionEntry> findAllForDate(
            LocalDate date,
            Function<LocalDate, List<ExceptionEntry>> byDateFetcher,
            Function<Weekday, List<ExceptionEntry>> byWeekdayFetcher,
            Predicate<ExceptionEntry> filter) {

        List<ExceptionEntry> byDate = date != null ? new ArrayList<>(byDateFetcher.apply(date)) : new ArrayList<>();
        Weekday weekday = date != null ? Weekday.fromLocalDate(date) : null;
        List<ExceptionEntry> byWeekday = weekday != null ? new ArrayList<>(byWeekdayFetcher.apply(weekday)) : new ArrayList<>();

        if (filter != null) {
            byDate.removeIf(filter.negate());
            byWeekday.removeIf(filter.negate());
        }

        List<ExceptionEntry> result = new ArrayList<>(byDate);
        result.addAll(byWeekday);
        return result;
    }

    private List<ExceptionEntry> findAllForWeekday(
            Weekday weekday,
            Function<Weekday, List<ExceptionEntry>> fetcher,
            Predicate<ExceptionEntry> filter) {

        if (weekday == null) return List.of();
        List<ExceptionEntry> list = new ArrayList<>(fetcher.apply(weekday));
        if (filter != null) list.removeIf(filter.negate());
        return list;
    }

    private ExceptionEntry mapRowToEntry(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");

        int routeId = rs.getInt("routeId");
        Route route = routeRepo.readById(routeId).orElseThrow(() -> new RuntimeException("Route not found: " + routeId));

        Integer stopId = rs.getObject("stopId", Integer.class);
        Stops stop = stopId != null ? stopsRepo.readById(stopId).orElse(null) : null;

        java.sql.Date sqlDate = rs.getDate("validDate");
        LocalDate validDate = sqlDate != null ? sqlDate.toLocalDate() : null;
        String weekdayStr = rs.getString("weekday");
        Weekday weekday = weekdayStr != null ? Weekday.valueOf(weekdayStr) : null;

        Integer seasonId = rs.getObject("seasonId", Integer.class);
        Season season = seasonId != null ? seasonRepo.readById(seasonId).orElse(null) : null;

        Time sqlTime = rs.getTime("departureTime");
        LocalTime departureTime = sqlTime != null ? sqlTime.toLocalTime() : null;

        String typeStr = rs.getString("type");
        ExceptionType type = typeStr != null ? ExceptionType.valueOf(typeStr) : null;

        boolean isActive = rs.getBoolean("isActive");

        Integer msgId = rs.getObject("operationMessageId", Integer.class);
        OperationMessage msg = msgId != null ? msgRepo.readById(msgId).orElse(null) : null;

        return new ExceptionEntry.Builder()
                .setId(id)
                .setRoute(route)
                .setStop(stop)
                .setValidDate(validDate)
                .setWeekday(weekday)
                .setSeason(season)
                .setDepartureTime(departureTime)
                .setType(type)
                .setIsActive(isActive)
                .setOperationMessage(msg)
                .build();
    }

    private List<ExceptionEntry> executeQueryList(String sql, SQLConsumer<PreparedStatement> setter) {
        List<ExceptionEntry> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (setter != null) setter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRowToEntry(rs));
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
