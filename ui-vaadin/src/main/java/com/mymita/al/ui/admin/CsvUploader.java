package com.mymita.al.ui.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

class CsvUploader<T> implements Receiver, StartedListener, ProgressListener, SucceededListener, FailedListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdminUI.class);

  private File file;
  private final ProgressBar progressBar;
  private final Importer<T> importer;

  public CsvUploader(final ProgressBar progressBar, final Importer<T> importer) {
    this.progressBar = progressBar;
    this.importer = importer;
  }

  @Override
  public OutputStream receiveUpload(final String filename, final String mimeType) {
    LOGGER.debug("Receive upload " + filename);
    try {
      file = new File(Files.createTempDir(), filename);
      file.deleteOnExit();
      UI.getCurrent().setPollInterval(500);
      UI.getCurrent().access(new Runnable() {

        @Override
        public void run() {
          progressBar.setVisible(true);
        }
      });
      return new FileOutputStream(file);
    } catch (final java.io.FileNotFoundException e) {
      LOGGER.error("Could not open file for upload", e);
      Notification.show("Could not open file for upload", e.getMessage(), Notification.Type.ERROR_MESSAGE);
      return null;
    }
  }

  @Override
  public void updateProgress(final long readBytes, final long contentLength) {
    final float progress = (float) readBytes / (float) contentLength;
    LOGGER.debug("Upload progress '{}'", progress);
    UI.getCurrent().access(new Runnable() {

      @Override
      public void run() {
        progressBar.setValue(progress);
      }
    });
  }

  @Override
  public void uploadFailed(final FailedEvent event) {
    LOGGER.error("Upload failed " + event.getFilename());
    Notification.show("Could not upload file", Notification.Type.ERROR_MESSAGE);
    // UI.getCurrent().setPollInterval(-1);
    UI.getCurrent().access(new Runnable() {

      @Override
      public void run() {
        progressBar.setVisible(false);
      }
    });
  }

  @Override
  public void uploadStarted(final StartedEvent event) {
    LOGGER.debug("Upload started " + event.getFilename());
    Notification.show("Upload started", Notification.Type.TRAY_NOTIFICATION);
  }

  @Override
  public void uploadSucceeded(final SucceededEvent event) {
    LOGGER.debug("Upload successful finished " + event.getFilename());
    Notification.show("Upload finished", Notification.Type.TRAY_NOTIFICATION);
    UI.getCurrent().setPollInterval(1000);
    UI.getCurrent().access(new Runnable() {

      @Override
      public void run() {
        progressBar.setVisible(true);
      }
    });
    LOGGER.debug("Start import ...");
    importer.importData(file, progressBar);
    LOGGER.debug("Started import");
  }
}