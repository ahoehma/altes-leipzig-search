package com.mymita.al.service;

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
import com.mymita.al.repository.PersonRepository;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/context-test.xml" })
public class PersonServiceTest extends AbstractTransactionalTestNGSpringContextTests {

  @Autowired
  transient PersonService personService;
  @Autowired
  transient PersonRepository personRepository;

  @Test
  public void findByLastNameOrBirthNameAndYearOfBirthAndYearOfDeathNeg() throws Exception {
    assertThat(personService.find("Einstein", "1879", "1900"), Matchers.<Person> iterableWithSize(0));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfBirthAndYearOfDeathPos() throws Exception {
    assertThat(personService.find("Einstein", "1879", "1955"), Matchers.<Person> iterableWithSize(1));
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
}