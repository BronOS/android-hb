package com.bronos.hb.model;

public class Category {
    private long id;
    private long parentId;
    private long level;
    private String title;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        String space = "";

        for (int x =  0; x < level; x++) {
            space += "-";
        }

        return space + title;
    }
}
