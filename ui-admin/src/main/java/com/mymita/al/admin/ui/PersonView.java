package com.mymita.al.admin.ui;

import static com.google.common.collect.Lists.newArrayList;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static java.lang.Math.floor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.unit.DataSize;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import com.mymita.al.domain.Person;
import com.mymita.al.importer.ImportListener;
import com.mymita.al.importer.ImportService;
import com.mymita.al.repository.PersonRepository;
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
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "person", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Person")
public class PersonView extends Div implements AfterNavigationObserver {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersonView.class);

  @Autowired
  transient ImportService<Person> importService;
  @Autowired
  transient PersonRepository repository;

  private final Grid<Person> grid = new Grid<>(Person.class);
  private final MenuBar menuBar = new MenuBar();

  public PersonView() {
    setId("person-view");
    add(addMenu(menuBar));
    grid.setId("list");
    grid.addThemeVariants(LUMO_NO_BORDER);
    grid.setColumns("id", "personCode", "firstName", "lastName", "birthName", "gender", "yearOfBirth", "yearOfDeath", "yearsOfLife",
        "description", "reference", "link", "image");
    add(grid);
  }

  @Override
  public void afterNavigation(final AfterNavigationEvent event) {
    fill();
  }

  private void fill() {
    final FilterablePageableDataProvider<Person, Object> dataProvider = new FilterablePageableDataProvider<Person, Object>() {

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
        sortOrders.add(new QuerySortOrder("personCode", SortDirection.ASCENDING));
        return sortOrders;
      }
    };

    grid.setDataProvider(dataProvider);
  }

  private MenuBar addMenu(final MenuBar menuBar) {
    menuBar.addItem("Alle Zeigen", e -> {
      grid.setItems(newArrayList(repository.findAll()));
    });
    menuBar.addItem("Importieren", e -> {
      final UI ui = e.getSource().getUI().get();
      final Dialog dialog = new Dialog();
      dialog.setCloseOnEsc(true);
      dialog.setCloseOnOutsideClick(false);
      final VerticalLayout dialogContent = new VerticalLayout();
      dialog.add(dialogContent);
      dialogContent.setWidthFull();
      addImporter(ui, dialog, dialogContent,
          count -> ui.access(
              () -> {
                final Button confirmButton = new Button("Import was successful for '" + count + "' persons", event -> dialog.close());
                dialogContent.removeAll();
                dialogContent.add(confirmButton);
                fill();
              }));
      dialog.open();
    });
    return menuBar;
  }

  void addImporter(final UI ui, final Dialog dialog, final VerticalLayout dialogContent, final IntConsumer finishedImportCount) {
    final ProgressBar progressBarImport = new ProgressBar(0, 100);
    progressBarImport.setVisible(false);
    final MemoryBuffer buffer = new MemoryBuffer();
    final Upload upload = new Upload(buffer);
    upload.setAcceptedFileTypes(".txt", ".csv");
    upload.setDropAllowed(false);
    upload.setAutoUpload(true);
    upload.setMaxFileSize((int) DataSize.ofMegabytes(20).toBytes());
    final Button uploadButton = new Button("Select a Person-CSV (max 20MB)");
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
        dialog.setCloseOnEsc(false);
      });
      final InputStreamResource resource = new InputStreamResource(buffer.getInputStream());
      LOGGER.debug("Import data from '{}'", resource);
      doImport(ui, finishedImportCount, progressBarImport, resource);
    });
    upload.addFailedListener(event -> {
      LOGGER.debug("Upload failed", event.getReason());
      Notification.show("Import failed '" + event.getReason().getMessage() + "'");
    });
    dialogContent.add(upload, progressBarImport);
  }

  private void doImport(final UI ui, final IntConsumer importFinishedCallback, final ProgressBar progressBarImport,
      final InputStreamResource resource) {
    importService.importData(resource, new ImportListener<Person>() {

      @Override
      public void finishedImport(final int count) {
        LOGGER.debug("Import data finished");
        importFinishedCallback.accept(count);
      }

      @Override
      public void progressImport(final Person object, final int i, final int max) {
        if (i > 0) {
          final double progress = floor(100 * ((float) i / max));
          if (progress % 10 == 0) {
            // avoid to update progressbar too often
            LOGGER.debug("Import progress '{}' %", progress);
            ui.access(() -> progressBarImport.setValue(progress));
          }
        }
      }

      @Override
      public void startImport(final int max) {
        LOGGER.debug("Import data started");
      }
    });
  }
}
