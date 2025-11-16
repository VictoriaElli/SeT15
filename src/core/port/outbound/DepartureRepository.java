package port.outbound;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Et interface som beskriver hvilke data søket trenger
// Finn alle avganger for gitt rute og dato
public interface DepartureRepository {
    List<LocalTime> findDeparturesForRouteAndDate(int routeId, LocalDate date);
}
