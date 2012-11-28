package com.bronos.hb.model;

public class Type {
    private int id;
    private String title;

    public Type(int id, String title) {
        setId(id);
        setTitle(title);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        return title;
    }
}
