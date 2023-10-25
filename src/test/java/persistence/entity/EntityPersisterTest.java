package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;

class EntityPersisterTest {

    private JdbcTemplate jdbcTemplate;
    private DatabaseServer server;
    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(QueryGenerator.from(Person.class).create());
    }

    @Test
    @DisplayName("엔티티가 저장이 된다.")
    void entitySave() {
        Person person = new Person("이름", 3, "dsa@gmil.com");
        EntityMeta entityMeta = new EntityMeta(person.getClass());
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, entityMeta);

        Assertions.assertDoesNotThrow(() -> {
            entityPersister.insert(person);
        });
    }

    @Test
    @DisplayName("엔티티가 업데이트가 된다.")
    void entityUpdate() {
        Person person = new Person(1L, "이름", 3, "dsa@gmil.com");
        EntityMeta entityMeta = new EntityMeta(Person.class);
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, entityMeta);

        assertThat(entityPersister.update(person)).isTrue();
    }

    @Test
    @DisplayName("엔티티가 삭제 된다.")
    void entityDelete() {
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, new EntityMeta(Person.class));

        Assertions.assertDoesNotThrow(() -> {
            entityPersister.delete(1);
        });
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(QueryGenerator.from(Person.class).drop());
        server.stop();
    }
}