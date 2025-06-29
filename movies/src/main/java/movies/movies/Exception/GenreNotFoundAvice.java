package movies.movies.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GenreNotFoundAvice {

    @ExceptionHandler(GenreNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String GenreNotFoundHandler(GenreNotFoundException ex){
        return ex.getMessage();
    }
}
