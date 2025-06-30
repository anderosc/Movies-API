package movies.movies.Controller;
import movies.movies.Entity.Actor;
import movies.movies.Service.ActorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/actors") //Base URL for all endpoints in this controller
public class ActorController {

    @Autowired
    private ActorService actorService;

    //Create a new actor
    @PostMapping
    public ResponseEntity<?> createActor(@Valid @RequestBody Actor actor) {
            Actor createdActor = actorService.createActor(actor);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdActor); //// Return 201 Created status
    }

    //function to check if page number is negative
    private void validatePageAndSize(int page, int size) {
        if (page < 0) throw new IllegalStateException("Page number must be 0 or higher");
        if (size > 100) throw new IllegalStateException("Page size must be 100 or less");
    }

    // Get all actors with pagination
    @GetMapping
    public ResponseEntity<?> getAllActors(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        // Check that page number is not negative
        validatePageAndSize(page, size);

        // Get actors from service and prepare response
        Page<Actor> actorPage = actorService.getAllActors(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", actorPage.getContent());
        response.put("totalElements", actorPage.getTotalElements());
        response.put("totalPages", actorPage.getTotalPages());
        response.put("elementsOnThisPage", actorPage.getNumberOfElements());

        return ResponseEntity.ok(response);
    }

    //Get actor by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getActorById(@PathVariable Long id) {
            return ResponseEntity.ok(actorService.getActorById(id));
    }

    //Update an existing actor
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateActor(@PathVariable Long id,
                                         @RequestBody Map<String, Object> updatedActor) {
                return ResponseEntity.ok(actorService.updateActor(id, updatedActor));
    }

    //Delete an actor by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActorById(@PathVariable Long id,
                                             @RequestParam(defaultValue = "false") boolean force) {
        actorService.deleteActor(id, force);
        return ResponseEntity.noContent().build();
    }

    
    //Search for actors by name
    @GetMapping("/search")
    public ResponseEntity<?> getActorByName(@RequestParam String name,
                                            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                                            @RequestParam(required = false, defaultValue = "0") int page,
                                            @RequestParam(required = false, defaultValue = "10") int size) {
       // Check page number and size limits
        validatePageAndSize(page, size);
        
        // Search actors by name and prepare response
        Page<Actor> actorPage = actorService.findByName(name, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", actorPage.getContent());
        response.put("totalElements", actorPage.getTotalElements());
        response.put("totalPages", actorPage.getTotalPages());
        response.put("elementsOnThisPage", actorPage.getNumberOfElements());

        return ResponseEntity.ok(response);
    }
}