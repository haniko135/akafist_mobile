package net.energogroup.akafist.models.psaltir;

import androidx.annotation.NonNull;

import java.util.List;

public class PsaltirModel {
    private final int id;
    private final String name;
    private final String desc;
    private List<PsaltirKafismaModel> psaltirKafismas;

    public PsaltirModel(int id, String name, String desc, List<PsaltirKafismaModel> psaltirKafismas) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.psaltirKafismas = psaltirKafismas;
    }

    public PsaltirModel(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public List<PsaltirKafismaModel> getPsaltirKafismas() {
        return psaltirKafismas;
    }

    @NonNull
    @Override
    public String toString() {
        return "PsaltirModel: {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", psaltirKafismas=" + psaltirKafismas +
                '}';
    }
}
