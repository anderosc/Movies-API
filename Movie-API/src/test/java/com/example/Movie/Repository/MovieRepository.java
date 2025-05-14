package com.example.Movie.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Movie.model.Movie;

interface MovieRepository extends JpaRepository<Movie, Long> {

}