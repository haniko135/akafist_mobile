package net.energogroup.akafist.models;

/**
 * Entity class describing conferences
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class SkypesConfs {
    private final int id;
    private final String name;
    private final String url;

    /**
     * Constructor of a class with three parameters
     * @param id int - individual number
     * @param name String - name of the conference
     * @param url String - link to the conference
     */
    public SkypesConfs(int id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    /**
     * Constructor of a class with two parameters
     * @param id int - individual number
     * @param name String - name of the conference
     */
    public SkypesConfs(int id, String name) {
        this.id = id;
        this.name = name;
        this.url = null;
    }

    /**
     * @return String - returns name field
     */
    public String getName() {
        return name;
    }

    /**
     * @return String - returns url field
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return int - returns id field
     */
    public int getId() {
        return id;
    }

    /**
     * This method checks for a link
     * @return
     */
    public boolean isUrl(){
        if(url != null)
            return true;
        return false;
    }
}
