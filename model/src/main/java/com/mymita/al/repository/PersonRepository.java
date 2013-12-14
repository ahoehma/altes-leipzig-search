package com.mymita.al.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.NamedIndexRepository;
import org.springframework.stereotype.Repository;

import com.mymita.al.domain.Person;

@Repository
public interface PersonRepository extends GraphRepository<Person>, NamedIndexRepository<Person> {

  Page<Person> findByLastName(final String lastName, final Pageable page);

  /**
   * Find person by {@link Person#getYearOfBirth()}.
   * 
   * @param dateOfBirth
   * @param page
   * @return
   */
  Page<Person> findByYearOfBirth(final String yearOfBirth, final Pageable page);
}