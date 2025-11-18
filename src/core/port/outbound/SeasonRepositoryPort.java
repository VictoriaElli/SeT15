package port.outbound;

import domain.model.Season;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeasonRepositoryPort extends CRUDRepositoryPort<Season> {
    List<Season> findActiveByDate(LocalDate date);
}

