package movies.movies.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

        @Getter //Lombok annotations to automatically generate getters and setters
        @Setter
        @Entity //Marks this class as a JPA entity
        @Table(name = "tbl_movie") // The table in the database will be named 'tbl_movie'
        public class Movie {

        // Unique ID for each movie, automatically generated
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Movie title must be unique, not empty or blank, and between 1–100 characters
        @Column(unique = true)
        @NotBlank(message = "Title must not be null / blank.")
        @Size(min = 1, max = 100, message = "Movie title must be between 1 and 100 characters.")
        private String title;

        @NotNull(message = "Release year must not be null.")
        @Min(value = 1900, message = "Release year must be between 1900 and 2100.")
        @Max(value = 2100, message = "Release year must be between 1900 and 2100.")
        private Integer releaseYear;
                
        @NotNull(message = "Duration must not be null.")
        @Min(value = 1, message = "Duration must be between 1 and 1000.")
        @Max(value = 1000, message = "Duration must be between 1 and 1000.")
        private Integer duration;

        // Many-to-many relationship with genres
        // Ignores the 'movies' field inside Genre when converting to JSON to avoid infinite loops
        // Genres are sorted by name in ascending order
        @JsonIgnoreProperties("movies") 
        @NotEmpty
        @ManyToMany
        @OrderBy("name ASC") //Sorting the genres alphabetically
        @JoinTable(
                name = "movie_genres",
                joinColumns = @JoinColumn(name = "movie_id", referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(name = "genre_id", referencedColumnName = "id")
                )
        private Set<Genre> genres = new HashSet<>();

        @JsonIgnoreProperties("movies") //Prevents infinite recursion during JSON serialization
        @ManyToMany
        @OrderBy("name ASC") //Sorting the actors alphabetically
        @JoinTable(
                name = "movie_actors",
                joinColumns = @JoinColumn(name = "movie_id", referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(name = "actor_id", referencedColumnName = "id")
                )
        private Set<Actor> actors = new HashSet<>();


}
