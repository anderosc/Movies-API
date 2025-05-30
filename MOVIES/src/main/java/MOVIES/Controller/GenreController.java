
package Movies.Controller;

import Movies.Entity.GenreEntity;
import Movies.Service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @GetMapping
    public List<GenreEntity> getAllGenres() {
        return genreService.getAllGenres();
    }

    @PostMapping
    public GenreEntity createGenre(@RequestBody GenreEntity genre) {
        return genreService.saveGenre(genre);
    }
}

