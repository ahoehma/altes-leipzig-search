package com.mymita.al.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.NamedIndexRepository;
import org.springframework.stereotype.Repository;

import com.mymita.al.domain.Person;

@Repository
public interface PersonRepository extends GraphRepository<Person>, NamedIndexRepository<Person> {

  /**
   * Find person by {@link Person#getDateOfBirth()}.
   * 
   * @param dateOfBirth
   * @param page
   * @return
   */
  Page<Person> findByDateOfBirth(final Date dateOfBirth, final Pageable page);

  // @Query("START person=node:__types__(className='Person') WHERE person.lastName! = {0} RETURN person")
  // @Query("START person=node:__types__(className='Person') WHERE person.lastName! =~ \"(?i){0}\" RETURN person")
  // @Query("START person=node:__types__(className='Person') WHERE person.lastName! =~ '(?i){0}.*' RETURN person")
  Page<Person> findByLastName(final String lastName, final Pageable page);

  /**
   * Find person by {@link Person#getBirthName(String)} or {@link Person#getLastName()}.
   * 
   * @param name
   * @param page
   * @return
   */
  // @Query("START users=node:Person('*:*') WHERE (users.birthName =~ '(?i){0}' OR users.lastName  =~ '(?i){0}') RETURN users")
  // @Query("START users=node:Person(birthName=~'(?i){0}'),users2=node:Person(lastName=~'(?i){0}') RETURN users, users2")
  @Query("START person=node:__types__(className='Person') WHERE (person.birthName =~ (?i){0} OR person.lastName  =~ '(?i){0}') RETURN person")
  Page<Person> findByLastNameOrBirthName(final String name, final Pageable page);
}