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
import com.mysema.query.types.expr.BooleanExpression;

@Service
public class ChristeningService {

  private static final QChristening $ = QChristening.christening;

  @Autowired
  transient ChristeningRepository repository;

  @Transactional(readOnly = true)
  public Iterable<Christening> find(final String aName, final String aYear) {
    final List<BooleanExpression> predicates = Lists.newArrayList();
    if (!Strings.isNullOrEmpty(aName)) {
      predicates.add($.lastNameFather.containsIgnoreCase(aName));
    }
    if (!Strings.isNullOrEmpty(aYear)) {
      predicates.add($.year.eq(aYear));
    }
    if (predicates.isEmpty()) {
      return Lists.<Christening> newArrayList();
    }
    return repository.findAll(BooleanExpressions.and(predicates));
  }
}