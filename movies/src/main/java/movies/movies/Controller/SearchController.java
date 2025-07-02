package movies.movies.Controller;

import movies.movies.Entity.Actor;
import movies.movies.Entity.Genre;
import movies.movies.Entity.Movie;
import movies.movies.Exception.ResourceNotFoundException;
import movies.movies.Service.ActorService;
import movies.movies.Service.GenreService;
import movies.movies.Service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")  // Base API
public class SearchController {

    @Autowired
    private MovieService movieService;
    @Autowired
    private ActorService actorService;
    @Autowired
    private GenreService genreService;

@GetMapping("/search")  //

// Re-using functions from entities -> colleting all search restults from movies, actors, genres
public ResponseEntity<?> searchAllEntities(@RequestParam String query) {
    //// Pageable object used for pagination. Here, it's set to "unpaged" (need to fetch all results).
    Pageable pageable = Pageable.unpaged();

    //Declare list to store found movies
    List<Movie> movies;
    try {
        // Call service method to find moves by title
        movies = movieService.findByTitle(query, pageable).getContent();
    } catch (ResourceNotFoundException e) {
        movies = Collections.emptyList();
    }

    List<Actor> actors;
    try {
        actors = actorService.findByName(query, pageable).getContent();
    } catch (ResourceNotFoundException e) {
        actors = Collections.emptyList();
    }

    List<Genre> genres;
    try {
        genres = genreService.findByName(query, pageable).getContent();
    } catch (ResourceNotFoundException e) {
        genres = Collections.emptyList();
    }
    /// Mapping data as objects
    
    Map<String, Object> result = new HashMap<>();
    result.put("movies", movies);
    result.put("actors", actors);
    result.put("genres", genres);

    return ResponseEntity.ok(result);
}


}
