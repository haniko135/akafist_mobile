package net.energogroup.akafist.models.azbykaAPI;

public class WholeDayTropariaOrKontakia {
    private final int id;
    private String type;
    private String text;
    private String title;

    public WholeDayTropariaOrKontakia(int id) {
        this.id = id;
    }

    public WholeDayTropariaOrKontakia(int id, String type, String title) {
        this.id = id;
        this.type = type;
        this.title = title;
    }
}
