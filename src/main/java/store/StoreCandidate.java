package store;

import model.Candidate;

import java.sql.SQLException;
import java.util.Collection;

public interface StoreCandidate {

    Collection<Candidate> findAllCandidates();

    void saveCandidate(Candidate candidate);

    Candidate findByIdCandidate(int id);
}
