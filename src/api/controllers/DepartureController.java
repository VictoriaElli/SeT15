package controllers;

import dto.DepartureRequestDTO;
import dto.DepartureResponseDTO;
import dto.ScheduleDTO;
import org.springframework.web.bind.annotation.*;
import service.ScheduleService;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/departures")
@CrossOrigin(origins = "*")
public class DepartureController {

    private final ScheduleService scheduleService;

    public DepartureController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/search")
    public List<DepartureResponseDTO> getDepartures(@RequestBody DepartureRequestDTO request) {
        return scheduleService.getDepartures(request);
    }

    @GetMapping("/full-schedule")
    public List<ScheduleDTO> getFullSchedule(
            @RequestParam(required = false) String date
    ) {
        LocalDate targetDate;

        if (date == null) {
            targetDate = LocalDate.now(); // default = i dag
        } else {
            targetDate = LocalDate.parse(date); // forventer format yyyy-MM-dd
        }

        return scheduleService.getFullSchedule(targetDate);
    }




}
