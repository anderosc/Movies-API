
package Movies.Service;

import Movies.Entity.GenreEntity;
import Movies.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    @Autowired
    private EmployeeRepository repository;

    public List<GenreEntity> getAllGenres() {
        return repository.findAll();
    }

    public GenreEntity saveGenre(GenreEntity genre) {
        return repository.save(genre);
    }
}
