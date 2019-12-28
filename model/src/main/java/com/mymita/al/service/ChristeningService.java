package com.mymita.al.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mymita.al.domain.Christening;
import com.mymita.al.domain.QChristening;
import com.mymita.al.repository.ChristeningRepository;
import com.mymita.al.util.BooleanExpressions;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class ChristeningService {

  @Autowired
  transient ChristeningRepository repository;

  @Transactional(readOnly = true)
  public Iterable<Christening> find(final String name, final String year) {
    final List<BooleanExpression> predicates = Lists.newArrayList();
    if (!Strings.isNullOrEmpty(name)) {
      predicates.add(QChristening.christening.lastNameFather.containsIgnoreCase(name));
    }
    if (!Strings.isNullOrEmpty(year)) {
      predicates.add(QChristening.christening.year.eq(year));
    }
    if (predicates.isEmpty()) {
      return Lists.<Christening> newArrayList();
    }
    return repository.findAll(BooleanExpressions.and(predicates));
  }
}
