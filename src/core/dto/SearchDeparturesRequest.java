package org.byferge.core.dto;

import java.time.LocalDate;

// Dette er en DTO (Data Transfer Object) og
// er et enkelt objekt som kun inneholder data.
// Denne holder på input fra brukeren som gjør et "request"
public class SearchDeparturesRequest {
    public final LocalDate date;
    public final int routeId;

    public SearchDeparturesRequest(LocalDate date, int routeId) {
        this.date = date;
        this.routeId = routeId;
    }
}
