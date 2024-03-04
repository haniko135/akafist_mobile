package net.energogroup.akafist.models;

public class StarredModel {
    private int id;
    private String objectType;
    private String objectUrl;

    public StarredModel(int id, String objectType, String objectID) {
        this.id = id;
        this.objectType = objectType;
        this.objectUrl = objectID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectUrl() {
        return objectUrl;
    }

    public void setObjectUrl(String objectUrl) {
        this.objectUrl = objectUrl;
    }
}
