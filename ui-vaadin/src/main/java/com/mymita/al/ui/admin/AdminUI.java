package com.mymita.al.ui.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.Person.Gender;
import com.mymita.al.importer.CountingImportListener;
import com.mymita.al.importer.PersonImportService;
import com.mymita.al.repository.PersonRepository;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
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

  static class CsvUploader implements Receiver, SucceededListener {

    private static final long serialVersionUID = -8775965777754450989L;

    private File file;
    private final ProgressBar progressBar;

    public CsvUploader(final ProgressBar progressBar) {
      this.progressBar = progressBar;
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
      LOGGER.debug("Upload successful finished" + event.getFilename());
      progressBar.setVisible(true);
      UI.getCurrent().setPollInterval(1000);
      new PersonImportWorker(file, progressBar).start();
    }
  }

  @Configurable
  static class PersonImportWorker extends Thread {

    private final File file;
    private final ProgressBar progressBar;

    @Autowired
    transient PersonImportService importer;

    /**
     * @param csvFile
     *          to import
     * @param progressBar
     *          to show the progress of the import
     */
    public PersonImportWorker(final File csvFile, final ProgressBar progressBar) {
      this.file = csvFile;
      this.progressBar = progressBar;
    }

    @Override
    public void run() {
      try {
        importer.importPersons(file, new CountingImportListener<Person>() {

          @Override
          public void onImport(final Person object) {
            super.onImport(object);
            if (object instanceof Person) {
              final int numberOfImportedPersons = count(object);
              if (numberOfImportedPersons > 0) {
                UI.getCurrent().accessSynchronously(new Runnable() {

                  @Override
                  public void run() {
                    final float progress = (float) numberOfImportedPersons / (float) max(object);
                    LOGGER.debug("Import progress '{}'", progress);
                    progressBar.setValue(progress);
                  }
                });
              }
            }
          }
        });
      } catch (final IOException e) {
        new Notification("Import failed<br/>", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
      }
      file.delete();
      UI.getCurrent().setPollInterval(-1);
      progressBar.setVisible(false);
    }
  }

  private static final long serialVersionUID = -2393645969797715398L;
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminUI.class);

  @Autowired
  transient PersonRepository personRepository;

  private void addCreatePerson(final VerticalLayout result) {
    final FormLayout addLayout = new FormLayout();
    final TextField code = new TextField("Code");
    addLayout.addComponent(code);
    final TextField firstName = new TextField("FirstName");
    addLayout.addComponent(firstName);
    final TextField lastName = new TextField("LastName");
    addLayout.addComponent(lastName);
    final ComboBox gender = new ComboBox("Gender", Lists.newArrayList(Person.Gender.MALE, Person.Gender.FEMALE));
    gender.setConverter(Gender.class);
    addLayout.addComponent(gender);
    final TextField description = new TextField("Description");
    addLayout.addComponent(description);
    final DateField dateOfBirth = new DateField("DateOfBirth");
    addLayout.addComponent(dateOfBirth);
    final DateField dateOfDeath = new DateField("DateOfDeath");
    addLayout.addComponent(dateOfDeath);
    addLayout.addComponent(new NativeButton("Add", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        // final Person p = new Person();
        // p.setCode(code.getValue());
        // p.setFirstName(firstName.getValue());
        // p.setLastName(lastName.getValue());
        // p.setGender((Gender) gender.getValue());
        // p.setDescription(description.getValue());
        // p.setDateOfBirth(SimpleDateFormat.getInstance().format(
        // dateOfBirth.getValue()));
        // p.setDateOfDeath(SimpleDateFormat.getInstance().format(
        // dateOfDeath.getValue()));
        // service.save(p);
      }
    }));
    result.addComponent(addLayout);
  }

  private void addCsvUpload(final VerticalLayout result) {
    final ProgressBar progressBar = new ProgressBar();
    progressBar.setVisible(false);
    final CsvUploader receiver = new CsvUploader(progressBar);
    final Upload upload = new Upload(null, receiver);
    upload.setButtonCaption("Start Import");
    upload.addSucceededListener(receiver);
    final HorizontalLayout c = new HorizontalLayout(upload, progressBar);
    c.setWidth(500, Unit.PIXELS);
    c.setComponentAlignment(upload, Alignment.MIDDLE_LEFT);
    c.setComponentAlignment(progressBar, Alignment.MIDDLE_RIGHT);
    result.addComponent(c);
  }

  private void addDeleteAll(final VerticalLayout result) {
    result.addComponent(new NativeButton("Delete ALL", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        personRepository.deleteAll();
      }
    }));
  }

  private void addPersonTable(final VerticalLayout result) {
    result.addComponent(new Table(null, new BeanItemContainer<Person>(Person.class, ImmutableList.copyOf(personRepository.findAll()))));
  }

  private Component createContent() {
    final VerticalLayout result = new VerticalLayout();
    addPersonTable(result);
    addCreatePerson(result);
    addCsvUpload(result);
    addDeleteAll(result);
    return result;
  }

  @Override
  protected void init(final VaadinRequest request) {
    getPage().setTitle("Altes Leipzig Suche - Administration");
    setSizeFull();
    setContent(createContent());
  }
}