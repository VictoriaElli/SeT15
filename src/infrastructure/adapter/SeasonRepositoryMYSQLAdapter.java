package adapter;

import domain.model.Season;
import org.springframework.stereotype.Repository;
import port.outbound.SeasonRepositoryPort;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SeasonRepositoryMYSQLAdapter implements SeasonRepositoryPort {

    private final DataSource dataSource;

    public SeasonRepositoryMYSQLAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // --- CRUD METHODS ---
    @Override
    public void create(Season season) {
        String sql = "INSERT INTO season (seasonType, validYear, startDate, endDate) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, season.getSeasonType());
            stmt.setInt(2, season.getValidYear());
            stmt.setDate(3, season.getStartDate() != null ? Date.valueOf(season.getStartDate()) : null);
            stmt.setDate(4, season.getEndDate() != null ? Date.valueOf(season.getEndDate()) : null);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Creating season failed, no rows affected.");

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) season.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Season> readById(int id) {
        String sql = "SELECT * FROM season WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapResultSetToSeason(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Season> readAll() {
        List<Season> seasons = new ArrayList<>();
        String sql = "SELECT * FROM season";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) seasons.add(mapResultSetToSeason(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seasons;
    }

    @Override
    public void update(Season season) {
        String sql = "UPDATE season SET seasonType = ?, validYear = ?, startDate = ?, endDate = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, season.getSeasonType());
            stmt.setInt(2, season.getValidYear());
            stmt.setDate(3, season.getStartDate() != null ? Date.valueOf(season.getStartDate()) : null);
            stmt.setDate(4, season.getEndDate() != null ? Date.valueOf(season.getEndDate()) : null);
            stmt.setInt(5, season.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Season season) {
        deleteById(season.getId());
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM season WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Season> findActiveByDate(LocalDate date) {
        List<Season> seasons = new ArrayList<>();
        String sql = "SELECT * FROM season WHERE startDate <= ? AND endDate >= ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                seasons.add(mapResultSetToSeason(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seasons;
    }

    // --- HELPER METHODS ---
    private Season mapResultSetToSeason(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String seasonType = rs.getString("seasonType");
        int validYear = rs.getInt("validYear");
        LocalDate startDate = rs.getDate("startDate") != null ? rs.getDate("startDate").toLocalDate() : null;
        LocalDate endDate = rs.getDate("endDate") != null ? rs.getDate("endDate").toLocalDate() : null;
        return new Season(id, seasonType, validYear, startDate, endDate);
    }
}
