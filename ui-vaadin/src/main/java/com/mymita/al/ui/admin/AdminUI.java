package com.mymita.al.ui.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.mymita.al.domain.Christening;
import com.mymita.al.domain.Marriage;
import com.mymita.al.domain.Person;
import com.mymita.al.importer.CountingImportListener;
import com.mymita.al.importer.ImportService;
import com.mymita.al.repository.ChristeningRepository;
import com.mymita.al.repository.MarriageRepository;
import com.mymita.al.repository.PersonRepository;
import com.mymita.al.ui.admin.AdminUI.ImportWorker.Callback;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Configurable
@PreserveOnRefresh
@Theme(Reindeer.THEME_NAME)
public class AdminUI extends UI {

  static class CsvUploader<T> implements Receiver, SucceededListener {

    private static final long serialVersionUID = -8775965777754450989L;

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
    public void uploadSucceeded(final SucceededEvent event) {
      LOGGER.debug("Upload successful finished " + event.getFilename());
      progressBar.setVisible(true);
      UI.getCurrent().setPollInterval(1000);
      importer.importData(file, progressBar);
    }
  }

  interface Importer<T> {
    void importData(File file, ProgressBar progressBar);
  }

  static class ImportWorker<T> extends Thread {

    interface Callback {
      void finishedImport();
    }

    private final ImportService<T> importer;

    private File file;
    private ProgressBar progressBar;

    private Callback callback;

    public ImportWorker(final ImportService<T> importer) {
      this.importer = importer;
    }

