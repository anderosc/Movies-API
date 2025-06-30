package movies.movies.Controller;


import movies.movies.Entity.Actor;
import movies.movies.Entity.Movie;
import movies.movies.Service.ActorService;
import movies.movies.Service.MovieService;
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
@RequestMapping("/api/movies") //Base URL for all endpoints in this controller
public class MovieController {

    @Autowired
    private MovieService movieService;
    @Autowired
    private ActorService actorService;

    //Endpoint to get all movies with pagination
    @PostMapping
    public ResponseEntity<?> createMovie(@Valid @RequestBody Movie movie) {
        Movie createdMovie = movieService.createMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie); //Specifying the http status
    }
    
        //function to check if page number is negative

       private void validatePageAndSize(int page, int size) {
        if (page < 0) throw new IllegalStateException("Page number must be 0 or higher");
        if (size > 100) throw new IllegalStateException("Page size must be 100 or less");
    }    

    // Get all movies with pagination
    @GetMapping
    public ResponseEntity<?> getAllMoviesOrByGenreByReleaseYearByActor(
            @RequestParam(required = false) Long genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long actor,
            @PageableDefault(sort = "title", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        // Check that page number is not negative
                    validatePageAndSize(page, size);


        Page<Movie> moviePage; //Create an empty page before fetching anything to keep the code more readable

        //Fetches the movies depending on what search parameter has been given
        if (genre != null) {
            moviePage = movieService.getMoviesByGenreId(genre, pageable);
        } else if (year != null) {
            moviePage = movieService.getMoviesByReleaseYear(year, pageable);
        } else if (actor != null) {
            moviePage = actorService.getMoviesByActorId(actor, pageable);
        } else {
            moviePage = movieService.getAllMovies(pageable);
        }

        //Construct the response manually so it looks better
        Map<String, Object> response = new HashMap<>();
        response.put("content", moviePage.getContent());
        response.put("totalElements", moviePage.getTotalElements());
        response.put("totalPages", moviePage.getTotalPages());
        response.put("elementsOnThisPage", moviePage.getNumberOfElements());

        return ResponseEntity.ok(response);
    }

    //Endpoint to get a movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    //Endpoint to update an existing movie
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Long id,
                                         @RequestBody Map<String, Object> updatedMovie) {
        return ResponseEntity.ok(movieService.updateMovie(id, updatedMovie));
    }

    //Endpoint to delete a movie by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovieById(@PathVariable Long id,
                                             @RequestParam(defaultValue = "false") boolean force) {
        movieService.deleteMovie(id, force);
        return ResponseEntity.noContent().build();
    }

    //Endpoint to search for a movie by title
    @GetMapping("/search")
    public ResponseEntity<?> getByTitle(@RequestParam String title,
                                        @PageableDefault(sort = "title", direction = Sort.Direction.ASC) Pageable pageable,
                                        @RequestParam(required = false, defaultValue = "0") int page,
                                        @RequestParam(required = false, defaultValue = "10") int size) {
        
        validatePageAndSize(page, size);

        Page<Movie> moviePage = movieService.findByTitle(title, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", moviePage.getContent());
        response.put("totalElements", moviePage.getTotalElements());
        response.put("totalPages", moviePage.getTotalPages());
        response.put("elementsOnThisPage", moviePage.getNumberOfElements());

        return ResponseEntity.ok(response);
    }

    //Endpoint to get all actors in a specific movie
    @GetMapping("/{id}/actors")
    public ResponseEntity<?> getAllActorsInMovie(@PathVariable Long id,
                                                 @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                                                 @RequestParam(required = false, defaultValue = "0") int page,
                                                 @RequestParam(required = false, defaultValue = "10") int size) {
        if (page < 0) {
            throw new IllegalStateException("Page number must be greater than or equal to 0");
        }
        if (size > 100) {
            throw new IllegalStateException("Page size must be less than or equal to 100");
        }
        Page<Actor> actorPage = movieService.getAllActorsInMovie(id, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", actorPage.getContent());
        response.put("totalElements", actorPage.getTotalElements());
        response.put("totalPages", actorPage.getTotalPages());
        response.put("elementsOnThisPage", actorPage.getNumberOfElements());

        return ResponseEntity.ok(response);
    }
}
