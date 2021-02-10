package com.testtask.filter.controller;

import com.testtask.filter.data.dto.ContactsResponseDto;
import com.testtask.filter.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;

@Validated
@RestController
@RequestMapping("/hello")
public class HelloController {

    private final ContactService contactService;

    @Autowired
    public HelloController(ContactService contactService) {
        this.contactService = contactService;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/contacts", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<ContactsResponseDto> findContactsByFilter(@RequestParam(name = "nameFilter") @NotBlank String nameFilter) {
        return contactService.findContactsByRegex(nameFilter)
            .collectList()
            .map(ContactsResponseDto::new);
    }
}
