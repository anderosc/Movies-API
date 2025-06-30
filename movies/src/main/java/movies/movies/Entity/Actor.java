package movies.movies.Entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;






@Getter //Lombok annotations to automatically generate getters and setters
@Setter
@Entity //Marks this class as a JPA entity
@Table(name = "tbl_actor")
public class Actor {

    //Marks this as the primary key, and automatically generates the id value
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //I've chosen to only allow unique names in the database
    @Column(unique = true)
    @NotBlank(message = "Name must not be null / blank")
    @Size(min = 3, max = 50, message = "Actor name must be between 3 and 50 characters")
    private String name;

    //Specifying the date format to catch and better handle invalid birthDate inputs
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date of birth must not be null")
    @Past(message = "Date of birth must be in the past")
    private LocalDate birthDate;

    /*
    Prevents infinite recursion during JSON serialization,
    and I've chosen not to show genres in actor related tables
     */
    @JsonIgnoreProperties({"actors", "genres"})
    @OrderBy("title ASC") //Sorting the movies alphabetically
    @ManyToMany(mappedBy = "actors")
    private Set<Movie> movies = new HashSet<>();




}
