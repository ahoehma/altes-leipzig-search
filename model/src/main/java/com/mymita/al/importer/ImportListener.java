package com.mymita.al.importer;

public interface ImportListener<T> {

  void startImport(final int max);
  void progressImport(final T object, final int i, final int max);
  void finishedImport(int count);

  void startDelete(long count);
  void finishedDelete();
}
