package model;

import java.util.Objects;

public class Candidate {
    private int id;
    private String name;
    private int photo_id;


    public Candidate(int id, String name, int photo_id) {
        this.id = id;
        this.name = name;
        this.photo_id = photo_id;
    }

    public Candidate(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
    }


    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return id == candidate.id && photo_id == candidate.photo_id && name.equals(candidate.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, photo_id);
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", photo_id=" + photo_id +
                '}';
    }
}