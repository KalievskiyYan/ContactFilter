package com.testtask.filter.service;

import com.testtask.filter.data.dto.ContactDto;
import com.testtask.filter.data.entity.Contact;
import com.testtask.filter.repository.ContactRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    private static final List<String> NAMES = List.of("Yan", "Ali", "Di", "Nike", "Alyo", "Versh");

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    @Test
    void shouldThrowExceptionByNullRegex() {
        Assertions.assertThrows(HttpClientErrorException.class, () -> contactService.findContactsByRegex(null));
    }
    
    @Test
    void shouldThrowExceptionByInvalidRegex() {
        String regex = "[[";
        Assertions.assertThrows(HttpClientErrorException.class, () -> contactService.findContactsByRegex(regex));
    }

    @Test
    void shouldFindNonExcludedContactsByRegex() {
        String regex = "^A.*$";
        List<Contact> contacts = NAMES.stream()
            .filter(name -> !name.matches(regex)).map(name -> new Contact(null, name))
            .collect(Collectors.toList());

        when(contactRepository.findAll()).thenReturn(Flux.fromStream(contacts.stream()));

        List<ContactDto> expected = contacts.stream().map(ContactDto::new).collect(Collectors.toList());

        StepVerifier
            .create(contactService.findContactsByRegex(regex))
            .expectNextSequence(expected)
            .verifyComplete();

        verify(contactRepository, times(1)).findAll();
    }

    @Test
    void shouldNotFindContactsByRegex() {
        String regex = "^A.*$";
        when(contactRepository.findAll()).thenReturn(Flux.empty());
        Assertions.assertEquals(0, contactService.findContactsByRegex(regex).count().block());
        verify(contactRepository, times(1)).findAll();
    }

}