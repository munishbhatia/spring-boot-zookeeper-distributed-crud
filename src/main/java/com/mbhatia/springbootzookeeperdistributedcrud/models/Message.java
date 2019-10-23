package com.mbhatia.springbootzookeeperdistributedcrud.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Message {
    private String message;
    List<Person> persons;
}
