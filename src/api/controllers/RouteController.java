package controllers;

import domain.model.*;
import domain.service.RouteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public void addRoute(@RequestBody Route route) {
        routeService.addRoute(route);
    }

    @GetMapping("/{id}")
    public Route getRoute(@PathVariable int id) {
        return routeService.getRoute(id);
    }

    @GetMapping
    public List<Route> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    @PutMapping("/{id}")
    public void updateRoute(@PathVariable int id, @RequestBody Route route) {
        route.setId(id);
        routeService.updateRoute(route);
    }

    @DeleteMapping("/{id}")
    public void deleteRoute(@PathVariable int id) {
        routeService.removeRoute(id);
    }
}