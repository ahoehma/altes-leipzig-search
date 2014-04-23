package com.mymita.al.importer;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

public class CountingImportListener<T> implements ImportListener<T> {

  private final Map<Class<? extends Object>, Integer> count = Maps.newHashMap();
  private final Map<Class<? extends Object>, Integer> max = Maps.newHashMap();

  final protected int count(final T object) {
    return count.get(object.getClass());
  }

  final protected int max(final T object) {
    return max.get(object.getClass());
  }

  @Override
  public void onImport(final T object) {
    count.put(object.getClass(), 1 + Objects.firstNonNull(count.get(object.getClass()), 0));
  }

  @Override
  public void startImport(final Class<? extends Object> clazz, final int size) {
    count.put(clazz, 0);
    max.put(clazz, size);
  }
}