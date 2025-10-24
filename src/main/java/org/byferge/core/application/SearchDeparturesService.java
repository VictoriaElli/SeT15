package org.byferge.core.application;

import org.byferge.core.dto.SearchDeparturesRequest;
import org.byferge.core.dto.SearchDeparturesResponse;
import org.byferge.core.port.in.SearchDeparturesUseCase;
import org.byferge.core.port.out.DepartureRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

// Oppretter en Service som bestemmer søkelogikken
// Den implementerer interfacet SearchDeparturesUseCase
public class SearchDeparturesService implements SearchDeparturesUseCase {
    private final DepartureRepository repository;

    //Konstruktør
    public SearchDeparturesService (DepartureRepository repository) {
        this.repository = repository;
    }

    // Her overstyrer vi metoden definert i interfacet SearchDeparturesUseCase
    // Et request-objekt blir tatt inn og skal returnere et
    // SearchDeparturesResponse-objekt
    @Override
    public SearchDeparturesResponse search(SearchDeparturesRequest request) {
        // Sjekker at request ikke er tom
        // Hvis den er det får vi feilmelding
        if(request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        // Sjekker at date ikke er tom
        // Hvis den er det får vi feilmelding
        if(request.date == null || request.date.isBlank()) {
            throw new IllegalArgumentException("date cannot be null");
        }
        // Sjekker at routeId ikke er negativ
        // Hvis den er det får vi feilmelding
        if(request.routeId < 0) {
            throw new IllegalArgumentException("routeId cannot be negative");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(request.date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in format yyy-MM-dd", e);
        }

        // Oppretter en liste som skal inneholde alle avgangstidene
        // som hentes fra databasen for valgt rute og dato
        List<LocalTime> departures = repository.findDeparturesForRouteAndDate(request.routeId, date);

        //Her sjekker vi om listen med avganger som blir hentet ut er tom.
        // Er den tom blir det sent en tom liste, i steden for at systemet krasjer
        if(departures.isEmpty()){
            return new SearchDeparturesResponse(request.routeId, request.date, List.of());
        }

        //Hvis listen inneholder avgangstider blir den sendt til brukeren
        else {
            return new SearchDeparturesResponse(request.routeId, request.date, departures);
        }

    }
}
