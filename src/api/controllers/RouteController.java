package controllers;

import dto.RouteDTO;
import dto.StopDTO;
import org.springframework.web.bind.annotation.*;
import service.RouteService;


import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/stops")
    public List<StopDTO> getAllStops() {
        return routeService.getAllStopsDTO();
    }

    @GetMapping("/routes")
    public List<RouteDTO> getAllRoutes() {
        return routeService.getAllRoutesDTO();
    }

    @GetMapping("/routes/{id}")
    public RouteDTO getRouteById(@PathVariable int id) {
        return routeService.getRouteDTOById(id);
    }
}
