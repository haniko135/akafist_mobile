package net.energogroup.akafist.models.psaltir;

public class PsalmModel {
    private final int id;
    private final String name;
    private final String text;
    private SlavaModel slava;

    public PsalmModel(int id, String name, String text, SlavaModel slava) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.slava = slava;
    }

    public PsalmModel(int id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSlava(SlavaModel slava) {
        this.slava = slava;
    }

    public SlavaModel getSlava() {
        return slava;
    }
}
