package Movies_API.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Movies_API.model.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
