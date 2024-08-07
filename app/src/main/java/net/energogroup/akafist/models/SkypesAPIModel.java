package net.energogroup.akafist.models;

import java.util.ArrayList;

public class SkypesAPIModel {
    private ArrayList<SkypesConfs> confs;
    private ArrayList<SkypesConfs> blocks;

    public SkypesAPIModel(ArrayList<SkypesConfs> confs, ArrayList<SkypesConfs> blocks) {
        this.confs = confs;
        this.blocks = blocks;
    }

    public ArrayList<SkypesConfs> getConfs() {
        return confs;
    }

    public void setConfs(ArrayList<SkypesConfs> confs) {
        this.confs = confs;
    }

    public ArrayList<SkypesConfs> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<SkypesConfs> blocks) {
        this.blocks = blocks;
    }
}
