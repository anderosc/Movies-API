package movies.movies.Service;

import movies.movies.Entity.Movie;
import movies.movies.Entity.Actor;

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
public class ActorService {
    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ObjectMapper jacksonObjectMapper; //Using objectmapper in the update method

    //Method to create an actor with associated movies
    @Transactional
    public Actor createActor(Actor actor) {
        if (!actor.getMovies().isEmpty()) {
            Set<Movie> existingMovies = new HashSet<>(); //Empty set to hold the existing movies
            for (Movie movie : actor.getMovies()) { //Checks if the movie exists
                Movie existingMovie = movieRepository.findById(movie.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movie.getId()));
                existingMovies.add(existingMovie); //Adds the existing movie to the set
                existingMovie.getActors().add(actor); //Adds the new actor to the existing movie
            }
            actor.setMovies(existingMovies); //Sets the movies to the actor
        }
        actor.setName(actor.getName().trim()); //Trim blank spaces in front and back of the name
        return actorRepository.save(actor);
    }

    //Method to get all actors with pagination
    public Page<Actor> getAllActors(Pageable pageable) {
        return actorRepository.findAll(pageable);
    }

    //Method to get an actor by ID and throw an exception if not found
    public Optional<Actor> getActorById(Long id) {
        return Optional.ofNullable(actorRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + id)));
    }

    //Method to update an existing actor and throw an exception if not found
    @Transactional
    public Optional<Actor> updateActor(Long id, Map<String, Object> updates) {
        Actor existingActor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + id));
        /*
        Using objectmapper to convert the input query's JSON body to a map and use it to confirm if the user is trying to clear the existing
        associations or if they don't want to update the associations
         */
        Actor updatedActor = jacksonObjectMapper.convertValue(updates, Actor.class);
        //Update actor properties only if they're provided
        if (updatedActor.getName() != null) {
            existingActor.setName(updatedActor.getName().trim());
        }
        if (updatedActor.getBirthDate() != null) {
            existingActor.setBirthDate(updatedActor.getBirthDate());
        }

        //Update the associated movies if provided
        if (updates.containsKey("movies") && updatedActor.getMovies().isEmpty()) { //If the "movies" field exists in the given JSON body and it is empty then it clears the associations
            existingActor.getMovies().clear();
        }
        else if (!updatedActor.getMovies().isEmpty()) { //If the "movies" field isnt empty in the JSON body then checks if the movies exist
            Set<Movie> updatedMovies = new HashSet<>();
            for (Movie movie : updatedActor.getMovies()) { //Check if the associated movies exist and throw an exception if not found
                Movie existingMovie = movieRepository.findById(movie.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movie.getId()));
                updatedMovies.add(existingMovie);
            }

            //Remove the actor from movies NOT in the updated list
            for (Movie movie : existingActor.getMovies()) {
                if (!updatedMovies.contains(movie)) {
                    movie.getActors().remove(existingActor);
                }
            }

            // Add the actor to new movies if not already associated
            for (Movie movie : updatedMovies) {
                if (!movie.getActors().contains(existingActor)) {
                    movie.getActors().add(existingActor);
                }
            }

            // Update the actor's movie list
            existingActor.setMovies(updatedMovies);
        }

        Actor savedActor = actorRepository.save(existingActor);
        return Optional.of(savedActor);
    }

    //Method to delete an actor and throw an exception if actor isn't found or they have associated movies
    @Transactional
    public void deleteActor(Long id, boolean force) {
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + id));
        if (!force && !actor.getMovies().isEmpty()) {
            throw new IllegalStateException("Unable to delete actor '" + actor.getName() + "' because they have " + actor.getMovies().size() + " associated movie(s)");
        }
        if (force) { //If force=true then remove the actor from all associated movies
            for (Movie movie : actor.getMovies()) {
                movie.getActors().remove(actor);
            }
            actor.getMovies().clear();
        }
        actorRepository.delete(actor);
    }

    //Method to find actors by name (case-insensitive and partial match)
    public Page<Actor> findByName(String name, Pageable pageable) {
        Page<Actor> actors = actorRepository.findByNameContainingIgnoreCase(name, pageable);
        if (actors.isEmpty()) {
            throw new ResourceNotFoundException("No actors found with name containing: " + name);
        }
        return actors;
    }

    //Method to get all the movies by a specific actor
    public Page<Movie> getMoviesByActorId(Long actor, Pageable pageable) {
        if (!actorRepository.existsById(actor)) {
            throw new ResourceNotFoundException("Actor not found with id: " + actor);
        }
        Page<Movie> movies = movieRepository.findByActorsId(actor, pageable);
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No associated movies found for actor with id: " + actor);
        }
        return movies;
    }

}