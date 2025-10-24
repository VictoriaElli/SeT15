package org.byferge.core.infrastructure.database;

import org.byferge.core.port.out.DepartureRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Et fake repository for testing av søkelogikken i SearchDeparturesService.
// Denne klassen implementerer interfacet DepartureRepository,
// og fungerer som et midlertidig "databaselag" uten ekte data.
public class FakeDBRepository implements DepartureRepository {
    @Override
    public List<LocalTime> findDeparturesForRouteAndDate(int routeid, LocalDate date) {
        return List.of();
    }
}
