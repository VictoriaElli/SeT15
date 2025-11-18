package port.outbound;

import domain.model.OperationMessage;
import domain.model.Route;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationMessageRepositoryPort extends CRUDRepositoryPort<OperationMessage> {

    // --- Finn alle meldinger for en rute ---
    List<OperationMessage> findByRoute(Route route);

    // --- Finn alle aktive meldinger for en rute ---
    List<OperationMessage> findActiveByRoute(Route route);

    // --- Finn meldinger som er gyldige på et bestemt tidspunkt ---
    List<OperationMessage> findByRouteAndTime(Route route, LocalDateTime time);

    // --- Finn alle meldinger som er aktive akkurat nå ---
    List<OperationMessage> findActiveNow();

    // --- Finn meldinger i et tidsintervall ---
    List<OperationMessage> findByRouteAndInterval(Route route, LocalDateTime from, LocalDateTime to);

    // --- Oppdater status (aktiv/inaktiv) ---
    void setActiveStatus(int id, boolean active);
}
