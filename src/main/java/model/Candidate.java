package model;

import java.util.Objects;

public class Candidate {
    private int id;
    private String name;
    private int photo_id;
    private int city_id;


    public Candidate(int id, String name, int photo_id, int city_id) {
        this.id = id;
        this.name = name;
        this.photo_id = photo_id;
        this.city_id = city_id;
    }

    public Candidate(int id, String name, int city_id) {
        this.id = id;
        this.name = name;
        this.city_id = city_id;
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

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return id == candidate.id && photo_id == candidate.photo_id && city_id == candidate.city_id && Objects.equals(name, candidate.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, photo_id, city_id);
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", photo_id=" + photo_id +
                ", city_id=" + city_id +
                '}';
    }
}