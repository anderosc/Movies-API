package movies.movies.Repository;

import movies.movies.Entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    //Helper methods to add features beyond the given JpaRepository ones
    Page<Movie> findByGenresId(Long genreId, Pageable pageable);
    Page<Movie> findByActorsId(Long actorId, Pageable pageable);
    Page<Movie> findByReleaseYear(Integer releaseYear, Pageable pageable);
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}