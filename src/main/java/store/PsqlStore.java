package store;

import model.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.impl.Log4JLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PsqlStore implements StorePost, StoreCandidate, StorePhoto, StoreUser, StoreCity {

    private final BasicDataSource pool = new BasicDataSource();

    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());

     /*
     This method connects to the Postgres database
      */

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader("db.properties")
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final PsqlStore INST = new PsqlStore();

    }

    public static PsqlStore instOf() {
        return Lazy.INST;
    }

    /*
     table post in Database
     This method searches for all vacancies
     @return Collection with Posts
      */
    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Неверный SQL запрос, Вакансии не найдены");
        }
        return posts;
    }

    /*
     table candidate in Database
     This method searches for all Candidates
     @return Collection with candidates
      */
    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidate = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM candidate ")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidate.add(new Candidate(it.getInt("id"), it.getString("name"),
                            it.getInt("photo_id"), it.getInt("city_id")));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, кандидаты не найдены");
        }
        return candidate;
    }

    @Override
    public void saveCandidate(Candidate candidate) {
        if (candidate.getId() == 0) {
            create(candidate);
        } else {
            update(candidate);
        }
    }

    /*
     table candidate in Database
     This method searches Candidate by id
     @return Candidate if exists with this id
      */
    @Override
    public Candidate findByIdCandidate(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM candidate as c left join city c2 on c2.id = c.city_id WHERE c.id = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, id);
            ResultSet rslSet = ps.executeQuery();
            if (rslSet.next()) {
                String name = rslSet.getString("name");
                int city = rslSet.getInt("city_id");
                Candidate candidate = new Candidate(id, name, city);
                return candidate;
            } else {
                LOG.error("Кандидат с указанным id не найден");
            }
            return null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, Кандидат с указанным id не найден");
        }
        return null;
    }

    /*
     table photo
     This method searches Photo by id
     @return Photo if exists with this id
      */
    @Override
    public Photo findByIdPhoto(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM photo WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, id);
            ResultSet rslSet = ps.executeQuery();
            if (rslSet.next()) {
                String name = rslSet.getString("name");
                Photo photo = new Photo(id, name);
                return photo;
            } else {
                LOG.error("Фото с указанным id не найдено");
            }
            return null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, Фото с указанным id не найдено");
        }
        return null;
    }
    /*
     table users
     This method searches User by id
     @return User if exists with this id
      */

    @Override
    public User findUserById(int id) {
        User user = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT name FROM users WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            String name = resultSet.getString("name");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            user = new User(id, name, email, password);
            return user;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, Пользователь с указанным id не найден");
        }
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        if ( email == null && !email.equals("")) {
            LOG.info("Email не содержит символов");
            return null;
        }
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM users WHERE email = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, email);
            ResultSet resultSet = ps.executeQuery();
            var result = ps.getResultSet();
            if ( result == null) {
                return null;
            }
            resultSet.next();
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String emails = resultSet.getString("email");
            String password = resultSet.getString("password");
            User user = new User(id, name, emails, password);
            return user;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, Пользователь с указанным Email не найден");
        }

        return null;
    }


    /*
     table candidate
     This method delete Candidate by id
     if true then deleted row id in table photo
     @return true if delete successfully
      */

    @Override
    public boolean deleteCandidate(Candidate candidate) {
        if (candidate == null) {
            LOG.error("Указанного кандидата не существует");
            return false;
        }
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE from candidate WHERE id = (?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, candidate.getId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, указанный кандидат не был удален");
        }
        return false;
    }

    @Override
    public void savePost(Post post) {
        if (post.getId() == 0) {
            create(post);
        } else {
            update(post);
        }

    }

    @Override
    public Photo savePhoto(Photo photo) {
        if (photo.getId() == 0) {
            create(photo);
        } else {
            update(photo);
        }
        return photo;
    }

    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            create(user);
        } else {
            update(user);
        }
    }

    @Override
    public void saveCity(City city) {
        if (city.getId() == 0) {
            create(city);
        } else {
            update(city);
        }
    }

    /*
     table post
     This method create post
     @return Post
      */

    private Post create(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO post(name) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, указанная вакансия не была создана");
        }

        return post;
    }

    /*
     table candidate
     This method create Candidate
     @return Candidate
      */
    private Candidate create(Candidate candidate) {
        System.out.println("save" + candidate);
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO candidate(name, city_id) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            //ps.setInt(2, 0);
            ps.setInt(2, candidate.getCity_id());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, указанный кандидат не был создан");

        }
        return candidate;
    }

    /*
     table photo
     This method create Object photo
     @return photo
      */

    private Photo create(Photo photo) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO photo (name) VALUES (?) ", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, photo.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    photo.setId(id.getInt(1));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, Фото не добавлено");

        }
        return photo;
    }
    /*
     table city
     This method create Object city
     @return city
      */

    private City create(City city) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO city (name) VALUES (?) ", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, city.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    city.setId(id.getInt(1));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, Город не добавлен");

        }
        return city;
    }

    /*
     table users
     This method create Object user
     @return user
      */

    private User create(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO users(name, email, password) VALUES (?, ?, ?) ",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    /*
     table post
     This method update post if exist id
      */

    private void update(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE post SET name = ? WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.setInt(2, post.getId());
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Неверный SQL запрос, указанная вакансия не отредактирована");
        }
    }

    /*
     table candidate
     This method update Candidate if exist id
      */

    private void update(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE candidate SET name = ?, city_id = ? WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getCity_id());
            ps.setInt(3, candidate.getId());

            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, указанный кандидат не отредактирован");
        }
    }

    /*
     table users
     This method update USERS if exist id
      */

    private void update(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?")
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getId());
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Неверный SQL запрос, указанный пользователь не отредактирован");
        }
    }

    /*
     table candidate
     This method update Candidate with photo if exist id
      */
    public void updateCandidateWithPhoto(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE candidate SET photo_id = ? WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, candidate.getPhoto_id());
            ps.setInt(2, candidate.getId());
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, указанный кандидат не отредактирован");
        }
    }
    /*
     table photo
     This method update Photo if exist id
      */

    private void update(Photo photo) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE photo SET name = ? WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, photo.getName());
            ps.setInt(2, photo.getId());
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Неверный SQL запрос, указанная фотогафия не отредактирована");
        }

    }
    /*
     table city
     This method update City if exist id
      */

    private void update(City city) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE city SET name = ? WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, city.getName());
            ps.setInt(2, city.getId());
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Неверный SQL запрос, указанный город не отредактирован");
        }

    }
    /*
     table post
     This method search post by id
     return post
     */

    @Override
    public Post findByIdPost(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, id);
            ResultSet rslSet = ps.executeQuery();
            if (rslSet.next()) {
                String name = rslSet.getString("name");
                Post post = new Post(id, name);
                return post;
            } else {
                LOG.error("Вакансия с указанным id не найдена");

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, Вакансия с указанным id не найдена");
        }
        return null;
    }
    /*
     table city
     This method search city by id
     return city
     */

    @Override
    public City findCityById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM city WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, id);
            ResultSet rslSet = ps.executeQuery();
            if (rslSet.next()) {
                String name = rslSet.getString("name");
                City city = new City(id, name);
                return city;
            } else {
                LOG.error("Город с указанным id не найден");
            }
            return null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, Город с указанным id не найден");
        }
        return null;
    }
    /*
     table city
     This method search city by name
     return city
     */
    @Override
    public City findByNameCity(String name) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM city WHERE name = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, name);
            ResultSet rslSet = ps.executeQuery();
            if (rslSet.next()) {
                int id = rslSet.getInt("id");
                City city = new City(id, name);
                return city;
            } else {
                LOG.error("Город с указанным названием не найден");
            }
            return null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, Город с указанным названием не найден");
        }
        return null;
    }
    /*
     table city
     This method search all city
     return Collection city
     */

    @Override
    public Collection<City> findAllCity() {
        List<City> city = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM city")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    city.add(new City(it.getInt("id"), it.getString("name")));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, города не найдены");
        }
        return city;
    }
    /*
     table photo
     This method search all photo
     return Collection photo
     */
    @Override
    public Collection<Photo> findAllPhoto() {
        List<Photo> photo = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM photo")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    photo.add(new Photo(it.getInt("id"), it.getString("name")));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, фото не найдены");
        }
        return photo;
    }
    /*
     table user
     This method search all user
     return Collection user
     */
    @Override
    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM photo WHERE id = (?)")
        ) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    users.add(new User(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("password")
                    ));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, пользователи не найдены");
        }
        return users;
    }


}