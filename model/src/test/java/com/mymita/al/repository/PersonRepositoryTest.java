package com.mymita.al.repository;

import static com.mymita.al.repository.PersonRepository.Predicates.hasBirthName;
import static com.mymita.al.repository.PersonRepository.Predicates.hasBirthYear;
import static com.mymita.al.repository.PersonRepository.Predicates.hasDeathYear;
import static com.mymita.al.repository.PersonRepository.Predicates.hasLastName;
import static com.mysema.query.types.expr.BooleanExpression.anyOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.Person.Gender;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/context-test.xml" })
public class PersonRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {

  @Autowired
  transient PersonRepository personRepository;

  @Test
  public void findByLastName() throws Exception {
    assertThat(personRepository.findAll(hasLastName("Höhmann")), Matchers.<Person> iterableWithSize(1));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfBirthAndYearOfDeathNeg() throws Exception {
    assertThat(
        personRepository.findAll(anyOf(hasLastName("Einstein"), hasBirthName("Einstein")).and(hasBirthYear("1879")).and(
            hasDeathYear("1900"))), Matchers.<Person> iterableWithSize(0));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfBirthAndYearOfDeathPos() throws Exception {
    assertThat(
        personRepository.findAll(anyOf(hasLastName("Einstein"), hasBirthName("Einstein")).and(hasBirthYear("1879")).and(
            hasDeathYear("1955"))), Matchers.<Person> iterableWithSize(1));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfBirthNeg() throws Exception {
    assertThat(personRepository.findAll(anyOf(hasLastName("Höhmann"), hasBirthName("höhmann")).and(hasBirthYear("1900"))),
        Matchers.<Person> iterableWithSize(0));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfBirthPos() throws Exception {
    assertThat(personRepository.findAll(anyOf(hasLastName("Höhmann"), hasBirthName("höhmann")).and(hasBirthYear("1976"))),
        Matchers.<Person> iterableWithSize(1));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfDeathNeg() throws Exception {
    assertThat(personRepository.findAll(anyOf(hasLastName("Höhmann"), hasBirthName("höhmann")).and(hasDeathYear("1900"))),
        Matchers.<Person> iterableWithSize(0));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfDeathPos() throws Exception {
    assertThat(personRepository.findAll(anyOf(hasLastName("Einstein"), hasBirthName("Einstein")).and(hasDeathYear("1955"))),
        Matchers.<Person> iterableWithSize(1));
  }

  @Test
  public void findByYearOfBirth() throws Exception {
    assertThat(personRepository.findAll(hasBirthYear("1976")), Matchers.<Person> iterableWithSize(1));
    assertThat(personRepository.findAll(hasBirthYear("1900")), Matchers.<Person> iterableWithSize(0));
  }

  @BeforeTransaction
  public void setupData() throws Exception {
    deleteFromTables("person");
    personRepository.save(new Person().firstName("Andreas").lastName("Höhmann").birthName("Höhmann").gender(Gender.MALE).personCode("0001")
        .yearOfBirth("1976"));
    personRepository.save(new Person().firstName("Albert").lastName("Einstein").birthName("Einstein").gender(Gender.MALE)
        .personCode("0002").yearOfBirth("1879").yearOfDeath("1955"));
    assertThat(personRepository.count(), Matchers.is(2L));
    assertThat(Iterables.getFirst(personRepository.findAll(), null).getLastName(), is("Höhmann"));
  }

  @Test(expectedExceptions = { DataIntegrityViolationException.class }, expectedExceptionsMessageRegExp = ".*constraint \\[UNIQUE_PERSON_CODE\\].*")
  public void unqiuePersonCode() throws Exception {
    // same person code 0001
    personRepository.save(new Person().firstName("Peter").lastName("Lustig").birthName("Lustig").gender(Gender.MALE).personCode("0001")
        .yearOfBirth("1912"));
  }
}
