package persistence.entity;

public class EntityEntry {
    private final Object entity;
    private EntityStatus status;

    public EntityEntry(Object entity) {
        this.entity = entity;

    }

    public Object saving(EntityPersister persister) {
        status = EntityStatus.SAVING;
        final Object saveEntity = persister.insert(entity);
        status = EntityStatus.MANAGED;
        return saveEntity;
    }

    public boolean isManaged() {
        return status == EntityStatus.MANAGED;
    }

    public boolean isReadOnly() {
        return status == EntityStatus.READ_ONLY;
    }

    public boolean isDeleted() {
        return status == EntityStatus.DELETED;
    }

    public boolean isGone() {
        return status == EntityStatus.GONE;
    }

    public boolean isLoading() {
        return status == EntityStatus.LOADING;
    }

    public boolean isSaving() {
        return status == EntityStatus.SAVING;
    }


}
