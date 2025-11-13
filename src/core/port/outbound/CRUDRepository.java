package port.outbound;

import java.util.List;

public interface CRUDRepository<T> {
    void create(T entity);       // CREATE
    T read(int id);              // READ by ID
    List<T> readAll();           // READ all
    void update(T entity);       // UPDATE
    void delete(int id);         // DELETE by ID
}