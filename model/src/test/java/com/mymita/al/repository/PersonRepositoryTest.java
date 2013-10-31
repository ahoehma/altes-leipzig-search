package com.mymita.al.repository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

  private Date asDate(final String value) throws IOException {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      return new SimpleDateFormat("dd.MM.yyyyy hh:mm:ss").parse(String.format("1.1.%s 00:00:00", value));
    } catch (final ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Test
  public void findAllByProperty() throws Exception {
    final List<Person> users = Lists.newArrayList(personRepository.findAllByPropertyValue("lastName", "Höhmann"));
    System.out.println(users);
  }

  @Test
  public void findByDateOfBirth() throws Exception {
    personRepository.findByDateOfBirth(new Date(), null);
  }

  @Test
  public void findByLastName() throws Exception {
    final List<Person> users = Lists.newArrayList(personRepository.findByLastName("Höhmann", null));
    System.out.println(users);
  }

  @Test
  public void findByLastNameOrBirthName() throws Exception {
    final List<Person> users = Lists.newArrayList(personRepository.findByLastNameOrBirthName("Höhmann", null));
    System.out.println(users);
  }

  @Test
  public void findByTemplate() throws Exception {
    // final String q =
    // "START person=node:__types__(className='Person') WHERE (person.birthName =~ '(?i){0}' OR person.lastName =~ '(?i){0}') RETURN person";
    // final String q = String
    // .format(
    // "START person=node:__types__(className='Person') WHERE (person.birthName =~ '(?i)%s' OR person.lastName =~ '(?i)%s') RETURN person",
    // name, name);
    // final Result<Object> result = template.getGraphDatabase().queryEngineFor(QueryType.Cypher).query(q, null);
    // final List<Person> users = Lists.newArrayList(result.to(Person.class,
    // new EntityResultConverter<Object, Person>(template.getConversionService(), template)).iterator());
    final String nameValue = "Höhmann";
    final String yearOfBirthValue = "1912";
    final String q = String.format("START person=node:__types__(className='Person') " + "WHERE (person.birthName =~ '(?i)%s' "
        + "OR person.lastName =~ '(?i)%s' OR person.dateOfBirth! = '%s') " + "RETURN person", nameValue, nameValue,
        asDate(yearOfBirthValue));
    final List<Person> persons = Lists.newArrayList(template.getGraphDatabase().queryEngineFor(QueryType.Cypher).query(q, null)
        .to(Person.class, new EntityResultConverter<Object, Person>(template.getConversionService(), template)).iterator());
    System.out.println(persons);
  }

}
