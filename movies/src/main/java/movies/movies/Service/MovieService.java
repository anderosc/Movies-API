package movies.movies.Service;

import movies.movies.Entity.Genre;
import movies.movies.Entity.Movie;
import movies.movies.Entity.Actor;

import movies.movies.Repository.GenreRepository;
import movies.movies.Repository.ActorRepository;
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
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private ObjectMapper jacksonObjectMapper; //Using objectmapper in the update method

    //Method to create a movie with it's associated actors/genres
    @Transactional
    public Movie createMovie (Movie movie){
        if (!movie.getActors().isEmpty()) { //Checks if the associated actors exist and throws an exception if not
            Set<Actor> existingActors = new HashSet<>();
            for (Actor actor : movie.getActors()) {
                Actor existingActor = actorRepository.findById(actor.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + actor.getId()));
                existingActors.add(existingActor); //If actor is found then it's added to the new set
                existingActor.getMovies().add(movie); //Adds the new movie to the existing actor
            }
            movie.setActors(existingActors); //Sets the actors to the new movie
        }
        if (!movie.getGenres().isEmpty()) { //Checks if the associated genres exist and throws an exception if not
            Set<Genre> existingGenres = new HashSet<>();
            for (Genre genre : movie.getGenres()) {
                Genre existingGenre = genreRepository.findById(genre.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genre.getId()));
                existingGenres.add(existingGenre); //If genre is found then it's added to the new set
                existingGenre.getMovies().add(movie); //Add the new movie to the existing genre
            }
            movie.setGenres(existingGenres); //Sets the genres to the new movie
        }
        movie.setTitle(movie.getTitle().trim()); //Trim blank spaces in front and back of the title
        return movieRepository.save(movie);
    }

    //Method to get all movies with pagination
    public Page<Movie> getAllMovies (Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    //Method to get a movie by ID and throw an exception if not found
    public Optional<Movie> getMovieById(Long id) {
        return Optional.ofNullable(movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id)));
    }

    //Method to get all movies by genre ID and with pagination
    public Page<Movie> getMoviesByGenreId(Long genre, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByGenresId(genre, pageable);
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("Movies not found with genre id: " + genre);
        }
        return movies;
    }
    //Method to get all movies by release year and with pagination
    public Page<Movie> getMoviesByReleaseYear(int releaseYear, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByReleaseYear(releaseYear, pageable);
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("Movies not found with release year: " + releaseYear);
        }
        return movies;
    }
    //Method to get all the actors in a specific movie with pagination
    public Page<Actor> getAllActorsInMovie(Long movieId, Pageable pageable) {
        if (!movieRepository.existsById(movieId)) {
     //Firstly checks if the movie exists in the movie repository
            throw new ResourceNotFoundException("Movie not found with id: " + movieId);
        }
        Page<Actor> actors = actorRepository.findByMoviesId(movieId, pageable); 
    //If movie exists then calls the actor list from the actor repository
        if (actors.isEmpty()) { 
    //Throws an exception if no actors have been associated with the given movie
            throw new ResourceNotFoundException("No associated actors found for movie with id: " + movieId);
        }
        return actors;
    }

    //Method to update an existing movie and throw an exception if not found
    @Transactional
    public Optional<Movie> updateMovie(Long id, Map<String, Object> updates) {
        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        
        /*
        Using objectmapper to convert the input query's JSON body to a map and use it to confirm if the user is trying to clear the existing
        associations or if they don't want to update the associations
         */
        Movie updatedMovie = jacksonObjectMapper.convertValue(updates, Movie.class);


        //Update the movie properties if provided
        if (updatedMovie.getTitle() != null) {
            existingMovie.setTitle(updatedMovie.getTitle().trim());
        }
        if (updatedMovie.getReleaseYear() != null) {
            existingMovie.setReleaseYear(updatedMovie.getReleaseYear());
        }
        if (updatedMovie.getDuration() != null) {
            existingMovie.setDuration(updatedMovie.getDuration());
        }

        //Update the associated genres if provided
        if (updates.containsKey("genres") && updatedMovie.getGenres().isEmpty()) { //If the "genres" field exists in the given JSON body and its empty then it clears associations
            existingMovie.getGenres().clear();
        }
         else if (!updatedMovie.getGenres().isEmpty()) {
            Set<Genre> updatedGenres = new HashSet<>();
            for (Genre genre : updatedMovie.getGenres()) { //Checks if the given genres exist
                Genre existingGenre = genreRepository.findById(genre.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genre.getId()));
                updatedGenres.add(existingGenre);
            }
            existingMovie.getGenres().clear(); //Clears the previous genres and adds all the new genres
            existingMovie.getGenres().addAll(updatedGenres);
        }


        //Update the associated actors if provided
        if (updates.containsKey("actors") && updatedMovie.getActors().isEmpty()) { //If the "actors" field exists in the given JSON body and its empty then it clears associations
            existingMovie.getActors().clear();
        }
        if (!updatedMovie.getActors().isEmpty()) {
            Set<Actor> updatedActors = new HashSet<>();
            for (Actor actor : updatedMovie.getActors()) { //Checks if the given actors exist
                Actor existingActor = actorRepository.findById(actor.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + actor.getId()));
                updatedActors.add(existingActor);
            }
            existingMovie.getActors().clear(); //Clears all the previous actors and add all the new actors
            existingMovie.getActors().addAll(updatedActors);
        }
        return Optional.of(existingMovie);
    }

    
    //Method to delete a movie and throw an exception if movie isn't found or it has associated genres or actors
    @Transactional
    public void deleteMovie(Long id, boolean force) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        if (!force && (!movie.getGenres().isEmpty() || !movie.getActors().isEmpty())) {
            throw new IllegalStateException("Unable to delete movie '" + movie.getTitle() + "' because it has associated genre(s) or actor(s)");
        }
        if (force) { //If force=true then removes the movie from all the associated genres and actors
            for (Genre genre : movie.getGenres()) {
                genre.getMovies().remove(movie);
            }
            for (Actor actor : movie.getActors()) {
                actor.getMovies().remove(movie);
            }
            movie.getGenres().clear();
            movie.getActors().clear();
        }
        movieRepository.delete(movie);
    }

    //Method to find movies by title (case-insensitive and partial match)
    public Page<Movie> findByTitle (String title, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByTitleContainingIgnoreCase(title, pageable);
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found with title containing: " + title);
        }
        return movies;
    }

}