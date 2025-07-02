package movies.movies.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import movies.movies.Entity.Genre;



@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

        //Needed only for main search api
        Page<Genre> findByNameContainingIgnoreCase(String name, Pageable pageable);

}