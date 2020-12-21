package model;

import java.util.Objects;

public class HrManager {
    private int id;
    private String name;
    private int age;

    public HrManager(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public HrManager(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public HrManager() {
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

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HrManager)) {
            return false;
        }
        HrManager hrManager = (HrManager) o;
        return id == hrManager.id
                && age == hrManager.age
                && Objects.equals(name, hrManager.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }
}
