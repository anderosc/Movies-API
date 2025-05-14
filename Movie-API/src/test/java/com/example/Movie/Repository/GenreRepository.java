package com.example.Movie.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Movie.model.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {

}
