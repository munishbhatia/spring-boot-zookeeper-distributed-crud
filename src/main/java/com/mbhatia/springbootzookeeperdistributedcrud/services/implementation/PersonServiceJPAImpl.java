package com.mbhatia.springbootzookeeperdistributedcrud.services.implementation;

import com.mbhatia.springbootzookeeperdistributedcrud.models.Person;
import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.PersonService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PersonServiceJPAImpl implements PersonService {
    @Override
    public List<Person> getPersons() {
        return null;
    }

    @Override
    public void savePerson(Person person) {

    }

    @Override
    public void removeAllPeople() {

    }

    @Override
    public void saveAllPeople(List<Person> people) {

    }
}