    @Override
    public void run() {
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
            UI.getCurrent().access(new Runnable() {

              @Override
              public void run() {
                final float progress = (float) numberOfImportedPersons / (float) max(object);
                LOGGER.debug("Import progress '{}'", progress);
                progressBar.setValue(progress);
              }
            });
          }
        }
      });
      file.delete();
      UI.getCurrent().setPollInterval(-1);
      progressBar.setVisible(false);
    }

    /**
     * @param csvFile
     *          to import
     * @param progressBar
     *          to show the progress of the import
     */
    public void start(final File csvFile, final ProgressBar progressBar, final Callback callback) {
      this.file = csvFile;
      this.progressBar = progressBar;
      this.callback = callback;
      start();
    }
  }

  private static final long serialVersionUID = -2393645969797715398L;
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminUI.class);

  @Autowired
  transient PersonRepository personRepository;
  @Autowired
  transient MarriageRepository marriageRepository;
  @Autowired
  transient ChristeningRepository christeningRepository;
  @Autowired
  transient ImportService<Person> personImportService;
  @Autowired
  transient ImportService<Marriage> marriageImportService;
  @Autowired
  transient ImportService<Christening> christeningImportService;

  private final BeanItemContainer<Person> personContainer = new BeanItemContainer<Person>(Person.class);
  private final BeanItemContainer<Marriage> marriageContainer = new BeanItemContainer<Marriage>(Marriage.class);
  private final BeanItemContainer<Christening> christeningContainer = new BeanItemContainer<Christening>(Christening.class);

  @SuppressWarnings("serial")
  private void addChristeningDeleteAll(final VerticalLayout result) {
    result.addComponent(new NativeButton("Delete all christenings", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        christeningRepository.deleteAll();
      }
    }));
  }

  private void addChristeningImport(final VerticalLayout result) {
    addImporter(result, "Start christening import", christeningImportService, new Callback() {

      @Override
      public void finishedImport() {
        christeningContainer.removeAllItems();
        christeningContainer.addAll(ImmutableList.copyOf(christeningRepository.findAll()));
      }
    });
  }

  @SuppressWarnings("serial")
  private void addChristeningRefresh(final VerticalLayout result) {
    result.addComponent(new NativeButton("Refresh all christenings", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        christeningContainer.removeAllItems();
        christeningContainer.addAll(ImmutableList.copyOf(christeningRepository.findAll()));
      }
    }));
  }

  private void addChristeningTable(final VerticalLayout result) {
    result.addComponent(new Table(null, christeningContainer));
  }

  private <T> void addImporter(final VerticalLayout result, final String caption, final ImportService<T> importService,
      final Callback callback) {
    final ProgressBar progressBar = new ProgressBar();
    progressBar.setVisible(false);
    final CsvUploader<T> receiver = new CsvUploader<T>(progressBar, new Importer<T>() {

      @Override
      public void importData(final File file, final ProgressBar progressBar) {
        new ImportWorker<T>(importService).start(file, progressBar, callback);
      }
    });
    final Upload upload = new Upload(null, receiver);
    upload.setButtonCaption(caption);
    upload.addSucceededListener(receiver);
    final HorizontalLayout c = new HorizontalLayout(upload, progressBar);
    c.setWidth(500, Unit.PIXELS);
    c.setComponentAlignment(upload, Alignment.MIDDLE_LEFT);
    c.setComponentAlignment(progressBar, Alignment.MIDDLE_RIGHT);
    result.addComponent(c);
  }

  @SuppressWarnings("serial")
  private void addMarriageDeleteAll(final VerticalLayout result) {
    result.addComponent(new NativeButton("Delete all marriages", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        marriageRepository.deleteAll();
      }
    }));
  }

  private void addMarriageImport(final VerticalLayout result) {
    addImporter(result, "Start marriage import", marriageImportService, new Callback() {

      @Override
      public void finishedImport() {
        marriageContainer.removeAllItems();
        marriageContainer.addAll(ImmutableList.copyOf(marriageRepository.findAll()));
      }
    });
  }

  @SuppressWarnings("serial")
  private void addMarriageRefresh(final VerticalLayout result) {
    result.addComponent(new NativeButton("Refresh all marriages", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        marriageContainer.removeAllItems();
        marriageContainer.addAll(ImmutableList.copyOf(marriageRepository.findAll()));
      }
    }));
  }

  private void addMarriageTable(final VerticalLayout result) {
    result.addComponent(new Table(null, marriageContainer));
  }

  @SuppressWarnings("serial")
  private void addPersonDeleteAll(final VerticalLayout result) {
    result.addComponent(new NativeButton("Delete all persons", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        personRepository.deleteAll();
        personContainer.removeAllItems();
        personContainer.addAll(ImmutableList.copyOf(personRepository.findAll()));
      }
    }));
  }

  private void addPersonImport(final VerticalLayout result) {
    addImporter(result, "Start person import", personImportService, new Callback() {

      @Override
      public void finishedImport() {
        personContainer.removeAllItems();
        personContainer.addAll(ImmutableList.copyOf(personRepository.findAll()));
      }
    });
  }

  @SuppressWarnings("serial")
  private void addPersonRefresh(final VerticalLayout result) {
    result.addComponent(new NativeButton("Refresh all persons", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        personContainer.removeAllItems();
        personContainer.addAll(ImmutableList.copyOf(personRepository.findAll()));
      }
    }));
  }

  private void addPersonTable(final VerticalLayout result) {
    result.addComponent(new Table(null, personContainer));
  }

  private Component createChristeningTab() {
    final VerticalLayout result = new VerticalLayout();
    addChristeningTable(result);
    addChristeningImport(result);
    addChristeningDeleteAll(result);
    addChristeningRefresh(result);
    return result;
  }

  private Component createContent() {
    final TabSheet ts = new TabSheet();
    ts.addTab(createPersonTab(), "Person");
    ts.addTab(createMarriageTab(), "Marriage");
    ts.addTab(createChristeningTab(), "Christening");
    return new VerticalLayout(ts);
  }

  private Component createMarriageTab() {
    final VerticalLayout result = new VerticalLayout();
    addMarriageTable(result);
    addMarriageImport(result);
    addMarriageDeleteAll(result);
    addMarriageRefresh(result);
    return result;
  }

  private Component createPersonTab() {
    final VerticalLayout result = new VerticalLayout();
    addPersonTable(result);
    addPersonImport(result);
    addPersonDeleteAll(result);
    addPersonRefresh(result);
    return result;
  }

  @Override
  protected void init(final VaadinRequest request) {
    getPage().setTitle("Altes Leipzig Suche - Administration");
    setSizeFull();
    setContent(createContent());
  }
}