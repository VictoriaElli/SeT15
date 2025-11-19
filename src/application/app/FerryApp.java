package app;

import database.DatabaseConnector;
import domain.model.*;
import adapter.*;
import dto.DepartureRequestDTO;
import dto.DepartureResponseDTO;
import exception.MySQLDatabaseException;
import port.outbound.StopDistanceRepositoryPort;
import port.outbound.StopsRepositoryPort;
import service.EnvironmentService;
import service.ScheduleService;
import dto.DepartureDTO;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class FerryApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            DataSource dataSource = DatabaseConnector.getDataSource();

            // --- Repository-adaptere ---
            StopsRepositoryMYSQLAdapter stopsRepo = new StopsRepositoryMYSQLAdapter(dataSource);
            RouteRepositoryMYSQLAdapter routeRepo = new RouteRepositoryMYSQLAdapter(dataSource);
            SeasonRepositoryMYSQLAdapter seasonRepo = new SeasonRepositoryMYSQLAdapter(dataSource);
            OperationMessageRepositoryMYSQLAdapter msgRepo = new OperationMessageRepositoryMYSQLAdapter(dataSource, routeRepo);
            RouteStopsRepositoryMYSQLAdapter routeStopsRepo = new RouteStopsRepositoryMYSQLAdapter(dataSource, routeRepo, stopsRepo);
            FrequencyRepositoryMYSQLAdapter frequencyRepo = new FrequencyRepositoryMYSQLAdapter(dataSource, routeRepo, seasonRepo);
            ExceptionEntryRepositoryMYSQLAdapter exceptionRepo = new ExceptionEntryRepositoryMYSQLAdapter(dataSource, routeRepo, stopsRepo, seasonRepo, msgRepo);
            StopDistanceRepositoryMYSQLAdapter stopsDistanceRepo = new StopDistanceRepositoryMYSQLAdapter(dataSource, stopsRepo);
            EnvironmentService environmentService = new EnvironmentService(stopsDistanceRepo);

            // --- ScheduleService ---
            ScheduleService scheduleService = new ScheduleService(
                    routeRepo,
                    routeStopsRepo,
                    frequencyRepo,
                    exceptionRepo,
                    stopsRepo,
                    environmentService
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

                // --- Input: Dato og tid (hvis ikke NOW) ---
                LocalDate date = LocalDate.now();
                LocalTime time = LocalTime.now();
                if (timeMode != TimeMode.NOW) {
                    System.out.print("Enter travel date (YYYY-MM-DD): ");
                    date = LocalDate.parse(scanner.next());

                    if (timeMode == TimeMode.DEPART) {
                        System.out.print("Enter earliest departure time (HH:MM): ");
                    } else {
                        System.out.print("Enter desired arrival time (HH:MM): ");
                    }
                    time = LocalTime.parse(scanner.next());
                }

                // --- Hent avganger ---
                List<DepartureResponseDTO> departures = scheduleService.getDepartures(new DepartureRequestDTO(fromStop.getName(), toStop.getName(), date, time, timeMode));

                System.out.println("\n=== Departures from " + fromStop.getName() + " to " + toStop.getName() + " ===");
                if (departures.isEmpty()) {
                    System.out.println("No departures found.");
                } else {
                    for (DepartureResponseDTO d : departures) {
                        System.out.printf(
                                "%d | %s -> %s | Departs %s | Arrives %s | %s\n",
                                d.getRouteNumber(),
                                d.getFromStopName(),
                                d.getToStopName(),
                                d.getPlannedDeparture(),
                                d.getArrivalTime(),
                                d.getEnvironmentSavings()
                        );
                    }
                }

                // Spør om brukeren vil fortsette
                System.out.print("\nDo you want to search again? (y/n): ");
                String cont = scanner.next();
                if (!cont.equalsIgnoreCase("y")) exit = true;
            }

        } catch (MySQLDatabaseException e) {
            System.err.println("Error connecting to database:");
            e.printStackTrace();
        }
    }
}
