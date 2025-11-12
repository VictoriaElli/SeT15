package controllers;

import domain.model.*;
import domain.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/{routeId}")
    public Schedule getSchedule(@PathVariable int routeId,
                                @RequestParam String date) {
        return scheduleService.buildSchedule(routeId, LocalDate.parse(date));
    }

    @PutMapping("/{routeId}")
    public Schedule updateSchedule(@PathVariable int routeId,
                                   @RequestParam String date,
                                   @RequestBody UpdateRequest updateRequest) {
        return scheduleService.updateSchedule(routeId, LocalDate.parse(date),
                updateRequest.getFrequencies(),
                updateRequest.getExceptions());
    }

    public static class UpdateRequest {
        private List<Frequency> frequencies;
        private List<ExceptionEntry> exceptions;
        public List<Frequency> getFrequencies() { return frequencies; }
        public void setFrequencies(List<Frequency> frequencies) { this.frequencies = frequencies; }
        public List<ExceptionEntry> getExceptions() { return exceptions; }
        public void setExceptions(List<ExceptionEntry> exceptions) { this.exceptions = exceptions; }
    }
}