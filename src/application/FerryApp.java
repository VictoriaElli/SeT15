import database.DatabaseConnector;
import domain.model.*;
import adapter.*;
import exception.MySQLDatabaseException;
import service.ScheduleServiceWithoutDTO;
import dto.DepartureDTO;

import java.sql.SQLException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class FerryApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            var conn = DatabaseConnector.connect();

            // --- Repository-adaptere ---
            StopsRepositoryMYSQLAdapter stopsRepo = new StopsRepositoryMYSQLAdapter(conn);
            RouteRepositoryMYSQLAdapter routeRepo = new RouteRepositoryMYSQLAdapter(conn);
            SeasonRepositoryMYSQLAdapter seasonRepo = new SeasonRepositoryMYSQLAdapter(conn);
            OperationMessageRepositoryMYSQLAdapter msgRepo = new OperationMessageRepositoryMYSQLAdapter(conn, routeRepo);
            RouteStopsRepositoryMYSQLAdapter routeStopsRepo = new RouteStopsRepositoryMYSQLAdapter(conn, routeRepo, stopsRepo);
            FrequencyRepositoryMYSQLAdapter frequencyRepo = new FrequencyRepositoryMYSQLAdapter(conn, routeRepo, seasonRepo);
            ExceptionEntryRepositoryMYSQLAdapter exceptionRepo = new ExceptionEntryRepositoryMYSQLAdapter(conn, routeRepo, stopsRepo, seasonRepo, msgRepo);

            // --- ScheduleService ---
            ScheduleServiceWithoutDTO scheduleService = new ScheduleServiceWithoutDTO(
                    routeRepo,
                    routeStopsRepo,
                    frequencyRepo,
                    exceptionRepo
            );

            // --- Hent alle stops ---
            Map<Integer, Stops> stopsMap = new HashMap<>();
            for (Stops stop : stopsRepo.readAll()) {
                stopsMap.put(stop.getId(), stop);
            }

            if (stopsMap.isEmpty()) {
                System.out.println("No stops found in database.");
                return;
            }

            // Print stops
            System.out.println("Available stops:");
            stopsMap.forEach((id, stop) -> System.out.println(id + ": " + stop.getName()));

            boolean exit = false;
            while (!exit) {

                // --- Input: Stopp ---
                System.out.print("\nEnter FROM stop ID (or 0 to exit): ");
                int fromId = scanner.nextInt();
                if (fromId == 0) break;

                System.out.print("Enter TO stop ID: ");
                int toId = scanner.nextInt();

                Stops fromStop = stopsMap.get(fromId);
                Stops toStop = stopsMap.get(toId);
                if (fromStop == null || toStop == null) {
                    System.out.println("Invalid stop IDs.");
                    continue;
                }

                // --- Input: Dato og tid ---
                System.out.print("Enter travel date (YYYY-MM-DD): ");
                LocalDate date = LocalDate.parse(scanner.next());
                System.out.print("Enter earliest departure time (HH:MM): ");
                LocalTime time = LocalTime.parse(scanner.next());

                // --- Input: TimeMode ---
                TimeMode timeMode = null;
                while (timeMode == null) {
                    System.out.println("\nSelect TimeMode:");
                    System.out.println("1: DEPART");
                    System.out.println("2: ARRIVAL");
                    System.out.println("3: NOW");
                    System.out.print("Enter choice (1-3): ");
                    int choice = scanner.nextInt();

                    switch (choice) {
                        case 1 -> timeMode = TimeMode.DEPART;
                        case 2 -> timeMode = TimeMode.ARRIVAL;
                        case 3 -> timeMode = TimeMode.NOW;
                        default -> System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                    }
                }

                // --- Bygg tidsplan ---
                scheduleService.buildSchedule(date);

                // --- Hent avganger ---
                List<DepartureDTO> departures = scheduleService.getDepartures(fromStop, toStop, date, time, timeMode);

                System.out.println("\n=== Departures from " + fromStop.getName() + " to " + toStop.getName() + " ===");
                if (departures.isEmpty()) {
                    System.out.println("No departures found.");
                } else {
                    for (DepartureDTO d : departures) {
                        System.out.printf(
                                "%d | %s -> %s | Departs %s | Arrives %s\n",
                                d.getRouteNumber(),
                                d.getFromStopName(),
                                d.getToStopName(),
                                d.getPlannedDeparture(),
                                d.getArrivalTime()
                        );
                    }
                }

                // Spør om brukeren vil fortsette
                System.out.print("\nDo you want to search again? (y/n): ");
                String cont = scanner.next();
                if (!cont.equalsIgnoreCase("y")) exit = true;
            }

        } catch (SQLException | IOException | MySQLDatabaseException e) {
            System.err.println("Error connecting to database:");
            e.printStackTrace();
        }
    }
}
