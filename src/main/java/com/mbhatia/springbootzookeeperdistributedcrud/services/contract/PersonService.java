package com.mbhatia.springbootzookeeperdistributedcrud.services.contract;

import com.mbhatia.springbootzookeeperdistributedcrud.models.Person;

import java.util.List;

public interface PersonService {
    List<Person> getPersons();
    void savePerson(Person person);
    void removeAllPeople();
    void saveAllPeople(List<Person> people);
}
