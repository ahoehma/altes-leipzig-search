package com.mymita.al.importer;

public interface ImportListener<T> {

  void onImport(final T object);

  void startImport(final Class<? extends Object> clazz, final int size);
}