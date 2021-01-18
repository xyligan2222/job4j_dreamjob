package store;

import model.Candidate;
import model.Post;

import java.sql.SQLException;

public class PsqlMain {
    public static void main(String[] args) throws SQLException {
        StorePost store = PsqlStore.instOf();
        //store.savePost(new Post(0, "Java Job"));
        //store.savePost(new Post(1, "Java got"));

        StoreCandidate storeCandidate = PsqlStore.instOfCandidate();
        //storeCandidate.saveCandidate(new Candidate(1, "Андрей Широков"));
        storeCandidate.saveCandidate(new Candidate(1, "Вадик"));
        storeCandidate.saveCandidate(new Candidate(4, "Вадик"));
        System.out.println(storeCandidate.findByIdCandidate(1).getName());

      /*  for (Post post : store.findAllPosts()) {
            System.out.println(post.getId() + " " + post.getName());
        }
      */
        for (Candidate candidate : storeCandidate.findAllCandidates()) {
            System.out.println(candidate.getId() + " " + candidate.getName());
        }
        /*
        String name = store.findByIdPost(1).getName();
        System.out.println(name);
        store.savePost(new Post(1,"Java GOT"));
        name = store.findByIdPost(1).getName();
        System.out.println(name);
         */
        store.findByIdPost(43);
    }
}