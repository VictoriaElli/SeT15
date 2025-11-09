package database;

import port.outbound.DepartureRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

// Et fake repository for testing av søkelogikken i SearchDeparturesService.
// Denne klassen implementerer interfacet DepartureRepository,
// og fungerer som et midlertidig "databaselag" uten ekte data.
public class FakeDBRepository implements DepartureRepository {
    //Lager fiktive rute-id'er og avgangstider for testing
    private final Map<Integer, List<LocalTime>> fakeData = Map.of
            (1, List.of(LocalTime.of (10, 0), LocalTime.of(14, 15)),
            2, List.of(LocalTime.of(8, 30), LocalTime.of(13,45)));

    @Override
    public List<LocalTime> findDeparturesForRouteAndDate(int routeid, LocalDate date) {
        // Vi ignorerer date her, fordi vi kun skal teste søkelogikken
        //Den henter en liste hvis rute-tid'en finnes
        // Eller så henter den en tom liste
        return fakeData.getOrDefault(routeid, List.of());
    }
}
