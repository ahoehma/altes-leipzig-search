package com.mymita.al.ui.admin;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.mymita.al.importer.ImportListener;
import com.mymita.al.importer.ImportService;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

class ImportWorker<T> implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportWorker.class);

  private final File file;
  private final ImportService<T> importService;
  private final ImportListener<T> importListener;

  @SafeVarargs
  public ImportWorker(final ImportService<T> importService, final File csvFile, final ImportListener<T>... importListeners) {
    this.importService = importService;
    this.file = csvFile;
    this.importListener = new ImportListener<T>() {

      @Override
      public void finishedImport() {
        for (final ImportListener<T> l : importListeners) {
          l.finishedImport();
        }
      }

      @Override
      public void progressImport(final T object, final int i, final int max) {
        if (i % 100 == 0) {
          LOGGER.debug("Import progress '{}'/'{}'", i, max);
        }
        for (final ImportListener<T> l : importListeners) {
          l.progressImport(object, i, max);
        }
      }

      @Override
      public void startImport(final int size) {
        for (final ImportListener<T> l : importListeners) {
          l.startImport(size);
        }
      }
    };
  }

  @Override
  public void run() {
    try {
      importService.importData(file, importListener);
    } catch (final DataIntegrityViolationException e) {
      LOGGER.error("Can't successfull import all data", e);
      Notification.show("Import error", e.getMessage(), Type.ERROR_MESSAGE);
    } finally {
      file.delete();
    }
  }
}