package com.mymita.al.repository;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.annotation.QueryType;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.conversion.EntityResultConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.Person.Gender;

@ContextConfiguration(locations = { "/context-application.xml" })
@TestExecutionListeners({ TransactionalTestExecutionListener.class })
public class PersonRepositoryTest extends AbstractTestNGSpringContextTests {

  @Autowired
  transient PersonRepository           personRepository;
  @Autowired
  transient Neo4jTemplate              neo4jTemplate;
  @Autowired
  transient PlatformTransactionManager transactionManager;

  @Test
  public void findAllByProperty() throws Exception {
    MatcherAssert.assertThat(Lists.newArrayList(personRepository.findAllByPropertyValue("lastName", "Höhmann")).size(), Matchers.is(1));
  }

  @Test
  public void findByDateOfBirth() throws Exception {
    MatcherAssert.assertThat(Lists.newArrayList(personRepository.findByYearOfBirth("1900", null)).size(), Matchers.is(0));
    MatcherAssert.assertThat(Lists.newArrayList(personRepository.findByYearOfBirth("1976", null)).size(), Matchers.is(1));
  }

  @Test
  @Transactional
  public void findByIndexedLastName() throws Exception {
    MatcherAssert.assertThat(Lists.newArrayList(personRepository.findAllByPropertyValue("lastName", "Höhmann")).size(), Matchers.is(1));
  }

  @Test
  public void findByLastName() throws Exception {
    MatcherAssert.assertThat(Lists.newArrayList(personRepository.findByLastName("Höhmann", null)).size(), Matchers.is(1));
  }

  @Test(enabled = false)
  public void findByLastNameIgnoreCaseAndBirthNameLikeIgnoreCase() throws Exception {
    MatcherAssert.assertThat(Lists.newArrayList(personRepository.findByLastNameLikeIgnoreCase("höhmann", null)).size(), Matchers.is(1));
  }

  @Test
  public void findByQuery() throws Exception {
    final String nameValue = "Höhmann";
    final String yearOfBirthValue = "1912";
    final String q = String.format("START person=node:__types__(className='Person') " + "WHERE (person.birthName =~ '(?i)%s' "
        + "OR person.lastName =~ '(?i)%s' OR person.yearOfBirth = '%s') " + "RETURN person", nameValue, nameValue, yearOfBirthValue);
    final List<Person> persons = Lists.newArrayList(neo4jTemplate.getGraphDatabase().queryEngineFor(QueryType.Cypher).query(q, null)
        .to(Person.class, new EntityResultConverter<Object, Person>(neo4jTemplate.getConversionService(), neo4jTemplate)).iterator());
    MatcherAssert.assertThat(persons.size(), Matchers.is(1));
  }

  @BeforeMethod
  public void preChecks() {
    // Neo4jHelper.cleanDb(neo4jTemplate);
    personRepository.save(new Person().firstName("Andreas").lastName("Höhmann").birthName("Höhmann").gender(Gender.MALE).code("0001")
        .yearOfBirth("1976"));
    MatcherAssert.assertThat(personRepository.count(), Matchers.is(1L));
    MatcherAssert.assertThat(Iterables.getFirst(personRepository.findAll(), null).getLastName(), Matchers.is("Höhmann"));
  }
}
