package net.energogroup.akafist.models;

public class SkypeBlockItem {
    private String id;
    private String name;
    private String order;
    private String created_at;
    private String updated_at;

    public SkypeBlockItem(String id, String name, String order, String created_at, String updated_at) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
