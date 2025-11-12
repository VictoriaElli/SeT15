package unitTesting;

import domain.model.*;
import domain.repository.*;
import domain.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {

    // Deklarerer mock-objektene for de nødvendige repositoryene
    private RouteRepository routeRepository;
    private FrequencyRepository frequencyRepository;
    private ExceptionEntryRepository exceptionRepository;
    private ScheduleService scheduleService;

    // Før hver test initialiseres mock-objektene og ScheduleService
    @BeforeEach
    void setUp() {
        routeRepository = mock(RouteRepository.class); // Mock av RouteRepository
        frequencyRepository = mock(FrequencyRepository.class); // Mock av FrequencyRepository
        exceptionRepository = mock(ExceptionEntryRepository.class); // Mock av ExceptionEntryRepository
        scheduleService = new ScheduleService(routeRepository, frequencyRepository, exceptionRepository); // Initialisering av ScheduleService med mocks
    }

    // Test for å sikre at frekvenser og unntak blir oppdatert i metoden updateSchedule
    @Test
    void updateSchedule_ShouldUpdateFrequenciesAndExceptions() {
        // Arrange (forbereder testdata)
        int routeId = 1; // Rute-ID som skal brukes i testen
        LocalDate date = LocalDate.of(2025, 11, 12); // Dato for oppdatering av tidtabell

        // Oppretter en ny rute
        Route route = new Route(routeId, 101);

        // Oppretter en ny frekvens tilhørende ruten
        Frequency freq = new Frequency(10, route, Weekday.MONDAY, new Season("Winter", 2025,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31)),
                LocalTime.of(6, 0), LocalTime.of(10, 0), 30);

        // Oppretter et unntak for ruten
        ExceptionEntry exc = new ExceptionEntry.Builder()
                .setId(20)
                .setRoute(route)
                .setValidDate(date)
                .setDepartureTime(LocalTime.of(8, 0))
                .setType(ExceptionType.DELAYED)
                .build();

        // Definerer hva mock-objektene skal returnere
        when(routeRepository.read(routeId)).thenReturn(route); // RouteRepository skal returnere vår rute når routeId er 1
        when(frequencyRepository.readAll()).thenReturn(List.of(freq)); // FrequencyRepository skal returnere vår frekvens
        when(exceptionRepository.readAll()).thenReturn(List.of(exc)); // ExceptionEntryRepository skal returnere vårt unntak

        // Act (kaller metoden som skal testes)
        Schedule result = scheduleService.updateSchedule(routeId, date, List.of(freq), List.of(exc));

        // Assert (verifiserer at resultatet er som forventet)
        verify(frequencyRepository).update(freq); // Verifiserer at update ble kalt på frequencyRepository
        verify(exceptionRepository).update(exc); // Verifiserer at update ble kalt på exceptionRepository
        assertNotNull(result); // Verifiserer at resultatet ikke er null
        assertEquals(routeId, result.getRoute().getId()); // Verifiserer at den returnerte tidtabellen har riktig rute-ID
        assertEquals(1, result.getFrequencies().size()); // Verifiserer at én frekvens er inkludert i tidtabellen
        assertEquals(1, result.getExceptions().size()); // Verifiserer at én unntak er inkludert i tidtabellen
    }

    // Test for å sikre at en unntak blir kastet når ruten ikke finnes i oppdateringen
    @Test
    void updateSchedule_ShouldThrowException_WhenRouteNotFound() {
        int routeId = 99; // En rute-ID som ikke finnes
        LocalDate date = LocalDate.of(2025, 11, 12); // Dato for oppdatering

        // Simulerer at ruten ikke finnes ved å returnere null fra routeRepository
        when(routeRepository.read(routeId)).thenReturn(null);

        // Sjekker at IllegalArgumentException kastes når metoden blir kalt med en ikke-eksisterende rute
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                scheduleService.updateSchedule(routeId, date, List.of(), List.of())
        );
        // Verifiserer at feilmeldingen inneholder "Route not found"
        assertTrue(ex.getMessage().contains("Route not found"));
    }

    // Test for å sikre at en unntak blir kastet når frekvensen tilhører feil rute
    @Test
    void updateSchedule_ShouldThrowException_WhenFrequencyBelongsToWrongRoute() {
        int routeId = 1; // Rute-ID for testen
        LocalDate date = LocalDate.of(2025, 11, 12); // Dato for oppdatering

        // Oppretter en riktig og feil rute
        Route correctRoute = new Route(routeId, 101);
        Route wrongRoute = new Route(2, 202); // Feil rute

        // Oppretter en frekvens som tilhører feil rute
        Frequency wrongFreq = new Frequency(10, wrongRoute, Weekday.MONDAY, new Season("Winter", 2025,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31)),
                LocalTime.of(6, 0), LocalTime.of(10, 0), 30);

        // Simulerer at den riktige ruten blir returnert fra routeRepository
        when(routeRepository.read(routeId)).thenReturn(correctRoute);

        // Sjekker at IllegalArgumentException kastes når frekvensen tilhører feil rute
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                scheduleService.updateSchedule(routeId, date, List.of(wrongFreq), List.of())
        );
        // Verifiserer at feilmeldingen inneholder "Frequency does not belong"
        assertTrue(ex.getMessage().contains("Frequency does not belong"));
    }

    // Test for å sikre at en unntak blir kastet når unntaket tilhører feil rute
    @Test
    void updateSchedule_ShouldThrowException_WhenExceptionBelongsToWrongRoute() {
        int routeId = 1; // Rute-ID for testen
        LocalDate date = LocalDate.of(2025, 11, 12); // Dato for oppdatering

        // Oppretter en riktig og feil rute
        Route correctRoute = new Route(routeId, 101);
        Route wrongRoute = new Route(2, 202); // Feil rute

        // Oppretter et unntak som tilhører feil rute
        ExceptionEntry wrongExc = new ExceptionEntry.Builder()
                .setId(30)
                .setRoute(wrongRoute)
                .setValidDate(date)
                .setDepartureTime(LocalTime.of(9, 0))
                .setType(ExceptionType.CANCELLED)
                .build();

        // Simulerer at den riktige ruten blir returnert fra routeRepository
        when(routeRepository.read(routeId)).thenReturn(correctRoute);

        // Sjekker at IllegalArgumentException kastes når unntaket tilhører feil rute
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                scheduleService.updateSchedule(routeId, date, List.of(), List.of(wrongExc))
        );
        // Verifiserer at feilmeldingen inneholder "Exception does not belong"
        assertTrue(ex.getMessage().contains("Exception does not belong"));
    }

    // Test for å sikre at tomme lister håndteres korrekt (ingen oppdateringer)
    @Test
    void updateSchedule_ShouldHandleEmptyLists() {
        int routeId = 1; // Rute-ID for testen
        LocalDate date = LocalDate.of(2025, 11, 12); // Dato for oppdatering

        // Oppretter en ny rute
        Route route = new Route(routeId, 101);

        // Simulerer at ingen frekvenser og unntak finnes
        when(routeRepository.read(routeId)).thenReturn(route);
        when(frequencyRepository.readAll()).thenReturn(List.of());
        when(exceptionRepository.readAll()).thenReturn(List.of());

        // Kaller updateSchedule med tomme lister for frekvenser og unntak
        Schedule result = scheduleService.updateSchedule(routeId, date, List.of(), List.of());

        // Verifiserer at resultatet ikke er null og at ingen frekvenser eller unntak ble lagt til
        assertNotNull(result);
        assertEquals(routeId, result.getRoute().getId());
        assertTrue(result.getFrequencies().isEmpty());
        assertTrue(result.getExceptions().isEmpty());
    }
}
