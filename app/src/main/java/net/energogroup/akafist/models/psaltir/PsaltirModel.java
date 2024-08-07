package net.energogroup.akafist.models.psaltir;

import androidx.annotation.NonNull;

import java.util.List;

public class PsaltirModel {
    private int id;
    private String name;
    private String desc;
    private List<PsaltirKafismaModel> kafismas;

    public PsaltirModel() { }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setKafismas(List<PsaltirKafismaModel> kafismas) {
        this.kafismas = kafismas;
    }

    public List<PsaltirKafismaModel> getKafismas() {
        return kafismas;
    }

    @NonNull
    @Override
    public String toString() {
        return "PsaltirModel: {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", psaltirKafismas=" + kafismas +
                '}';
    }
}
