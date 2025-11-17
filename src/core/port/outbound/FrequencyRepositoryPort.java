package port.outbound;

import domain.model.Frequency;
import domain.model.Route;
import domain.model.Season;
import domain.model.Weekday;

import java.time.LocalDate;
import java.util.List;

public interface FrequencyRepositoryPort extends CRUDRepositoryPort<Frequency> {
    List<Frequency> findByRoute(Route route);
    List<Frequency> findByRouteAndDate(Route route, LocalDate date);
    List<Frequency> findByRouteAndWeekday(Route route, Weekday weekday);

    // --- Finn alle frekvenser på en dato ---
    List<Frequency> findAllOnDate(LocalDate date);
    List<Frequency> findAllActiveOnDate(LocalDate date);
    List<Frequency> findAllInactiveOnDate(LocalDate date);

    // --- Finn alle frekvenser på en ukedag ---
    List<Frequency> findAllOnWeekday(Weekday weekday);
    List<Frequency> findAllActiveOnWeekday(Weekday weekday);
    List<Frequency> findAllInactiveOnWeekday(Weekday weekday);

    List<Frequency> findActiveForRouteAndDate(Route route, LocalDate date);
    List<Frequency> findInactiveForRouteAndDate(Route route, LocalDate date);

    List<Frequency> findBySeason(Season season);

}
