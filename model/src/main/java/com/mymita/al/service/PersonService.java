package com.mymita.al.service;

import static com.mysema.query.types.expr.BooleanExpression.anyOf;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mymita.al.domain.Person;
import com.mymita.al.repository.PersonRepository;
import com.mymita.al.repository.PersonRepository.Predicates;
import com.mysema.query.types.expr.BooleanExpression;

@Service
public class PersonService {

  @Nullable
  static BooleanExpression allOf(final Collection<BooleanExpression> exprs) {
    BooleanExpression rv = null;
    for (final BooleanExpression b : exprs) {
      rv = rv == null ? b : rv.and(b);
    }
    return rv;
  }

  @Autowired
  transient PersonRepository personRepository;

  public Iterable<Person> find(final String aName, final String aBirthYear, final String aDeathYear) {
    final List<BooleanExpression> predicates = Lists.newArrayList();
    if (!Strings.isNullOrEmpty(aName)) {
      predicates.add(anyOf(Predicates.hasLastName(aName), Predicates.hasBirthName(aName)));
    }
    if (!Strings.isNullOrEmpty(aBirthYear)) {
      predicates.add(Predicates.hasBirthYear(aBirthYear));
    }
    if (!Strings.isNullOrEmpty(aDeathYear)) {
      predicates.add(Predicates.hasDeathYear(aDeathYear));
    }
    if (predicates.isEmpty()) {
      return Lists.newArrayList();
    }
    return personRepository.findAll(allOf(predicates));
  }
}