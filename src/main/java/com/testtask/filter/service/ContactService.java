package com.testtask.filter.service;

import com.testtask.filter.data.dto.ContactDto;
import com.testtask.filter.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotBlank;
import java.util.regex.Pattern;


@Service
@Validated
public class ContactService {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Transactional(readOnly = true)
    public Flux<ContactDto> findContactsByRegex(@NotBlank String regex) {
        final Pattern pattern = getPattern(regex);
        return contactRepository.findAll()
                .filter(contact -> contact.getName() != null && !pattern.matcher(contact.getName()).matches())
                .map(ContactDto::new);
    }

    private Pattern getPattern(String pattern) {
        try {
            return Pattern.compile(pattern);
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid pattern format!");
        }
    }
}


