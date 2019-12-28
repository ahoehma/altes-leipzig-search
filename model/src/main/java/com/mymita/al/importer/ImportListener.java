package com.mymita.al.importer;

public interface ImportListener<T> {

  void finishedImport(int count);

  void progressImport(final T object, final int i, final int max);

  void startImport(final int max);
}
