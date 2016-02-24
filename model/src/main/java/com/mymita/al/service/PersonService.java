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
import com.mysema.query.types.expr.BooleanExpression;

@Service
public class PersonService {

  private static final QPerson $ = QPerson.person;

  @Autowired
  transient PersonRepository personRepository;

  public Iterable<Person> find(final String aName, final String aBirthYear, final String aDeathYear) {
    final List<BooleanExpression> predicates = Lists.newArrayList();
    if (!Strings.isNullOrEmpty(aName)) {
      predicates.add(BooleanExpressions.or($.lastName.containsIgnoreCase(aName), $.birthName.containsIgnoreCase(aName)));
    }
    if (!Strings.isNullOrEmpty(aBirthYear)) {
      predicates.add($.yearOfBirth.eq(aBirthYear));
    }
    if (!Strings.isNullOrEmpty(aDeathYear)) {
      predicates.add($.yearOfDeath.eq(aDeathYear));
    }
    if (predicates.isEmpty()) {
      return Lists.<Person> newArrayList();
    }
    return personRepository.findAll(BooleanExpressions.and(predicates));
  }
}