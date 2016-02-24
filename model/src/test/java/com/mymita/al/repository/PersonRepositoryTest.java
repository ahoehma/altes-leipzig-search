package com.mymita.al.repository;

import static com.mymita.al.domain.QPerson.person;
import static com.mysema.query.types.expr.BooleanExpression.anyOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
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
    assertThat(personRepository.findAll(person.lastName.containsIgnoreCase("Höhmann")), Matchers.<Person> iterableWithSize(1));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfBirthAndYearOfDeathNeg() throws Exception {
    assertThat(
        personRepository.findAll(anyOf(person.lastName.containsIgnoreCase("Einstein"), person.birthName.containsIgnoreCase("Einstein"))
            .and(person.yearOfBirth.eq("1879")).and(person.yearOfDeath.eq("1900"))),
        Matchers.<Person> iterableWithSize(0));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfBirthAndYearOfDeathPos() throws Exception {
    assertThat(
        personRepository.findAll(anyOf(person.lastName.containsIgnoreCase("Einstein"), person.birthName.containsIgnoreCase("Einstein"))
            .and(person.yearOfBirth.eq("1879")).and(person.yearOfDeath.eq("1955"))),
        Matchers.<Person> iterableWithSize(1));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfBirthNeg() throws Exception {
    assertThat(personRepository.findAll(anyOf(person.lastName.containsIgnoreCase("Höhmann"), person.birthName.containsIgnoreCase("Höhmann"))
        .and(person.yearOfBirth.eq("1900"))), Matchers.<Person> iterableWithSize(0));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfBirthPos() throws Exception {
    assertThat(personRepository.findAll(anyOf(person.lastName.containsIgnoreCase("Höhmann"), person.birthName.containsIgnoreCase("Höhmann"))
        .and(person.yearOfBirth.eq("1976"))), Matchers.<Person> iterableWithSize(1));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfDeathNeg() throws Exception {
    assertThat(personRepository.findAll(anyOf(person.lastName.containsIgnoreCase("Höhmann"), person.birthName.containsIgnoreCase("Höhmann"))
        .and(person.yearOfDeath.eq("1900"))), Matchers.<Person> iterableWithSize(0));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfDeathPos() throws Exception {
    assertThat(
        personRepository.findAll(anyOf(person.lastName.containsIgnoreCase("Einstein"), person.birthName.containsIgnoreCase("Einstein"))
            .and(person.yearOfDeath.eq("1955"))),
        Matchers.<Person> iterableWithSize(1));
  }

  @Test
  public void findByYearOfBirth() throws Exception {
    assertThat(personRepository.findAll(anyOf(person.yearOfBirth.eq("1976"))), Matchers.<Person> iterableWithSize(1));
    assertThat(personRepository.findAll(anyOf(person.yearOfBirth.eq("1955"))), Matchers.<Person> iterableWithSize(0));
  }

  @BeforeTransaction
  public void setupData() throws Exception {
    deleteFromTables("person");
    personRepository.save(Person.builder().firstName("Andreas").lastName("Höhmann").birthName("Höhmann").gender(Gender.MALE)
        .personCode("0001").yearOfBirth("1976").build());
    personRepository.save(Person.builder().firstName("Albert").lastName("Einstein").birthName("Einstein").gender(Gender.MALE)
        .personCode("0002").yearOfBirth("1879").yearOfDeath("1955").build());
    assertThat(personRepository.count(), Matchers.is(2L));
    assertThat(Iterables.getFirst(personRepository.findAll(), null).getLastName(), is("Höhmann"));
  }

  // @Test(expectedExceptions = { DataIntegrityViolationException.class }, expectedExceptionsMessageRegExp = ".*constraint
  // \\[UNIQUE_PERSON_CODE\\].*")
  @Test
  public void unqiuePersonCode() throws Exception {
    // same person code 0001
    personRepository.save(Person.builder().firstName("Peter").lastName("Lustig").birthName("Lustig").gender(Gender.MALE).personCode("0001")
        .yearOfBirth("1912").build());
  }
}
