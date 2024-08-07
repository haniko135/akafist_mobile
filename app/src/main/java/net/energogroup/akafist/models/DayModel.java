package net.energogroup.akafist.models;

import java.util.ArrayList;

public class DayModel {
    private String date;
    private String dateTxt;
    private String name;
    private ArrayList<TypesModel> types;
    private ArrayList<ServicesModel> services;

    public DayModel(String date, String dateTxt, String name, ArrayList<TypesModel> types, ArrayList<ServicesModel> services) {
        this.date = date;
        this.dateTxt = dateTxt;
        this.name = name;
        this.types = types;
        this.services = services;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateTxt() {
        return dateTxt;
    }

    public void setDateTxt(String dateTxt) {
        this.dateTxt = dateTxt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TypesModel> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<TypesModel> types) {
        this.types = types;
    }

    public ArrayList<ServicesModel> getServices() {
        return services;
    }

    public void setServices(ArrayList<ServicesModel> services) {
        this.services = services;
    }
}
