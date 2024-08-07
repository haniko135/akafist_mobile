package net.energogroup.akafist.models;

import java.util.ArrayList;

public class SkypeBlockAPIModel {
    private SkypeBlockItem item;
    private ArrayList<SkypesConfs> confs;

    public SkypeBlockAPIModel(SkypeBlockItem item, ArrayList<SkypesConfs> confs) {
        this.item = item;
        this.confs = confs;
    }

    public SkypeBlockItem getItem() {
        return item;
    }

    public void setItem(SkypeBlockItem item) {
        this.item = item;
    }

    public ArrayList<SkypesConfs> getConfs() {
        return confs;
    }

    public void setConfs(ArrayList<SkypesConfs> confs) {
        this.confs = confs;
    }
}
