package com.mymita.al.util;

import java.util.Collection;

import javax.annotation.Nullable;

import com.mysema.query.types.expr.BooleanExpression;

public final class BooleanExpressions {

  @Nullable
  public static BooleanExpression and(final Collection<BooleanExpression> exprs) {
    return BooleanExpression.allOf(exprs.toArray(new BooleanExpression[] {}));
  }

  @Nullable
  public static BooleanExpression or(final BooleanExpression... exprs) {
    return BooleanExpression.anyOf(exprs);
  }

  @Nullable
  public static BooleanExpression or(final Collection<BooleanExpression> exprs) {
    return BooleanExpression.anyOf(exprs.toArray(new BooleanExpression[] {}));
  }

}
