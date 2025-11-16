package domain.model.crud;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// Schedule klassen representerer en fergetid i systemet
// Og den brukes for å lagre informasjon om rute, ukedag, tidspunkt og type avgang
public class Schedule {

    // Dette er variablene for en fergetid
    private int id;
    private int routeId;
    private String weekday;
    private LocalTime time;
    private String type;

    // Tillatte verdier for ukedager
    public static final Set<String> VALID_WEEKDAYS = new HashSet<>(
            Arrays.asList("MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY")
    );
    // Tillatte verdier for type
    public static final Set<String> VALID_TYPES = new HashSet<>(
            Arrays.asList("NORMAL","EXTRA","OMITTED")
    );

    // Dette er en konstruktør og det brukes for når man lager et nytt Schedule objekt
    public Schedule(int id, int routeId, String weekday, LocalTime time, String type) {
        this.id = id;
        this.routeId = routeId;
        this.weekday = weekday;
        this.time = time;
        this.type = type;
    }

    // Gettere og settere for variablene
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getWeekday() { return weekday; }
    public void setWeekday(String weekday) { this.weekday = weekday; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Brukes for å skrive ut objektet som tekst
    @Override
    public String toString() {
        return "FerrySchedule{" +
                "id=" + id +
                ", routeId=" + routeId +
                ", weekday='" + weekday + '\'' +
                ", time=" + time +
                ", type='" + type + '\'' +
                '}';
    }

    // En konsoll-debug som skriver ut informasjon om fergetiden
    public void printInfo() {
        System.out.println("RuteId: " + routeId + " Dag: " + weekday +
                " Tid: " + time + " Type: " + type);
    }
}

