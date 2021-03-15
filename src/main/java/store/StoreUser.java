package store;

import model.User;

import java.util.List;

public interface StoreUser {

    List<User> findAllUser();

    User findUserById(int id);

    User findUserByEmail(String email);

    void save(User user);
}
