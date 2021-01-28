package store;

import model.Photo;

import java.util.Collection;

public interface StorePhoto {
    Collection<Photo> findAllPhoto();

    Photo savePhoto(Photo photo);

    Photo findByIdPhoto(int id);
}
