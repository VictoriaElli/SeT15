package port.outbound;

import domain.model.ExceptionEntry;
import domain.model.Route;
import domain.model.Stop;

import java.time.LocalDate;
import java.util.List;

public interface ExceptionEntryRepository {
    List<ExceptionEntry> findByRouteAndStop(Route route, Stop stop);
    List<ExceptionEntry> findByRouteStopAndDate(Route route, Stop stop, LocalDate date);
    ExceptionEntry save(ExceptionEntry exceptionEntry);
    void deleteById(int id);
}
