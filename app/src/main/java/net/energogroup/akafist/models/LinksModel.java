package net.energogroup.akafist.models;

/**
 * Класс-сущность, описывающий основные параметры аудиозаписи
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class LinksModel {
    private int id;
    private String url;
    private String name;
    private int image;

    public LinksModel() { }

    /**
     * Конструктор класса LinksModel
     * @param id int - индивидуальный номер записи
     * @param url String - ссылка на запись
     * @param name String - имя записи
     */
    public LinksModel(int id, String url, String name, int image) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.image = image;
    }

    public LinksModel(String url, String name) {
        this.url = url;
        this.name = name;
    }

    /**
     * Конструктор класса LinksModel
     * @param url String - ссылка на запись
     * @param name String - имя записи
     */
    public LinksModel(String url, String name, int image){
        this.url = url;
        this.name = name;
        this.image = image;
    }

    /**
     * @return int - Возвращает поле id
     */
    public int getId() {
        return id;
    }

    /**
     * @return String - Возвращает поле url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return String - Возвращает поле name
     */
    public String getName() {
        return name;
    }


    public int getImage() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
