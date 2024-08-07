package net.energogroup.akafist.models.azbykaAPI;

public class CacheDate {
    private AbstractDate abstractDate;

    public CacheDate(AbstractDate abstractDate) {
        this.abstractDate = abstractDate;
    }

    public AbstractDate getAbstractDate() {
        return abstractDate;
    }

    public void setAbstractDate(AbstractDate abstractDate) {
        this.abstractDate = abstractDate;
    }
}
