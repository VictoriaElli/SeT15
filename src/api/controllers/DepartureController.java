package controllers;

import dto.DepartureRequestDTO;
import dto.DepartureResponseDTO;
import org.springframework.web.bind.annotation.*;
import service.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/departures")
public class DepartureController {

    private final ScheduleService scheduleService;

    public DepartureController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public List<DepartureResponseDTO> getDepartures(@RequestBody DepartureRequestDTO request) {
        return scheduleService.getDepartures(request);
    }
}
