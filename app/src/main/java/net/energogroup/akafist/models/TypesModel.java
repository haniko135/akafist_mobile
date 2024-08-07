package net.energogroup.akafist.models;

/**
 * Entity class describing the type to which
 * the entity class {@link ServicesModel} belongs
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class TypesModel {
    private final int id;
    private String name;

    /**
     * Class Constructor
     * @param id int - individual number
     * @param name String - type name
     */
    public TypesModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @return int - returns id field
     */
    public int getId() {
        return id;
    }

    /**
     * @return String - returns name field
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
