package movies.movies.Repository;

import movies.movies.Entity.Actor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
    //Helper methods to add features beyond the given JpaRepository ones
    Page<Actor> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Actor> findByMoviesId(Long movieId, Pageable pageable);
}
