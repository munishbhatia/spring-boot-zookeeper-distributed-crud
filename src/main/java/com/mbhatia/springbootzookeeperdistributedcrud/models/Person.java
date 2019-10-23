package com.mbhatia.springbootzookeeperdistributedcrud.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @JsonProperty(value = "id")
    private int Id;
    private String name;
}
