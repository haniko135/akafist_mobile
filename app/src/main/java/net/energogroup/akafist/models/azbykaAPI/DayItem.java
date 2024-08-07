package net.energogroup.akafist.models.azbykaAPI;

public class DayItem {
    private int paragraph;
    private int number;
    private MemorialDay memorialDay;

    public DayItem() {
    }

    public DayItem(int paragraph, int number, MemorialDay memorialDay) {
        this.paragraph = paragraph;
        this.number = number;
        this.memorialDay = memorialDay;
    }

    public int getParagraph() {
        return paragraph;
    }

    public void setParagraph(int paragraph) {
        this.paragraph = paragraph;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public MemorialDay getMemorialDay() {
        return memorialDay;
    }

    public void setMemorialDay(MemorialDay memorialDay) {
        this.memorialDay = memorialDay;
    }
}
