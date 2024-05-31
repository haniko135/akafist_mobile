package net.energogroup.akafist.models.psaltir;

public class SlavaModel {
    private String text;
    private String prayer;

    public SlavaModel() {}

    public void setText(String text) {
        this.text = text;
    }

    public void setPrayer(String prayer) {
        this.prayer = prayer;
    }

    public String getText() {
        return text;
    }

    public String getPrayer() {
        return prayer;
    }
}
