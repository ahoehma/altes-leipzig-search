package com.mymita.al.service;

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
import com.mysema.query.types.expr.BooleanExpression;

@Service
public class MarriageService {

  private static final QMarriage $ = QMarriage.marriage;

  @Autowired
  transient MarriageRepository marriageRepository;

  @Transactional(readOnly = true)
  public Iterable<Marriage> find(final String aName, final String aYear) {
    final List<BooleanExpression> predicates = Lists.newArrayList();
    if (!Strings.isNullOrEmpty(aName)) {
      predicates.add(BooleanExpressions.or($.lastNamePerson1.containsIgnoreCase(aName), $.birthNamePerson2.containsIgnoreCase(aName)));
    }
    if (!Strings.isNullOrEmpty(aYear)) {
      predicates.add($.year.eq(aYear));
    }
    if (predicates.isEmpty()) {
      return Lists.<Marriage> newArrayList();
    }
    return marriageRepository.findAll(BooleanExpressions.and(predicates));
  }
}