package domain.model.crud;

import java.time.LocalTime;
import java.util.ArrayList;

// ScheduleManager klassen håndterer alle crud operasjoner for fergetider.
// Den kan lage, hente, endre og slette fergetider i systemet.
// Klassen brukes for å styre alle registrerte fergetider.
public class ScheduleManager {

    // Liste som lagrer alle fergetidene
    private ArrayList<Schedule> schedules = new ArrayList<>();

    // Er for å lage en ny fergetid
    public boolean createSchedule(Schedule schedule) {
        // Sjekk at dataene er gyldige før de lagres
        if (!validateSchedule(schedule)) {
            System.out.println("FEIL: Ugyldig fergetid. Ble ikke opprettet.");
            return false;
        }

        // Det her sjekker om fergetiden allerede finnes fra før
        for (Schedule s : schedules) {
            if (s.getRouteId() == schedule.getRouteId() &&
                    s.getWeekday().equals(schedule.getWeekday()) &&
                    s.getTime().equals(schedule.getTime())) {
                System.out.println("FEIL: Denne fergetiden finnes allerede.");
                return false;
            }
        }

        // For å legge til ny fergetid
        schedules.add(schedule);
        System.out.println("LOGG: Fergetid opprettet " + schedule.toString());
        return true;
    }

    // Denne skal hent alle fergetider
    public ArrayList<Schedule> getAllSchedules() {
        return schedules;
    }

    // Denne skal hent en bestemt fergetid basert på id
    public Schedule getScheduleById(int id) {
        for (Schedule s : schedules) {
            if (s.getId() == id) {
                return s;
            }
        }
        System.out.println("INFO: Fant ingen fergetid med id " + id);
        return null;
    }

    // Dette er for å oppdatere en eksisterende fergetid
    public boolean updateSchedule(int id, LocalTime newTime, String newType) {
        for (Schedule s : schedules) {
            if (s.getId() == id) {
                // Sjekk at ny tid og type er oppgitt
                if (newTime == null || newType == null) {
                    System.out.println("FEIL: Ny tid eller type mangler.");
                    return false;
                }

                // Sjekk at den nye tiden ikke overlapper med en annen fergetid
                for (Schedule other : schedules) {
                    if (other.getId() != id &&
                            other.getRouteId() == s.getRouteId() &&
                            other.getWeekday().equals(s.getWeekday()) &&
                            other.getTime().equals(newTime)) {
                        System.out.println("FEIL: Det finnes allerede en fergetid på dette tidspunktet.");
                        return false;
                    }
                }

                // Dette er for utføring av oppdatering
                s.setTime(newTime);
                s.setType(newType);
                System.out.println("LOGG: Fergetid oppdatert " + s.toString());
                return true;
            }
        }

        System.out.println("FEIL: Fant ingen fergetid å oppdatere (id " + id + ")");
        return false;
    }

    // For å slette en fergetid
    public boolean deleteSchedule(int id) {
        for (int i = 0; i < schedules.size(); i++) {
            Schedule s = schedules.get(i);
            if (s.getId() == id) {
                schedules.remove(i);
                System.out.println("LOGG: Fergetid slettet med id " + id);
                return true;
            }
        }

        System.out.println("FEIL: Fant ingen fergetid å slette (id " + id + ")");
        return false;
    }

    // Dette er for å validere data for å sikre at alt er gyldig
    private boolean validateSchedule(Schedule schedule) {
        if (schedule == null) return false;
        if (schedule.getRouteId() <= 0) return false;
        if (schedule.getWeekday() == null || !Schedule.VALID_WEEKDAYS.contains(schedule.getWeekday())) return false;
        if (schedule.getType() == null || !Schedule.VALID_TYPES.contains(schedule.getType())) return false;
        if (schedule.getTime() == null) return false;
        return true;
    }
}
