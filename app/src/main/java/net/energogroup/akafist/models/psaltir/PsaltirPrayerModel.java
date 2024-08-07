package net.energogroup.akafist.models.psaltir;

import java.util.List;

public class PsaltirPrayerModel {
    private String name;
    private String text;
    private String kafismaStart;
    private String kafismaEnd;
    private int next;
    private int prev;
    private List<PsalmModel> psalms;

    public PsaltirPrayerModel() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getKafismaStart() {
        return kafismaStart;
    }

    public void setKafismaStart(String kafismaStart) {
        this.kafismaStart = kafismaStart;
    }

    public String getKafismaEnd() {
        return kafismaEnd;
    }

    public void setKafismaEnd(String kafismaEnd) {
        this.kafismaEnd = kafismaEnd;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getPrev() {
        return prev;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }

    public List<PsalmModel> getPsalms() {
        return psalms;
    }

    public void setPsalms(List<PsalmModel> psalms) {
        this.psalms = psalms;
    }
}
