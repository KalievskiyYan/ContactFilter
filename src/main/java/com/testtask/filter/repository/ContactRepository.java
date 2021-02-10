package com.testtask.filter.repository;

import com.testtask.filter.data.entity.Contact;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends ReactiveCrudRepository<Contact, Integer> {

}
