package movies.movies.Service;

import movies.movies.Entity.Genre;
import movies.movies.Entity.Movie;

import movies.movies.Repository.GenreRepository;
import movies.movies.Repository.MovieRepository;

import movies.movies.Exception.ResourceNotFoundException;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class GenreService {
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ObjectMapper jacksonObjectMapper; //Using objectmapper in the update method

    //Method to create a genre with it's associated movies
    @Transactional
    public Genre createGenre (Genre genre) {
        if (!genre.getMovies().isEmpty()) { //Checks if the associated movies exist and throws an exception if not
            Set<Movie> existingMovies = new HashSet<>();
            for (Movie movie : genre.getMovies()) {
                Movie existingMovie = movieRepository.findById(movie.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movie.getId()));
                existingMovies.add(existingMovie); //Adds the movie to the new set
                existingMovie.getGenres().add(genre); //Adds the new genre to the existing movie
            }
            genre.setMovies(existingMovies); //Sets the movies to the genre
        }
        genre.setName(genre.getName().trim()); // Trim blank spaces in front and back of the name
        return genreRepository.save(genre);
    }

    //Method to get all genres with pagination
    public Page<Genre> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    //Method to get a genre by ID and throw an exception if not found
    public Optional<Genre> getGenreById(Long id) {
        return Optional.ofNullable(genreRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id)));
    }

    //Method to update an existing actor and throw an exception if not found
    @Transactional
    public Optional<Genre> updateGenre(Long id, Map<String, Object> updates){
     Genre existingGenre = genreRepository.findById(id)
             .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));
     /*
     Using objectmapper to convert the input query's JSON body to a map and use it to confirm if the user is trying to clear the existing
     associations or if they don't want to update the associations
     */
     Genre updatedGenre = jacksonObjectMapper.convertValue(updates, Genre.class);
     //Update genre name only if it's provided
     if (updatedGenre.getName() != null) {
         existingGenre.setName(updatedGenre.getName().trim());
     }

     //Update the associated movies if provided
        if (updates.containsKey("movies") && updatedGenre.getMovies().isEmpty()) { //If the "movies" field exists in the given JSON body and it is empty then it clears the associations
            existingGenre.getMovies().clear();
        }
        else if (!updatedGenre.getMovies().isEmpty()) { //If the "movies" field isnt empty in the JSON body then checks if the movies exist
         Set<Movie> updatedMovies = new HashSet<>();
         for (Movie movie : updatedGenre.getMovies()) { //Check if the given associated movies exist and throw an exception if not
             Movie existingMovie = movieRepository.findById(movie.getId())
                     .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movie.getId()));
             updatedMovies.add(existingMovie);
         }

         //Remove the genre from movies NOT in the updated list
         for (Movie movie : existingGenre.getMovies()) {
             if (!updatedMovies.contains(movie)) {
                 movie.getGenres().remove(existingGenre);
             }
         }
         //Add the genre to new movies if not already associated
         for (Movie movie : updatedMovies) {
             if (!movie.getGenres().contains(existingGenre)) {
                 movie.getGenres().add(existingGenre);
             }
         }
         //Update the genre's movie list
         existingGenre.setMovies(updatedMovies);
     }
     Genre savedGenre = genreRepository.save(existingGenre);
     return Optional.of(savedGenre);
    }

    //Method to delete a genre and throw an exception if it isn't found or it has associated movies
    @Transactional
    public void deleteGenre(Long id, boolean force) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));
        if (!force && !genre.getMovies().isEmpty()) {
            throw new IllegalStateException("Cannot delete genre '" + genre.getName() + "' because it has " + genre.getMovies().size() + " associated movie(s).");
        }
        if (force) { //If force=true then remove the genre from all associated movies
            for (Movie movie : genre.getMovies()) {
                movie.getGenres().remove(genre);
            }
            genre.getMovies().clear();
        }
        genreRepository.delete(genre);
    }

}