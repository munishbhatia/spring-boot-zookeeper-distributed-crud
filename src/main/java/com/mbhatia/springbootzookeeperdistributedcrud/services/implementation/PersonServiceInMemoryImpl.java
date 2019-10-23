package com.mbhatia.springbootzookeeperdistributedcrud.services.implementation;

import com.mbhatia.springbootzookeeperdistributedcrud.models.Person;
import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.PersonService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class PersonServiceInMemoryImpl implements PersonService {
    private static List<Person> people = new ArrayList<>(5);

    static {
        people.add(new Person(1, "Person1"));
        people.add(new Person(2,"Person2"));
    }

    @Override
    public List<Person> getPersons() {
        return people;
    }

    @Override
    public void savePerson(Person person) {
        people.add(person);
    }
}
