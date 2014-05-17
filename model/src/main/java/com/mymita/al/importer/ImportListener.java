package com.mymita.al.importer;

public interface ImportListener<T> {

  void finishedImport();

  void progressImport(final T object);

  void startImport(final Class<? extends Object> clazz, final int size);
}