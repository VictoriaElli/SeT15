package domain.repository;

import java.util.List;

public interface CrudRepository<T> {
    void create(T entity);       // CREATE
    T read(int id);                      // READ by ID
    List<T> readAll();                   // READ all
    void update(T entity);       // UPDATE
    void delete(int id);                              // DELETE by ID
}