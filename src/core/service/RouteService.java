package service;

import domain.model.Route;
import domain.model.RouteStops;
import domain.model.Stops;
import dto.RouteDTO;
import dto.RouteStopDTO;
import dto.StopDTO;
import org.springframework.stereotype.Service;
import port.outbound.RouteRepositoryPort;
import port.outbound.RouteStopsRepositoryPort;
import port.outbound.StopsRepositoryPort;

import java.util.List;

@Service
public class RouteService {

    private final RouteRepositoryPort routeRepo;
    private final RouteStopsRepositoryPort routeStopsRepo;
    private final StopsRepositoryPort stopsRepo;

    public RouteService(RouteRepositoryPort routeRepo,
                        RouteStopsRepositoryPort routeStopsRepo,
                        StopsRepositoryPort stopsRepo) {
        this.routeRepo = routeRepo;
        this.routeStopsRepo = routeStopsRepo;
        this.stopsRepo = stopsRepo;
    }

    // --- Hent alle stopp som DTO ---
    public List<StopDTO> getAllStopsDTO() {
        return stopsRepo.findAllActive().stream()
                .map(this::toStopDTO)
                .toList();
    }

    // --- Hent alle ruter med stopp som DTO ---
    public List<RouteDTO> getAllRoutesDTO() {
        List<Route> routes = routeRepo.findAllActive();
        List<RouteStops> allRouteStops = routeStopsRepo.findAllActive();

        // Sett alle RouteStops slik at hver Route kan bygge sin interne liste
        for (Route route : routes) {
            route.setAllRouteStops(allRouteStops);
        }

        return routes.stream()
                .map(this::toRouteDTO)
                .toList();
    }

    // --- Hent spesifikk rute som DTO ---
    public RouteDTO getRouteDTOById(int id) {
        return getAllRoutesDTO().stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Route not found"));
    }

    // --- Mapper metoder ---
    public StopDTO toStopDTO(Stops stop) {
        return new StopDTO(stop.getId(), stop.getName());
    }

    public RouteStopDTO toRouteStopDTO(RouteStops rs) {
        return new RouteStopDTO(
                rs.getId(),
                toStopDTO(rs.getStop()),
                rs.getRouteOrder(),
                rs.getTimeFromStart(),
                rs.getDistanceFromPrevious()
        );
    }

    public RouteDTO toRouteDTO(Route route) {
        return new RouteDTO(
                route.getId(),
                route.getRouteNum(),
                toStopDTO(route.getFromStop()),
                toStopDTO(route.getToStop()),
                route.isActive(),
                route.getStops().stream()
                        .map(this::toRouteStopDTO)
                        .toList()
        );
    }
}
