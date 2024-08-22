package net.energogroup.akafist.models.azbykaAPI;

import androidx.annotation.NonNull;

public class WholeDayText {
    private final int id;
    private final int type;
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

    public WholeDayText(int id, int type, String text, String title, String url) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.title = title;
        this.url = url;
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

    public String getTitle() {
        return title;
    }

    @NonNull
    @Override
    public String toString() {
        return "Text{ \n id=" + id +
                "\n, type=" + type +
                "\n, text=" + text +
                "\n, title=" + title +
                "\n, url=" + url + " }";
    }
}
