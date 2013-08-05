package com.mymita.al.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Service;

import com.mymita.al.domain.Person;

@Service
public interface PersonRepository extends GraphRepository<Person> {

	Page<Person> findByLastNameLikeAndFirstNameLike(final String lastName,
			final String firstName, final Pageable page);

	Page<Person> findByLastNameLike(final String lastName, final Pageable page);

	Page<Person> findByFirstNameLike(final String lastName, final Pageable page);

	Page<Person> findByDateOfBirth(final Date dateOfBirth, final Pageable page);
}