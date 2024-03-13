package net.energogroup.akafist.models;

import androidx.annotation.NonNull;

/**
 * An entity class describing prayer
 * @author Nastya  Izotina
 * @version 1.0.0
 */
public class PrayersModels {
    private int id;
    private final String namePrayer;
    private final String textPrayer;
    private final int prev;
    private final int next;

    /**
     * Class constructor
     * @param namePrayer String - name of the prayer
     * @param textPrayer String - текст молитвы
     * @param prev int - the text of the prayer
     * @param next int - link to the following prayer
     */
    public PrayersModels(String namePrayer, String textPrayer, int prev, int next) {
        this.namePrayer = namePrayer;
        this.textPrayer = textPrayer;
        this.prev = prev;
        this.next = next;
    }

    /**
     * Class constructor
     * @param id int - prayer id
     * @param namePrayer String - name of the prayer
     * @param textPrayer String - текст молитвы
     * @param prev int - the text of the prayer
     * @param next int - link to the following prayer
     */
    public PrayersModels(int id, String namePrayer, String textPrayer, int prev, int next) {
        this.id = id;
        this.namePrayer = namePrayer;
        this.textPrayer = textPrayer;
        this.prev = prev;
        this.next = next;
    }

    /**
     * @return String - returns the name of the prayer
     */
    public String getNamePrayer() {
        return namePrayer;
    }

    /**
     * @return String - returns the text of the prayer
     */
    public String getTextPrayer() {
        return textPrayer;
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

    public int getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return namePrayer+" : "+textPrayer+" : "+prev+" : "+next;
    }
}
