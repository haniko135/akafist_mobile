package net.energogroup.akafist.models;

/**
 * An entity class that describes the main parameters
 * of the blocks in the "Home" and "Menu" fragments
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class HomeBlocksModel {
    private String date;
    private String dateTxt;
    private String name;
    private int id;
    private String desc;
    private String additions;
    private int links;

    /**
     * Constructor of a class with three parameters
     * @param date String - block type
     * @param dateTxt String - upper block name
     * @param name String - lower block name
     */
    public HomeBlocksModel(String date, String dateTxt, String name) {
        this.date = date;
        this.dateTxt = dateTxt;
        this.name = name;
    }

    public HomeBlocksModel(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    /**
     * Constructor of a class with two parameters
     * @param date String - block type
     * @param dateTxt String - upper block name
     */
    public HomeBlocksModel(String date, String dateTxt) {
        this.date = date;
        this.dateTxt = dateTxt;
    }

    /**
     * @return String - Returns the date field
     */
    public String getDate() {
        return date;
    }

    /**
     * @return String - Returns the dateTxt field
     */
    public String getDateTxt() {
        return dateTxt;
    }

    /**
     * @return String - Returns the name field
     */
    public String getName() {
        return name;
    }

    /**
     * This method assigns links
     * @param links int - Links to fragments
     */
    public void setLinks(int links) {
        this.links = links;
    }

    /**
     * @return int - Returns links field
     */
    public int getLinks() {
        return links;
    }

    /**
     * This method assigns additional attributes
     * @param additions String - additional attributes
     */
    public void setAdditions(String additions) {
        this.additions = additions;
    }

    /**
     * @return String - Returns additions field
     */
    public String getAdditions() {
        return additions;
    }

    public int getId() { return id; }
}
