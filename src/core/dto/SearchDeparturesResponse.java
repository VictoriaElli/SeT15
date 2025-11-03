package dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Dette er en DTO (Data Transfer Object) og
// er et enkelt objekt som gir et resultat fra en request.
//  Vi får tilbake ruteId og dato og en liste med klokkeslett.
public class SearchDeparturesResponse {
    public final int routeId;
    public final LocalDate date;
    public final List <LocalTime> departures;

    public SearchDeparturesResponse(int routeId, LocalDate date, List <LocalTime> departures) {
        this.routeId = routeId;
        this.date = date;
        this.departures = departures;
    }
}
