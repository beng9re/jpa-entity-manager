package repository;

import java.util.List;
import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.entity.SimpleEntityManager;
import persistence.entity.EntityManager;

public class SimpleCrudRepository<T, ID> extends AbstractRepository<T, ID> implements CrudRepository<T, ID> {
    private final EntityManager entityManager;
    protected SimpleCrudRepository(JdbcTemplate jdbcTemplate, Class<T> tClass, Dialect dialect) {
        super(jdbcTemplate, tClass, dialect);
        this.entityManager = new SimpleEntityManager(jdbcTemplate, dialect);
    }

    public T save(T entity) {
        return (T) entityManager.persist(entity);
    }

    public void delete(T entity) {
        entityManager.remove(entity);
    }

    public T findById(Class<T> tClass, ID id) {
        return entityManager.find(tClass, id);
    }

    public List<T> findAll() {
        return entityManager.findAll(tClass);
    }
}
