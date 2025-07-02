package movies.movies.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice  // Tells Spring to use this class for handling exceptions globally
public class GlobalExceptionHandler {

    // Handles validation exceptions when @Valid fails. 
    //Collects all field errors and returns them all together
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // Loops through each field error and adds it to the map
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put("invalid "+error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Handles cases where something is not found (like a genre or movie that doesn't exist)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("ERROR",  ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handles invalid operations like deleting a genre with movies when it's not allowed
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("ERROR", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles errors when a date is in the wrong format 
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<?> handleDateTimeParseException(DateTimeParseException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("ERROR", "Invalid date of birth format. Please use yyyy-MM-dd");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles database errors when trying to save something with a duplicate value
    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<Map<String, String>> handleJpaSystemException(JpaSystemException ex) {
        Map<String, String> response = new HashMap<>();

        // Determine the entity based on the exception message or cause
        String message = ex.getMostSpecificCause().getMessage();

        // Checks which table caused the error and sends a specific message
        if (message.contains("tbl_actor.name")) {
            response.put("ERROR", "Actor with the given name already exists.");
        } else if (message.contains("tbl_genre.name")) {
            response.put("ERROR", "Genre with the given name already exists.");
        } else if (message.contains("tbl_movie.title")) {
            response.put("ERROR", "Movie with the given title already exists.");
        } else {
            response.put("ERROR", "A database error occurred.");
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles errors during saving/updating when validation fails at transaction level
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, String>> handleTransactionSystemException(TransactionSystemException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("ERROR", "The given update was not valid.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}