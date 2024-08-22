package net.energogroup.akafist.models.azbykaAPI;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/** @noinspection ALL */
public class AbstractDate {
    private ArrayList<Priority> priorities;
    private String date;
    private ArrayList<String> memorialDays;
    private ArrayList<WholeDayText> texts;
    private ArrayList<WholeDayTropariaOrKontakia> tropariaOrKontakias;
    //private WholeDay wholeDay;


    public AbstractDate() {}

    public AbstractDate(String date, ArrayList<String> memorialDays, ArrayList<WholeDayText> texts, ArrayList<WholeDayTropariaOrKontakia> tropariaOrKontakias) {
        this.date = date;
        this.memorialDays = memorialDays;
        this.texts = texts;
        this.tropariaOrKontakias = tropariaOrKontakias;
    }

    public AbstractDate(ArrayList<Priority> priorities) {
        this.priorities = priorities;
    }
//    public AbstractDate(WholeDay wholeDay) {
//        this.wholeDay = wholeDay;
//    }

    public ArrayList<Priority> getPriorities() {
        return priorities;
    }

    public void setPriorities(ArrayList<Priority> priorities) {
        this.priorities = priorities;
    }

//    public WholeDay getWholeDay() {
//        return wholeDay;
//    }
//
//    public void setWholeDay(WholeDay wholeDay) {
//        this.wholeDay = wholeDay;
//    }



    public void setDate(String date) {
        this.date = date;
    }

    public void setMemorialDays(ArrayList<String> memorialDays) {
        this.memorialDays = memorialDays;
    }

    public ArrayList<WholeDayText> getTexts() {
        return texts;
    }

    public void setTexts(ArrayList<WholeDayText> texts) {
        this.texts = texts;
    }

    public ArrayList<WholeDayTropariaOrKontakia> getTropariaOrKontakias() {
        return tropariaOrKontakias;
    }

    public void setTropariaOrKontakias(ArrayList<WholeDayTropariaOrKontakia> tropariaOrKontakias) {
        this.tropariaOrKontakias = tropariaOrKontakias;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<String> getMemorialDays() {
        return memorialDays;
    }

    @NonNull
    @Override
    public String toString() {
        return "AbstractDate: {" +
                "date='" + date + '\'' +
                ", memorialDays=" + memorialDays +
                ", texts=" + texts +
                ", tropariaOrKontakias=" + tropariaOrKontakias +
                '}';
    }
}
