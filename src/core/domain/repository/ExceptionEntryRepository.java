package domain.repository;

import domain.model.ExceptionEntry;
import java.util.List;

public interface ExceptionEntryRepository {
    void create(ExceptionEntry exceptionEntry);       // CREATE
    ExceptionEntry read(int id);                      // READ by ID
    List<ExceptionEntry> readAll();                   // READ all
    void update(ExceptionEntry exceptionEntry);       // UPDATE
    void delete(int id);                              // DELETE by ID
}