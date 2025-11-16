package controllers;

import domain.model.crud.Schedule;
import domain.model.crud.ScheduleManager;

import java.time.LocalTime;
import java.util.ArrayList;

// Dette er en controller som skal bruke klassen ScheduleManager for å håndtere fergetider
// ScheduleController har metoder for å lage, hente, oppdatere og slette fergetider
// Og det brukes for å koble frontend med backend sammen
public class ScheduleController {

    private ScheduleManager manager = new ScheduleManager();

    // Er for å opprette en ny fergetid
    public void createSchedule(int id, int routeId, String weekday, String timeHHmm, String type) {
        try {
            // Dette er for å konvertere klokkeslett fra tekst til LocalTime
            LocalTime time = LocalTime.parse(timeHHmm + ":00");

            // Denne lager et nytt schedule objekt og det blir sendt til schedulemanager
            Schedule schedule = new Schedule(id, routeId, weekday, time, type);
            boolean ok = manager.createSchedule(schedule);

            if (ok) {
                System.out.println("INFO: Fergetid opprettet.");
            } else {
                System.out.println("FEIL: Klarte ikke å opprette fergetid.");
            }
        } catch (Exception e) {
            System.out.println("FEIL: Ugyldig tidsformat (" + timeHHmm + ")");
        }
    }

    // Denne er for å hente alle fergetider
    public void listAllSchedules() {
        ArrayList<Schedule> schedules = manager.getAllSchedules();
        if (schedules.isEmpty()) {
            System.out.println("INFO: Ingen fergetider registrert.");
        } else {
            System.out.println("INFO: Alle fergetider:");
            for (Schedule s : schedules) {
                s.printInfo();
            }
        }
    }

    // Er for å hente en fergetid med id
    public void getScheduleById(int id) {
        Schedule schedule = manager.getScheduleById(id);
        if (schedule != null) {
            System.out.println("INFO: Fant fergetid:");
            schedule.printInfo();
        } else {
            System.out.println("FEIL: Fant ingen fergetid med id " + id);
        }
    }

    // Denne er for å oppdatere fergetid
    public void updateSchedule(int id, String newTimeHHmm, String newType) {
        try {
            // Konverterer ny tid fra tekst til LocalTime
            LocalTime newTime = LocalTime.parse(newTimeHHmm + ":00");
            boolean ok = manager.updateSchedule(id, newTime, newType);

            if (ok) {
                System.out.println("INFO: Fergetid ble oppdatert.");
            } else {
                System.out.println("FEIL: Kunne ikke oppdatere fergetid.");
            }
        } catch (Exception e) {
            System.out.println("FEIL: Det er en ugyldig tidsformat (" + newTimeHHmm + ")");
        }
    }

    // Dette er for å slette fergetid
    public void deleteSchedule(int id) {
        boolean ok = manager.deleteSchedule(id);
        if (ok) {
            System.out.println("INFO: Fergetid ble slettet.");
        } else {
            System.out.println("FEIL: Fant ingen fergetid å slette.");
        }
    }
}
