package com.mymita.al.service;

import static com.mymita.al.util.BooleanExpressions.or;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mymita.al.domain.Marriage;
import com.mymita.al.domain.QMarriage;
import com.mymita.al.repository.MarriageRepository;
import com.mymita.al.util.BooleanExpressions;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class MarriageService {

  @Autowired
  transient MarriageRepository marriageRepository;

  @Transactional(readOnly = true)
  public Iterable<Marriage> find(final String name, final String year) {
    final List<BooleanExpression> predicates = Lists.newArrayList();
    if (!Strings.isNullOrEmpty(name)) {
      predicates.add(
          or(QMarriage.marriage.lastNamePerson1.containsIgnoreCase(name), QMarriage.marriage.birthNamePerson2.containsIgnoreCase(name)));
    }
    if (!Strings.isNullOrEmpty(year)) {
      predicates.add(QMarriage.marriage.year.eq(year));
    }
    if (predicates.isEmpty()) {
      return Lists.<Marriage> newArrayList();
    }
    return marriageRepository.findAll(BooleanExpressions.and(predicates));
  }
}
