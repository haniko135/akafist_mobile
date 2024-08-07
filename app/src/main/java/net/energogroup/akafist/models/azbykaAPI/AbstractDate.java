package net.energogroup.akafist.models.azbykaAPI;

import java.util.List;

public class AbstractDate {
    private Priority priorities;

    public AbstractDate(Priority priorities) {
        this.priorities = priorities;
    }

    public Priority getPriorities() {
        return priorities;
    }

    public void setPriorities(Priority priorities) {
        this.priorities = priorities;
    }
}
