package net.energogroup.akafist.models;

import net.energogroup.akafist.fragments.ChurchFragment;
import net.energogroup.akafist.viewmodel.ChurchViewModel;


/**
 * An entity class describing the name of the prayer block.
 * There are only in classes {@link ChurchFragment}
 * and {@link ChurchViewModel}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class ServicesModel {
    private final int id;
    private String name;
    private int type;
    private String date;

    /**
     * Class constructor
     * @param id int - individual number
     * @param name String - name of the prayer
     * @param type int - reference to the type id
     * @param date String - type
     */
    public ServicesModel(int id, String name, int type, String date) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
    }

    public ServicesModel(String date, String name, int id){
        this.date = date;
        this.name = name;
        this.id = id;
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

    /**
     * @return int - returns type field
     */
    public int getType() {
        return type;
    }

    /**
     * @return String - returns date field
     */
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ServicesModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", date='" + date + '\'' +
                '}';
    }
}
