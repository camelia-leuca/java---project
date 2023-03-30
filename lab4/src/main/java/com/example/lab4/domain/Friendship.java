package com.example.lab4.domain;

import java.util.Objects;


public class Friendship extends Entity<Tuple<Long, Long>>{
    String status;
    String date;

    public Friendship(Tuple<Long, Long> id, String status, String date) {
        super(id);
        this.status = status;
        this.date=date;
    }

    public Long getFirstId(){
        return this.getId().getLeft();
    }

    public Long getSecondId(){
        return this.getId().getRight();
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        Friendship that = (Friendship) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStatus(),getDate());
    }
}
