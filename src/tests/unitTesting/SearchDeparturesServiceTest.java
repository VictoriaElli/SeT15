package unitTesting;

import dto.SearchDeparturesRequest;
import dto.SearchDeparturesResponse;
import port.outbound.DepartureRepository;
import usecases.SearchDeparturesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SearchDeparturesServiceTest {

    @Mock
    DepartureRepository mockDepartureRepository;

    SearchDeparturesService service;

    @Test
    public void testInvalidInput_RequestIsNull() {
        // Vi tester at når request er null, skal det kastes en IllegalArgumentException.
        service = new SearchDeparturesService(mockDepartureRepository);

        // Forvent at metoden kaster feil når request er null
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.search(null));

        // Ved ugyldig input skal vi ikke snakke med repository
        Mockito.verifyNoInteractions(mockDepartureRepository);
    }

    @Test
    public void testInvalidInput_DateIsNull() {

        // Vi tester at når dato mangler, skal det kastes en IllegalArgumentException.
        service = new SearchDeparturesService(mockDepartureRepository);

        // Vi oppretter en request uten dato (ugyldig input)
        SearchDeparturesRequest requestWithoutDate = new SearchDeparturesRequest(null, 5);

        // Forvent at metoden kaster feil når dato mangler
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.search(requestWithoutDate));

        // Ved ugyldig input skal vi ikke snakke med repository
        Mockito.verifyNoInteractions(mockDepartureRepository);
    }

    @Test
    public void testInvalidInput_RouteIdIsNegative() {

        // Vi tester at når routeId er negativ, skal det kastes en IllegalArgumentException.
        service = new SearchDeparturesService(mockDepartureRepository);

        // Vi oppretter en request med negativ routeId (ugyldig input)
        SearchDeparturesRequest requestWithNegativeRouteId = new SearchDeparturesRequest(LocalDate.of(2025, 11, 17), -7);

        // Forvent at metoden kaster feil når routeId er negativ
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.search(requestWithNegativeRouteId));

        // Ved ugyldig input skal vi ikke snakke med repository
        Mockito.verifyNoInteractions(mockDepartureRepository);
    }

    @Test
    public void testNoDepartures_ReturnEmptyList() {
        // Vi tester at når repository ikke finner avganger, skal servicen returnere en tom liste.

        // ARRANGE
        int routeId = 6;
        LocalDate date = LocalDate.of(2025,12,7);

        //Repoet finner ingen avganger og returnerer en tom liste
        Mockito.when(mockDepartureRepository.findDeparturesForRouteAndDate(routeId, date)).thenReturn(List.of());

        //Vi oppretter servicen med mocking-repo
        service = new SearchDeparturesService(mockDepartureRepository);

        SearchDeparturesRequest request = new SearchDeparturesRequest(date, routeId);
        SearchDeparturesResponse response = service.search(request);

        // ASSERT
        // Kontrollerer at ruteId og dato i responsen stemmer overens med verdiene i requesten.
        Assertions.assertEquals(routeId, response.routeId);
        Assertions.assertEquals(date, response.date);

        //Sjekke at lista er tom
        Assertions.assertTrue(response.departures.isEmpty());

        // Sjekker at repositoryet ble kalt med riktig rute og dato
        Mockito.verify(mockDepartureRepository).findDeparturesForRouteAndDate(routeId, date);
        Mockito.verifyNoMoreInteractions(mockDepartureRepository);

    }

    @Test
    public void testValidRequest_ReturnsAllDepartures() {
        // ARRANGE
        // Her skrives inn data som "forventes"
        // Vi definerer hvilken rute og dato vi ønsker å teste søk på
        int routeId = 5;
        LocalDate date = LocalDate.of(2025,11, 3);
        // Vi lager en liste med avganger som vi later som kommer fra databasen (gjennom repositoryet).
        // Denne listen representerer det vi forventer at søket skal returnere.
        List<LocalTime> expectedDepartures = List.of(
                LocalTime.of(9, 15),
                LocalTime.of(13, 45),
                LocalTime.of(16,30));

        // Forteller mocken hva den skal returnere når metoden blir kalt
        // mocken skal returnere listen vi definerte over (expectedDepartures)
        Mockito.when(mockDepartureRepository.findDeparturesForRouteAndDate(routeId, date))
                .thenReturn(expectedDepartures);

        // Her oppretter vi selve tjenesten (SearchDeparturesService) vi ønsker å teste
        // Vi sender inn mocket repository
        service = new SearchDeparturesService(mockDepartureRepository);

        // ACT
        // Vi oppretter et nytt objekt av typen SearchDeparturesRequest
        // og kaller den request
        var request = new SearchDeparturesRequest(date, routeId);
        // Vi kjører metoden search() på objektet service
        // og tar vare på resultatet i en variabel kalt response.
        var response = service.search(request);

        // ASSERT
        // Vi kontrollerer at resultatet ble som forventet
        // 1. Sjekker at routeId i svaret er det samme som vi sendte inn.
        Assertions.assertEquals(routeId, response.routeId);

        // 2. Sjekker at datoen i svaret også er den samme.
        Assertions.assertEquals(date, response.date);

        // 3. Sjekker at lista med avganger som kommer fra servicen er identisk med forventet liste.
        // Dette tester at søkelogikken returnerer nøyaktig det repositoryet (mocken) ga oss.
        Assertions.assertIterableEquals(expectedDepartures, response.departures);

        // Sjekker at repositoryet ble brukt riktig
        // Vi bekrefter at metoden findDeparturesForRouteAndDate() ble kalt én gang med riktige argumenter.
        Mockito.verify(mockDepartureRepository).findDeparturesForRouteAndDate(routeId, date);

        // Vi sjekker også at det ikke ble gjort noen andre kall til repositoryet.
        Mockito.verifyNoMoreInteractions(mockDepartureRepository);
    }
}
