package net.energogroup.akafist.models.azbykaAPI;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class WholeDay {
    private String date;
    private ArrayList<String> memorialDays;
    private ArrayList<WholeDayText> texts;
    private ArrayList<WholeDayTropariaOrKontakia> tropariaOrKontakias;

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

    public WholeDay(String date, ArrayList<String> memorialDays) {
        this.date = date;
        this.memorialDays = memorialDays;
    }

    public WholeDay() {}

    @NonNull
    @Override
    public String toString() {
        return "WholeDay: {" +
                "date='" + date + '\'' +
                ", memorialDays=" + memorialDays +
                ", texts=" + texts +
                ", tropariaOrKontakias=" + tropariaOrKontakias +
                '}';
    }
}
