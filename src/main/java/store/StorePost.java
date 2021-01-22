package store;


import model.Post;

import java.sql.SQLException;
import java.util.Collection;

public interface StorePost {
    Collection<Post> findAllPosts();

    void savePost(Post post);

    Post findByIdPost(int id);
}