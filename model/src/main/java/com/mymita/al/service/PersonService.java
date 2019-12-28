package com.mymita.al.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.QPerson;
import com.mymita.al.repository.PersonRepository;
import com.mymita.al.util.BooleanExpressions;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class PersonService {

  @Autowired
  transient PersonRepository personRepository;

  public Iterable<Person> find(final String name, final String birthYear, final String deathYear) {
    final List<BooleanExpression> predicates = Lists.newArrayList();
    if (!Strings.isNullOrEmpty(name)) {
      predicates
          .add(BooleanExpressions.or(QPerson.person.lastName.containsIgnoreCase(name), QPerson.person.birthName.containsIgnoreCase(name)));
    }
    if (!Strings.isNullOrEmpty(birthYear)) {
      predicates.add(QPerson.person.yearOfBirth.eq(birthYear));
    }
    if (!Strings.isNullOrEmpty(deathYear)) {
      predicates.add(QPerson.person.yearOfDeath.eq(deathYear));
    }
    if (predicates.isEmpty()) {
      return Lists.<Person> newArrayList();
    }
    return personRepository.findAll(BooleanExpressions.and(predicates));
  }
}
