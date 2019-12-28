package com.mymita.al.util;

import java.util.Collection;

import javax.annotation.Nullable;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

public final class BooleanExpressions {

  @Nullable
  public static BooleanExpression and(final Collection<BooleanExpression> exprs) {
    return Expressions.allOf(exprs.toArray(new BooleanExpression[] {}));
  }

  @Nullable
  public static BooleanExpression or(final BooleanExpression... exprs) {
    return Expressions.anyOf(exprs);
  }

  @Nullable
  public static BooleanExpression or(final Collection<BooleanExpression> exprs) {
    return Expressions.anyOf(exprs.toArray(new BooleanExpression[] {}));
  }

}
