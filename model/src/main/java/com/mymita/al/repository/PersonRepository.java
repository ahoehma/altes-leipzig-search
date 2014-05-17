package com.mymita.al.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mymita.al.domain.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

  /**
   * Finds {@link Person persons} by using the last name as a search criteria.
   *
   * @param lastName
   *
   * @return A list of persons which last name is an exact match with the given last name. If no persons is found, this method returns an
   *         empty list.
   */
  List<Person> findByLastName(String lastName);

  /**
   * Finds {@link Person persons} by using the last name and the birth name as a search criteria.
   *
   * @param lastName
   * @param birthName
   *
   * @return A list of persons which last name and birth name match with the given names, both case insensitiv. If no persons is found, this
   *         method returns an empty list.
   */
  List<Person> findByLastNameContainingAndBirthNameContainingAllIgnoringCase(String lastName, String birthName);

  /**
   * Finds {@link Person persons} by using the last name as a search criteria.
   *
   * @param lastName
   *
   * @return A list of persons which last name match with the given last name, case insensitiv. If no persons is found, this method returns
   *         an empty list.
   */
  List<Person> findByLastNameContainingIgnoreCase(String lastName);

  List<Person> findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfBirth(String lastName, String birthName,
      String yearOfBirth);

  List<Person> findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfBirthAndYearOfDeath(String lastName,
      String birthName, String yearOfBirth, String yearOfDeath);

  List<Person> findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfDeath(String lastName, String birthName,
      String yearOfDeath);

  /**
   * Find an {@link Person person} by using the unique person code as a search criteria.
   *
   * @param personCode
   *
   * @return a person which person code is an exact match with the given code. If no person is found, this method returns <code>null</code>
   */
  Person findByPersonCode(String personCode);

  List<Person> findByYearOfBirth(String yearOfBirth);

  List<Person> findByYearOfBirthOrYearOfDeath(String yearOfBirth, String yearOfDeath);
}