package com.bronos.hb.model;

import java.lang.Float;
import java.lang.String;

public class Account {
    private long id;
    private String title;
    private Float amount;

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

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return title + ":   " + amount;
    }
}
