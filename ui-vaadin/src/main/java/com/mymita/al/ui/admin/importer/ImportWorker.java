package com.mymita.al.ui.admin.importer;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mymita.al.importer.CountingImportListener;
import com.mymita.al.importer.ImportService;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;

public class ImportWorker<T> extends Thread {

  public interface Callback {
    void finishedImport();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportWorker.class);

  private final UI ui;
  private final ImportService<T> importer;
  private final File file;
  private final ProgressBar progressBar;
  private final Callback callback;

  public ImportWorker(final UI ui, final ImportService<T> importer, final File file, final ProgressBar progressBar, final Callback callback) {
    this.ui = ui;
    this.importer = importer;
    this.file = file;
    this.progressBar = progressBar;
    this.callback = callback;
  }

  @Override
  public void run() {
    LOGGER.debug("Import data ...");
    importer.importData(file, new CountingImportListener<T>() {

      @Override
      public void finishedImport() {
        super.finishedImport();
        callback.finishedImport();
      }

      @Override
      public void progressImport(final T object) {
        super.progressImport(object);
        final int numberOfImportedPersons = count(object);
        if (numberOfImportedPersons > 0) {
          ui.access(new Runnable() {

            @Override
            public void run() {
              final float progress = numberOfImportedPersons / (float) max(object);
              LOGGER.debug("Import progress '{}'", progress);
              progressBar.setValue(progress);
            }
          });
        }
      }
    });
    file.delete();
    LOGGER.debug("Import data done");
  }
}