package com.testtask.filter.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactsResponseDto {
    @JsonProperty("contacts")
    private List<ContactDto> contacts;
}
