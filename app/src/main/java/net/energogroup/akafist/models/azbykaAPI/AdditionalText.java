package net.energogroup.akafist.models.azbykaAPI;

public class AdditionalText{
    private final int id;
    private final int type;
    private final String text;
    private final String title;
    private final String url;

    public AdditionalText(int id, int type, String text, String title, String url) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.title = title;
        this.url = url;
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

    public String getUrl() {
        return url;
    }
}
