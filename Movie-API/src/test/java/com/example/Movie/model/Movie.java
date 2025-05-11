package com.example.Movie.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String releaseDate;
    private int duration;
    
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

        public Long getTitle(){
        return id;
    }

    public void setTitle(String title){
        this.title = title;
    }

            public Long getReleaseDate(){
        return id;
    }

    public void setReleaseDate(String releaseDate){
        this.releaseDate = releaseDate;
    }

    public Long getDuration(){
        return id;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }
}
