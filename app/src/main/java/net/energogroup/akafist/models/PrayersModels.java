package net.energogroup.akafist.models;

import androidx.annotation.NonNull;

/**
 * An entity class describing prayer
 * @author Nastya  Izotina
 * @version 1.0.0
 */
public class PrayersModels {
    private int id;
    private String name;
    private String html;
    private int prev;
    private int next;

    public PrayersModels() { }

    /**
     * Class constructor
     * @param name String - name of the prayer
     * @param html String - текст молитвы
     * @param prev int - the text of the prayer
     * @param next int - link to the following prayer
     */
    public PrayersModels(String name, String html, int prev, int next) {
        this.name = name;
        this.html = html;
        this.prev = prev;
        this.next = next;
    }

    /**
     * Class constructor
     * @param id int - prayer id
     * @param name String - name of the prayer
     * @param html String - текст молитвы
     * @param prev int - the text of the prayer
     * @param next int - link to the following prayer
     */
    public PrayersModels(int id, String name, String html, int prev, int next) {
        this.id = id;
        this.name = name;
        this.html = html;
        this.prev = prev;
        this.next = next;
    }

    /**
     * @return String - returns the name of the prayer
     */
    public String getName() {
        return name;
    }


    /**
     * @return String - returns the text of the prayer
     */
    public String getHtml() {
        return html;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }

    public void setNext(int next) {
        this.next = next;
    }

    /**
     * @return int - returns a link to the previous prayer
     */
    public int getPrev() {
        return prev;
    }

    /**
     * @return int - returns a link to the previous prayer
     */
    public int getNext() {
        return next;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return name +" : "+ html +" : "+prev+" : "+next;
    }
}
