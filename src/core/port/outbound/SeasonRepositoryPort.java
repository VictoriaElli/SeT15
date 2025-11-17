package port.outbound;

import domain.model.Season;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SeasonRepositoryPort extends CRUDRepositoryPort<Season> {
    List<Season> findActiveByDate(LocalDate date);
}

