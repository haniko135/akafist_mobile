package net.energogroup.akafist.models.psaltir;

public class PsaltirKafismaModel {
    private int id;
    private String name;
    private String desc;

    public PsaltirKafismaModel(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public PsaltirKafismaModel() {}

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
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
