package com.example.Movie.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Movie.model.Actor;

interface ActorRepository extends JpaRepository<Actor, Long> {

}