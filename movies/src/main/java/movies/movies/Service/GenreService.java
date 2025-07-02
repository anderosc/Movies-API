package movies.movies.Service;

import movies.movies.Entity.Actor;
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

    //Creates a new Genre and links it with existing movies if provided
    @Transactional
    public Genre createGenre (Genre genre) {
        if (!genre.getMovies().isEmpty()) { // Check that each associated movie exists and update their genre references
            Set<Movie> existingMovies = new HashSet<>();
            for (Movie movie : genre.getMovies()) {
                Movie existingMovie = movieRepository.findById(movie.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movie.getId()));
                existingMovies.add(existingMovie); //Adds the movie to the new set
                existingMovie.getGenres().add(genre); ///Link this genre to the movie
            }
            genre.setMovies(existingMovies); //Sets the movies to the genre
        }
        genre.setName(genre.getName().trim()); // Trim blank spaces in front and back of the name
        return genreRepository.save(genre); // Save to database
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


    // Convert incoming raw map to Genre object (used for extracting updated fields)
     Genre updatedGenre = jacksonObjectMapper.convertValue(updates, Genre.class);
     //If a new name is provided, update it 
     if (updatedGenre.getName() != null) {
         existingGenre.setName(updatedGenre.getName().trim());
     }

        /*
         * Handle movie associations:
         * If "movies" field exists and is empty → clear all associations.
         * Else → validate and update new movie associations.
         */   
        if (updates.containsKey("movies") && updatedGenre.getMovies().isEmpty()) { //If the "movies" field exists in the given JSON body and it is empty then it clears the associations
            existingGenre.getMovies().clear();
        }
        else if (!updatedGenre.getMovies().isEmpty()) {
             //If the "movies" field isnt empty in the JSON body then checks if the movies exist
         Set<Movie> updatedMovies = new HashSet<>();

         for (Movie movie : updatedGenre.getMovies()) { 
            //Check if the given associated movies exist and throw an exception if not
             Movie existingMovie = movieRepository.findById(movie.getId())
                     .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movie.getId()));
             updatedMovies.add(existingMovie);
         }

         // Remove this genre from movies that are no longer associated         
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

        //Method to find actors by name (case-insensitive and partial match)
    public Page<Genre> findByName(String name, Pageable pageable) {
        Page<Genre> genre = genreRepository.findByNameContainingIgnoreCase(name, pageable);
        if (genre.isEmpty()) {
            throw new ResourceNotFoundException("No genres found with name containing: " + name);
        }
        return genre;
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