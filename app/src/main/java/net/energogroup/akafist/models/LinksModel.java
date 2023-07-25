package net.energogroup.akafist.models;

/**
 * An entity class describing the basic parameters of an audio recording
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class LinksModel {
    private int id;
    private String url;
    private String name;
    private int image;

    public LinksModel() { }

    /**
     * LinksModel class constructor
     * @param id int - individual record number
     * @param url String - link to the record
     * @param name String - name of the record
     */
    public LinksModel(int id, String url, String name, int image) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.image = image;
    }

    public LinksModel(String url, String name) {
        this.url = url;
        this.name = name;
    }

    /**
     * LinksModel class constructor
     * @param url String - link to the record
     * @param name String - name of the record
     */
    public LinksModel(String url, String name, int image){
        this.url = url;
        this.name = name;
        this.image = image;
    }

    /**
     * @return int - Returns id field
     */
    public int getId() {
        return id;
    }

    /**
     * @return String - Returns url field
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return String - Returns name field
     */
    public String getName() {
        return name;
    }

    /**
     * @return int - Returns image id field
     */
    public int getImage() {
        return image;
    }

    /**
     * @param id - individual record number
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param url - link to record on Yandex.Disk
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @param name - record name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param image - image ID in Android resources
     */
    public void setImage(int image) {
        this.image = image;
    }
}
