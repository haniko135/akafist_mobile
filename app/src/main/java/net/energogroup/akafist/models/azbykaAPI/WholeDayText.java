package net.energogroup.akafist.models.azbykaAPI;

public class WholeDayText {
    private int id;
    private int type;
    private String text;
    private String title;
    private String url;

    public WholeDayText(int id, int type) {
        this.id = id;
        this.type = type;
    }

    public WholeDayText(int id, int type, String text) {
        this.id = id;
        this.type = type;
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
