package net.energogroup.akafist.models.psaltir;

public class PsaltirKafismaModel {
    private final int id;
    private final String name;
    private final String desc;

    public PsaltirKafismaModel(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
