package com.mymita.al.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mymita.al.domain.Person;
import com.mymita.al.domain.QPerson;
import com.mysema.query.types.expr.BooleanExpression;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long>, QueryDslPredicateExecutor<Person> {

  public final static class Predicates {

    public static BooleanExpression hasBirthName(final String theBirthName) {
      return QPerson.person.birthName.containsIgnoreCase(theBirthName);
    }

    public static BooleanExpression hasBirthYear(final String theBirthYear) {
      return QPerson.person.yearOfBirth.eq(theBirthYear);
    }

    public static BooleanExpression hasDeathYear(final String theDeathYear) {
      return QPerson.person.yearOfDeath.eq(theDeathYear);
    }

    public static BooleanExpression hasLastName(final String theLastName) {
      return QPerson.person.lastName.containsIgnoreCase(theLastName);
    }
  }
}