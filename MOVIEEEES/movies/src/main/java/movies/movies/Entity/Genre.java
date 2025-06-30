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

    //Marks this as the primary key, and automatically generates the id value
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //I've chosen to only allow unique names in the database
    @Column(unique = true)
    @NotBlank(message = "Name must not be null / blank")
    @Size(min = 1, max = 20, message = "Genre name must be between 1 and 20 characters")
    private String name;

    /*
    Prevents infinite recursion during JSON serialization,
    and I've chosen not to show actors in genre related tables
     */
    @JsonIgnoreProperties({"genres", "actors"})
    @OrderBy("title ASC") //Sorting the movies alphabetically
    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies = new HashSet<>();

}