package com.mymita.al.admin.ui;

import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static java.lang.Math.floor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.unit.DataSize;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import com.mymita.al.domain.Person;
import com.mymita.al.domain.Person.Gender;
import com.mymita.al.domain.QPerson;
import com.mymita.al.importer.ImportListener;
import com.mymita.al.importer.ImportService;
import com.mymita.al.repository.PersonRepository;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "person", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Person")
public class PersonView extends VerticalLayout {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersonView.class);

  @Autowired
  transient ImportService<Person> importService;
  @Autowired
  transient ImportServiceRunner importServiceRunner;
  @Autowired
  transient PersonRepository repository;

  private final Grid<Person> grid = new Grid<>(Person.class);
  private final FilterablePageableDataProvider<Person, Object> dataProvider = createDataProvider();
  private final MenuBar menuBar = new MenuBar();
  private final Text counterText = new Text("");

  public PersonView() {
    // note: direct access to autowired beans is not possible during construction-time!
    setId("person-view");
    grid.setId("list");
    grid.addThemeVariants(LUMO_NO_BORDER);
    grid.setSelectionMode(Grid.SelectionMode.MULTI);
    grid.setItemDetailsRenderer(TemplateRenderer.<Person> of(
        "<div>"
            + "<div>Bemerkung:[[item.description]]</div>"
            + "<div>Quelle:[[item.reference]]</div>"
            + "<div>Bildquelle:[[item.image]]</div>"
            + "<div>Link:[[item.link]]</div>"
            + "</div>")
        .withProperty("description", Person::getDescription)
        .withProperty("reference", Person::getReference)
        .withProperty("link", Person::getLink)
        .withProperty("image", Person::getImage)
        .withEventHandler("handleClick", grid.getDataProvider()::refreshItem));
    grid.setDataProvider(dataProvider);
    grid.setColumns();
    grid.addColumn(Person::getPersonCode).setHeader("PersCode").setFlexGrow(0).setWidth("150px").setResizable(false);
    grid.addColumn(Person::getLastName).setHeader("Name");
    grid.addColumn(Person::getFirstName).setHeader("Vorname");
    grid.addColumn(Person::getBirthName).setHeader("GebName");
    grid.addColumn(TemplateRenderer.<Person> of("[[item.gender]]")
        .withProperty("gender", person -> person.getGender() == Gender.MALE ? "Mann" : "Frau"))
        .setHeader("Sex")
        .setFlexGrow(0).setWidth("100px").setResizable(false);
    grid.addColumn(Person::getYearOfBirth).setHeader("JahrGeb").setFlexGrow(0).setWidth("100px").setResizable(false);
    grid.addColumn(Person::getYearOfDeath).setHeader("JahrGest").setFlexGrow(0).setWidth("100px").setResizable(false);
    grid.addColumn(Person::getYearsOfLife).setHeader("Jahre").setFlexGrow(0).setWidth("100px").setResizable(false);
    add(menuBar);
    setFlexGrow(0, menuBar);
    add(grid);
    setFlexGrow(1, grid);
    grid.setHeightFull();
    setHeightFull();
  }

  private FilterablePageableDataProvider<Person, Object> createDataProvider() {
    return new FilterablePageableDataProvider<Person, Object>() {

      @Override
      protected Page<Person> fetchFromBackEnd(final Query<Person, Object> query, final Pageable pageable) {
        return repository.findAll(pageable);
      }

      @Override
      protected int sizeInBackEnd(final Query<Person, Object> query) {
        return (int) repository.count();
      }

      @Override
      protected List<QuerySortOrder> getDefaultSortOrders() {
        final List<QuerySortOrder> sortOrders = new ArrayList<>();
        sortOrders.add(new QuerySortOrder(QPerson.person.personCode.getMetadata().getName(), SortDirection.ASCENDING));
        return sortOrders;
      }
    };
  }

  @Override
  protected void onAttach(final AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    fillMenu();
    counterText.setText(repository.count() + " Personen vorhanden");
  }

  private void fillMenu() {
    menuBar.removeAll();
    menuBar.addItem(counterText);
    menuBar.addItem("Alle Zeigen", e -> grid.getDataProvider().refreshAll());
    menuBar.addItem("Neu Importieren", e -> {
      final UI ui = e.getSource().getUI().get();
      final Dialog dialog = new Dialog();
      dialog.setCloseOnEsc(true);
      dialog.setCloseOnOutsideClick(false);
      addImporter(ui, dialog);
      dialog.open();
    });
  }

  void addImporter(final UI ui, final Dialog dialog) {
    final ProgressBar progressBarImport = new ProgressBar(0, 100);
    progressBarImport.setVisible(false);
    final VerticalLayout progressText = new VerticalLayout();
    progressText.setVisible(false);
    final MemoryBuffer buffer = new MemoryBuffer();
    final Upload upload = new Upload(buffer);
    upload.setAcceptedFileTypes(".txt", ".csv");
    upload.setDropAllowed(false);
    upload.setAutoUpload(true);
    upload.setMaxFileSize((int) DataSize.ofMegabytes(20).toBytes());
    final Button uploadButton = new Button("CSV mit Personen auswählen (maximal 20MB)");
    upload.setUploadButton(uploadButton);
    upload.addStartedListener(event -> {
      LOGGER.debug("Upload started");
    });
    upload.addSucceededListener(event -> {
      LOGGER.debug("Upload finished");
      ui.access(() -> {
        upload.setVisible(false);
        uploadButton.setVisible(false);
        progressBarImport.setVisible(true);
        progressText.setVisible(true);
        dialog.setCloseOnEsc(false);
      });
      final InputStreamResource resource = new InputStreamResource(buffer.getInputStream());
      LOGGER.debug("Import data from '{}'", resource);
      doImport(ui, dialog, progressBarImport, progressText, resource);
    });
    upload.addFailedListener(event -> {
      LOGGER.debug("Upload failed", event.getReason());
      Notification.show("Upload fehlgeschlagen '" + event.getReason().getMessage() + "'");
    });
    dialog.add(upload, progressBarImport, progressText);
  }

  private void doImport(final UI ui, final Dialog dialog, final ProgressBar progressBarImport,
      final VerticalLayout progressText, final InputStreamResource resource) {
    importServiceRunner.run(importService, resource, new ImportListener<Person>() {

      @Override
      public void startImport(final int max) {
        LOGGER.debug("Import data started");
        ui.access(() -> progressText.add(new Div(new Text("Import von " + max + " Personen gestartet."))));
      }

      @Override
      public void progressImport(final Person object, final int i, final int max) {
        if (i > 0) {
          final double progress = floor(100 * ((float) i / max));
          if (progress % 4 == 0) {
            // avoid to update progressbar too often
            LOGGER.debug("Import progress '{}' %", progress);
            ui.access(
                () -> {
                  progressBarImport.setValue(progress);
                  grid.getDataProvider().refreshAll();
                  counterText.setText(repository.count() + " Personen vorhanden");
                });
          }
        }
      }

      @Override
      public void finishedImport(final int count) {
        LOGGER.debug("Import data finished");
        ui.access(
            () -> {
              progressText.add(new Div(new Text("Es wurden '" + count + "' Personen importiert.")));
              dialog.add(new Button("Schliessen", event -> dialog.close()));
              grid.getDataProvider().refreshAll();
              counterText.setText(repository.count() + " Personen vorhanden");
            });
      }

      @Override
      public void startDelete(final long count) {
        ui.access(() -> progressText.add(new Div(new Text("Löschen von '" + count + "' Personen gestartet."))));
      }

      @Override
      public void finishedDelete() {
        ui.access(() -> progressText.add(new Div(new Text("Löschen fertig."))));
      }
    });
  }
}
