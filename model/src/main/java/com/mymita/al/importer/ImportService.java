package com.mymita.al.importer;

import org.springframework.core.io.Resource;

public interface ImportService<T> {

  void importData(final Resource resource, final ImportListener<T> importListener);
}
