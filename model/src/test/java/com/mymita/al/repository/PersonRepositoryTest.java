package com.mymita.al.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

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
    assertThat(personRepository.findByLastName("Höhmann").size(), is(1));
  }

  @Test
  public void findByLastNameContainingAndBirthNameContainingAllIgnoringCase() throws Exception {
    assertThat(personRepository.findByLastNameContainingAndBirthNameContainingAllIgnoringCase("höhmann", "höh").size(), is(1));
    assertThat(personRepository.findByLastNameContainingAndBirthNameContainingAllIgnoringCase("", "höhmann").size(), is(1));
    assertThat(personRepository.findByLastNameContainingAndBirthNameContainingAllIgnoringCase("mann", "").size(), is(1));
  }

  @Test
  public void findByLastNameContainingIgnoreCase() throws Exception {
    assertThat(personRepository.findByLastNameContainingIgnoreCase("höhmann").size(), is(1));
  }

  @Test
  public void findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfBirthAndYearOfDeath() throws Exception {
    assertThat(
        personRepository.findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfBirthAndYearOfDeath("höhmann", "", "",
            "").size(), is(1));
    assertThat(
        personRepository.findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfBirthAndYearOfDeath("öhm", "", "", "")
        .size(), is(1));
    assertThat(
        personRepository.findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfBirthAndYearOfDeath("höh", "", "", "")
        .size(), is(1));
    assertThat(
        personRepository.findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfBirthAndYearOfDeath("höhmann", "",
            "1976", "").size(), is(1));
    assertThat(
        personRepository.findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfBirthAndYearOfDeath("", "höhmann", "",
            "").size(), is(1));
    assertThat(
        personRepository.findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfBirthAndYearOfDeath("", "öhm", "", "")
        .size(), is(1));
    assertThat(
        personRepository.findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfBirthAndYearOfDeath("", "höh", "", "")
        .size(), is(1));
    assertThat(
        personRepository.findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfBirthAndYearOfDeath("", "höhmann",
            "1976", "").size(), is(1));
  }

  @Test
  public void findByPersonCode() throws Exception {
    assertNotNull(personRepository.findByPersonCode("0001"));
    assertNull(personRepository.findByPersonCode("0002"));
  }

  @Test
  public void findByYearOfBirth() throws Exception {
    assertThat(personRepository.findByYearOfBirth("1900").size(), is(0));
    assertThat(personRepository.findByYearOfBirth("1976").size(), is(1));
  }

  @BeforeTransaction
  public void setupData() throws Exception {
    deleteFromTables("person");
    personRepository.save(new Person().firstName("Andreas").lastName("Höhmann").birthName("Höhmann").gender(Gender.MALE).personCode("0001")
        .yearOfBirth("1976"));
    assertThat(personRepository.count(), Matchers.is(1L));
    assertThat(Iterables.getFirst(personRepository.findAll(), null).getLastName(), is("Höhmann"));
  }

  @Test(expectedExceptions = { DataIntegrityViolationException.class }, expectedExceptionsMessageRegExp = ".*constraint \\[UNIQUE_PERSON_CODE\\].*")
  public void unqiuePersonCode() throws Exception {
    // same person code 0001
    personRepository.save(new Person().firstName("Peter").lastName("Lustig").birthName("Lustig").gender(Gender.MALE).personCode("0001")
        .yearOfBirth("1912"));
  }
}
