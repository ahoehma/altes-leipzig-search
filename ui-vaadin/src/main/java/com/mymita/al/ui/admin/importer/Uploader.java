package com.mymita.al.ui.admin.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

public class Uploader<T> implements Receiver, StartedListener, ProgressListener, SucceededListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(Uploader.class);

  private final ProgressBar progressBar;
  private final Importer<T> importer;
  private final UI ui;

  private File file;

  public Uploader(final UI ui, final ProgressBar progressBar, final Importer<T> importer) {
    this.ui = ui;
    this.progressBar = progressBar;
    this.importer = importer;
  }

  @Override
  public OutputStream receiveUpload(final String filename, final String mimeType) {
    LOGGER.debug("Upload received " + filename);
    FileOutputStream fos = null;
    try {
      file = new File(Files.createTempDir(), filename);
      file.deleteOnExit();
      fos = new FileOutputStream(file);
    } catch (final java.io.FileNotFoundException e) {
      new Notification("Could not open file<br/>", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
      return null;
    }
    return fos;
  }

  @Override
  public void updateProgress(final long readBytes, final long contentLength) {
    LOGGER.debug("Upload in progress '{}' from '{}' byte read", readBytes, contentLength);
    ui.access(new Runnable() {

      @Override
      public void run() {
        final float progress = (float) readBytes / contentLength;
        LOGGER.debug("Upload progress '{}'", progress);
        progressBar.setValue(progress);
      }
    });
  }

  @Override
  public void uploadStarted(final StartedEvent event) {
    LOGGER.debug("Upload started " + event.getFilename());
    ui.access(new Runnable() {

      @Override
      public void run() {
        progressBar.setCaption("Uploading");
      }
    });
  }

  @Override
  public void uploadSucceeded(final SucceededEvent event) {
    LOGGER.debug("Upload successful finished " + event.getFilename());
    importer.importData(file, progressBar);
  }
}