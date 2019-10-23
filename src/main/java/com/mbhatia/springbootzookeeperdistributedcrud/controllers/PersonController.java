package com.mbhatia.springbootzookeeperdistributedcrud.controllers;

import com.mbhatia.springbootzookeeperdistributedcrud.models.Person;
import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.PersonService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/v1/persons")
public class PersonController {
    private PersonService personService;

    public PersonController(@Qualifier(value = "InMemoryPersonService") PersonService personService){
        this.personService = personService;
    }

    @GetMapping
    public List<Person> getPersons(){
        return personService.getPersons();
    }

    @PostMapping
    public ResponseEntity<String> postPerson(@RequestBody Person person){
        personService.savePerson(person);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private boolean amILeader(){
        return false;
    }
}
