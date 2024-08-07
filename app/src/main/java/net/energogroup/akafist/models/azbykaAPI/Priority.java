package net.energogroup.akafist.models.azbykaAPI;

import java.util.List;

public class Priority {
    private List<DayItem> items;

    public Priority(List<DayItem> items) {
        this.items = items;
    }

    public List<DayItem> getItems() {
        return items;
    }

    public void setItems(List<DayItem> items) {
        this.items = items;
    }
}
