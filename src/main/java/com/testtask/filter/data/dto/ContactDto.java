package com.testtask.filter.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testtask.filter.data.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;

    public ContactDto(Contact contact) {
        this.id = contact.getId();
        this.name = contact.getName();
    }
}
