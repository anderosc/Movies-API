package movies.movies.Entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter //Lombok annotations to automatically generate getters and setters
@Setter
@Entity //Marks this class as a JPA entity
@Table(name = "tbl_genre")
public class Genre {

    // Primary key for the table, ID is generated automatically
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Genre name must be unique, not blank, and between 1–20 characters
    @Column(unique = true)
    @NotBlank(message = "Name must not be null / blank")
    @Size(min = 1, max = 20, message = "Genre name must be between 1 and 20 characters")
    private String name;

    // Many-to-many relationship with movies
    // Ignores the 'genres' and 'actors' fields when converting to JSON to prevent infinite loops
    // Movies are sorted by title in ascending order
    @JsonIgnoreProperties({"genres", "actors"})
    @OrderBy("title ASC") //Sorting the movies alphabetically
    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies = new HashSet<>();

}