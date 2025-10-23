package org.byferge.core.dto;

// Dette er en DTO (Data Transfer Object) og
// er et enkelt objekt som kun inneholder data.
// Denne holder på input fra brukeren som gjør et "request"
public class SearchDeparturesRequest {
    public final String date;
    public final int routeId;

    public SearchDeparturesRequest(String date, int routeId) {
        this.date = date;
        this.routeId = routeId;
    }
}
