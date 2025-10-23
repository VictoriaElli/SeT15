package org.byferge.core.port.in;

import org.byferge.core.dto.SearchDeparturesRequest;
import org.byferge.core.dto.SearchDeparturesResponse;

// Dette er et interface for søk etter avganger.
// Her står bare hva systemet skal kunne gjøre
// Denne skal søke etter avganger

public interface SearchDeparturesUseCase {
    SearchDeparturesResponse search(SearchDeparturesRequest request);
}
