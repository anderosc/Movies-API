# Movies API

A REST API for managing a movie database built with **Spring Boot**
---

## Project Overview

This project provides a clean and well-structured API for:

- Managing **movies**, **genres**, and **actors**
- Searching and filtering movies by **genre**, **release year**,  **actor**
- Handling complex **many-to-many relationships**
- Supporting **CRUD operations** with **input validation** and **custom error handling**
- Offering **pagination**, **search**, and **force deletion** options for enhanced control

---

## Technologies Used

- **Spring Boot**
- **Spring Web**
- **Spring Data JPA**
- **SQLite**
- **Postman (for testing)**

---

## Setup and Installation

1. **Clone the repository**
   ```bash
   git clone https://gitea.kood.tech/anderoschutz/kmdb
   cd movies-api

2. **Start the application**
Use Spring Boot Dashboard extencion in VSCode.
Start the application


## Testing
1. **Use Postman**
Open your Postman and import Movie Database API.postman_collection.json

2. **Open a endpoint** 
You can see different endpoints with sample data to test the application.

Example:
```
{
    "title": "Titanic",
    "releaseYear": 2005,
    "duration": 145,
    "genres": [{"id" : 3}],
    "actors": [{"id" : 3}]
}
```
---
### Base Endpoints



#### Genres

- **POST** /api/genres — Create new genre  
- **GET** /api/genres — Retrieve all genres  
- **GET** /api/genres/{id} — Get genre by ID  
- **PATCH** /api/genres/{id} — Update genre name  
- **DELETE** /api/genres/{id}?force=true — Delete genre (with force)



#### Movies

- **POST** /api/movies — Create new movie  
- **GET** /api/movies — Get all movies (pagination supported)  
- **GET** /api/movies/{id} — Get movie by ID  
- **GET** /api/movies/search?title={title} — Search movies by title (partial match)  
- **GET** /api/movies?genre={id} — Filter movies by genre  
- **GET** /api/movies?year={releaseYear} — Filter movies by release year  
- **GET** /api/movies?actor={id} — Filter movies by actor  
- **GET** /api/movies/{id}/actors — Get all actors in a movie  
- **PATCH** /api/movies/{id} — Partially update movie  
- **DELETE** /api/movies/{id}?force=true — Delete movie (with force)



#### Actors

- **POST** /api/actors — Create new actor  
- **GET** /api/actors — Retrieve all actors  
- **GET** /api/actors/{id} — Get actor by ID  
- **GET** /api/actors?name={name} — Filter actors by name (partial match)  
- **PATCH** /api/actors/{id} — Partially update actor details  
- **DELETE** /api/actors/{id}?force=true — Delete actor (with force)


### General Search
- **GET** /api/search?query={searchTerm}  - Find parameter from all entities

