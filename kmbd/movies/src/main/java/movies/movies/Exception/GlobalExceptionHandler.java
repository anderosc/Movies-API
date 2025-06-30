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


@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles validation exceptions when @Valid fails. Collects all field errors and returns them all together
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put("invalid "+error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Handles cases when a requested resource is not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("ERROR",  ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handles invalid operations and also used it for the pagination validation just out of convenience
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("ERROR", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles date parsing exceptions when given an invalid date format in the Actor's birthDate field
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<?> handleDateTimeParseException(DateTimeParseException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("ERROR", "Invalid date of birth format. Please use yyyy-MM-dd");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Handles my database constraints violations to keep given name's and title's unique
    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<Map<String, String>> handleJpaSystemException(JpaSystemException ex) {
        Map<String, String> response = new HashMap<>();

        // Determine the entity based on the exception message or cause
        String message = ex.getMostSpecificCause().getMessage();

        //Customized error message based on the affected entity
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

    //Handles transaction system exceptions when an entity update transaction can't be completed due to an exception
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, String>> handleTransactionSystemException(TransactionSystemException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("ERROR", "The given update was not valid.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}