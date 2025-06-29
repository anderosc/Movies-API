package movies.movies.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Genre {

private @Id
@GeneratedValue Long id;
private String name;

public Genre(){}

public Genre(String name){

        this.name = name;
}

public Long getId(){
    return this.id;
}

public String getName(){
    return this.name;
}

public void setName(String name){
    this.name = name;
}

public void setId(Long id){
    this.id = id;
}
}
