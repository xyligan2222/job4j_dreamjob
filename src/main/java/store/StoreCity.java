package store;

import model.City;

import java.util.Collection;

public interface StoreCity {
    void saveCity (City city);

    City findCityById (int id);

    City findByNameCity (String name);

    Collection<City> findAllCity();
}
