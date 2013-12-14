package com.mymita.al.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.annotation.QueryType;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.conversion.EntityResultConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.mymita.al.domain.Person;

@ContextConfiguration(locations = { "/context-application.xml" })
public class PersonRepositoryTest extends AbstractTestNGSpringContextTests {

  @Autowired
  transient PersonRepository personRepository;
  @Autowired
  transient Neo4jTemplate    template;

  @Test
  public void findAllByProperty() throws Exception {
    final List<Person> users = Lists.newArrayList(personRepository.findAllByPropertyValue("lastName", "Höhmann"));
    System.out.println(users);
  }

  @Test
  public void findByDateOfBirth() throws Exception {
    personRepository.findByYearOfBirth("1900", null);
  }

  @Test
  public void findByLastName() throws Exception {
    final List<Person> users = Lists.newArrayList(personRepository.findByLastName("Höhmann", null));
  }

  @Test
  public void findByTemplate() throws Exception {
    final String nameValue = "Höhmann";
    final String yearOfBirthValue = "1912";
    final String q = String.format("START person=node:__types__(className='Person') " + "WHERE (person.birthName =~ '(?i)%s' "
        + "OR person.lastName =~ '(?i)%s' OR person.yearOfBirth! = '%s') " + "RETURN person", nameValue, nameValue, yearOfBirthValue);
    final List<Person> persons = Lists.newArrayList(template.getGraphDatabase().queryEngineFor(QueryType.Cypher).query(q, null)
        .to(Person.class, new EntityResultConverter<Object, Person>(template.getConversionService(), template)).iterator());
    System.out.println(persons);
  }

}
