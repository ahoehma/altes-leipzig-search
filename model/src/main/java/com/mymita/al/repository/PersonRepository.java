package com.mymita.al.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.NamedIndexRepository;
import org.springframework.stereotype.Repository;

import com.mymita.al.domain.Person;

@Repository
public interface PersonRepository extends GraphRepository<Person>, NamedIndexRepository<Person> {

  /**
   * @param lastName
   * @param page
   * @return
   */
  Page<Person> findByLastName(final String lastName, final Pageable page);

  /**
   * @param lastName
   * @param birthName
   * @param page
   * @return
   * 
   * @deprecated ignore-case is not supported - https://jira.springsource.org/browse/DATAGRAPH-420
   */
  @Deprecated
  Page<Person> findByLastNameLikeIgnoreCase(final String lastName, final Pageable page);

  /**
   * @param lastName
   * @param birthName
   * @param page
   * @return
   * 
   * @deprecated ignore-case is not supported - https://jira.springsource.org/browse/DATAGRAPH-420
   */
  @Deprecated
  Page<Person> findByLastNameLikeIgnoreCaseAndBirthNameLikeIgnoreCase(final String lastName, String birthName, final Pageable page);

  /**
   * Find person by {@link Person#getYearOfBirth()}.
   * 
   * @param yearOfBirth
   *          as 4 character string, i.e "1871"
   * @param page
   * @return
   */
  Page<Person> findByYearOfBirth(final String yearOfBirth, final Pageable page);
}