package movies.movies.Controller;
import movies.movies.Repository.*;
import movies.movies.Entity.Genre;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.*;
import movies.movies.Exception.GenreNotFoundException;

import java.util.List;




@RestController
public class GenreController {
    public final GenreRepository repository;

    GenreController(GenreRepository repository){
        this.repository = repository;
    }

    @GetMapping("/genre")
    List<Genre> all() {
        return repository.findAll();
    }
    
    @PostMapping("/genre")
    Genre newGenre(@RequestBody Genre newGenre){
        return repository.save(newGenre);
    }
    
    //Single item
    @GetMapping("/genre/{id}")
    Genre one(@PathVariable Long id) {
    return repository.findById(id)
            .orElseThrow(() -> new GenreNotFoundException(id));
}

   
    

   
    
}
