package port.outbound;

import domain.model.Frequency;
import domain.model.Route;
import domain.model.Stop;


import java.util.List;

public interface FrequencyRepository {
    List<Frequency> findByRouteAndStop(Route route, Stop stop);
    Frequency findById(int id);
    Frequency save(Frequency frequency);
    void deleteById(int id);
}
