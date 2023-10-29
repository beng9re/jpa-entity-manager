package persistence.entity;

import java.lang.reflect.Field;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.dialect.Dialect;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;


public class EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(EntityPersister.class);
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;

    public EntityPersister(JdbcTemplate jdbcTemplate,
                           Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public <T> T insert(T entity) {
        final EntityMeta entityMeta = EntityMeta.from(entity.getClass());
        final String query = QueryGenerator.of(entityMeta, dialect)
                .insert()
                .build(entity);

        log.info(query);

        if (entityMeta.isAutoIncrement()) {
            final long id = jdbcTemplate.insertForGenerateKey(query);
            changeValue(entityMeta.getPkColumn(), entity, id);
        }
        return entityMeta.createCopyEntity(entity);
    }

    public boolean update(Object entity) {
        final EntityMeta entityMeta = EntityMeta.from(entity.getClass());
        final String query = QueryGenerator.of(entityMeta, dialect)
                .update()
                .build(entity);

        log.info(query);
        jdbcTemplate.execute(query);
        return true;
    }

    public void delete(Object entity) {
        final EntityMeta entityMeta = EntityMeta.from(entity.getClass());

        final String query = QueryGenerator.of(entityMeta, dialect)
                .delete()
                .build(entityMeta.getPkValue(entity));

        log.info(query);

        jdbcTemplate.execute(query);
    }
    private void changeValue(EntityColumn column, Object entity, Object value) {
        try {
            final Field field = entity.getClass().getDeclaredField(column.getFieldName());
            field.setAccessible(true);
            field.set(entity, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
