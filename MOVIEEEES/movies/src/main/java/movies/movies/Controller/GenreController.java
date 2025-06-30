package movies.movies.Controller;

import movies.movies.Entity.Genre;
import movies.movies.Service.GenreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/genres") //Base URL for all endpoints in this controller
public class GenreController {

    @Autowired
    private GenreService genreService;

    //Create a new genre

    @PostMapping
    public ResponseEntity<?> createGenre(@Valid @RequestBody Genre genre) {
            Genre createdGenre = genreService.createGenre(genre);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre); //Specifying the http status
    }

    //function to check if page number is negative

    private void validatePageAndSize(int page, int size) {
        if (page < 0) throw new IllegalStateException("Page number must be 0 or higher");
        if (size > 100) throw new IllegalStateException("Page size must be 100 or less");
    }    

    // Get all genres with pagination
    @GetMapping
    public ResponseEntity<?> getAllGenres(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        // Check that page number is not negative
            validatePageAndSize(page, size);


        // Get genres from service and prepare response
        Page<Genre> genrePage = genreService.getAllGenres(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", genrePage.getContent());
        response.put("totalElements", genrePage.getTotalElements());
        response.put("totalPages", genrePage.getTotalPages());
        response.put("elementsOnThisPage", genrePage.getNumberOfElements());

        return ResponseEntity.ok(response);
    }

    //Get genre by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable Long id) {
            return ResponseEntity.ok(genreService.getGenreById(id));
    }

    //Update an genre actor
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateGenre(@PathVariable Long id,
                                         @RequestBody Map<String, Object> updatedGenre) {;
            return ResponseEntity.ok(genreService.updateGenre(id, updatedGenre));
    }

    //Delete an genre by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenreById(@PathVariable Long id,
                                                @RequestParam(defaultValue = "false") boolean force) {
        genreService.deleteGenre(id, force);
        return ResponseEntity.noContent().build();
    }
}