package adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import port.outbound.CRUDRepositoryPort;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class GenericJpaCrudRepositoryAdapter<T> implements CRUDRepositoryPort<T> {
    private final EntityManager em;
    private final Class<T> type;

    public GenericJpaCrudRepositoryAdapter(EntityManager em, Class<T> type) {
        this.em = em;
        this.type = type;
    }

    @Override
    public void create(T entity) {
        em.persist(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> readById(int id) {
        return Optional.ofNullable(em.find(type, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> readAll() {
        TypedQuery<T> query =
                em.createQuery("SELECT t FROM "+type.getSimpleName()+" t", type);
        return query.getResultList();
    }

    @Override
    public void update(T entity) {
        em.merge(entity);
    }

    @Override
    public void delete(T entity) {
        if (!em.contains(entity)) {
            entity = em.merge(entity);
        }
        em.remove(entity);
    }

    @Override
    public void deleteById(int id) {
        T entity = em.find(type, id);
        if (entity != null) {
            em.remove(entity);
        }
    }
}
