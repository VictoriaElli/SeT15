package port.in;

import dto.SearchDeparturesRequest;
import dto.SearchDeparturesResponse;

// Dette er et interface for søk etter avganger.
// Her står bare hva systemet skal kunne gjøre
// Denne skal søke etter avganger

public interface SearchDeparturesUseCase {
    SearchDeparturesResponse search(SearchDeparturesRequest request);
}
