package com.mymita.al.importer;

import java.io.File;

public interface ImportService<T> {

  void importData(final File file, final ImportListener<T> importListener);
}
