package movies.movies.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// This custom exception is used when something is not found 
// It automatically returns a 404 Not Found HTTP status when thrown

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // Constructor that takes a message describing what was not found
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
