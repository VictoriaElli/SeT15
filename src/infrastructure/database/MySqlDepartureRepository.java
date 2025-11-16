package database;

import port.outbound.DepartureRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MySqlDepartureRepository implements DepartureRepository {

    // private static final String DB_URL =
    // private static final String USERNAME =
    // private static final String PASSWORD =

    @Override
    public List<LocalTime> findDeparturesForRouteAndDate(int routeId, LocalDate date) {

        List<LocalTime> departures = new ArrayList<>();
        return departures;
    }
}
