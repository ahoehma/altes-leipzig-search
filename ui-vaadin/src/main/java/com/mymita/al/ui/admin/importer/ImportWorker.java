package com.mymita.al.ui.admin.importer;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import com.mymita.al.importer.ImportListener;
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

  public ImportWorker(final UI ui, final ImportService<T> importer, final File file, final ProgressBar progressBar,
      final Callback callback) {
    this.ui = ui;
    this.importer = importer;
    this.file = file;
    this.progressBar = progressBar;
    this.callback = callback;
  }

  @Override
  public void run() {
    LOGGER.debug("Import data ...");
    importer.importData(new FileSystemResource(file), new ImportListener<T>() {

      @Override
      public void finishedImport() {
        callback.finishedImport();
      }

      @Override
      public void progressImport(final T object, final int i, final int max) {
        if (i > 0) {
          ui.access(new Runnable() {

            @Override
            public void run() {
              final float progress = i / max;
              LOGGER.debug("Import progress '{}'", progress);
              progressBar.setValue(progress);
            }
          });
        }
      }

      @Override
      public void startImport(final int max) {
      }

    });
    file.delete();
    LOGGER.debug("Import data done");
  }
}