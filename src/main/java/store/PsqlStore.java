package store;

import model.Photo;
import org.apache.commons.dbcp2.BasicDataSource;
import model.Candidate;
import model.Post;
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

public class PsqlStore implements StorePost, StoreCandidate, StorePhoto {

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
                            it.getInt("photo_id")));
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
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM candidate WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, id);
            ResultSet rslSet = ps.executeQuery();
            if (rslSet.next()) {
                String name = rslSet.getString("name");
                Candidate candidate = new Candidate(id, name);
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
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO candidate(name) VALUES (?) ", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
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
             PreparedStatement ps = cn.prepareStatement("UPDATE candidate SET name = ? WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getId());
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Неверный SQL запрос, указанный кандидат не отредактирован");
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
        return null;
    }

}