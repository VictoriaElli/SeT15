package domain.repository;

import domain.model.OperationMessage;
import java.util.List;

public interface OperationMessageRepository {
    void create(OperationMessage message);           // CREATE
    OperationMessage read(int id);                   // READ by ID
    List<OperationMessage> readAll();                // READ all
    void update(OperationMessage message);           // UPDATE
    void delete(int id);                              // DELETE by ID
}