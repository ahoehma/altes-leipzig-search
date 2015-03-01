package com.mymita.al.ui.admin;

import java.io.File;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.dialogs.ConfirmDialog;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mymita.al.domain.Christening;
import com.mymita.al.domain.Marriage;
import com.mymita.al.domain.Person;
import com.mymita.al.importer.ImportListener;
import com.mymita.al.importer.ImportService;
import com.mymita.al.repository.ChristeningRepository;
import com.mymita.al.repository.MarriageRepository;
import com.mymita.al.repository.PersonRepository;
import com.mymita.al.ui.utils.ConcurrentUtils;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@Configurable
@PreserveOnRefresh
@Theme(ValoTheme.THEME_NAME)
public class AdminUI extends UI {

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

  private final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

  private void addChristeningDeleteAll(final VerticalLayout result) {
    result.addComponent(new Button("Delete all christenings", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        christeningRepository.deleteAll();
      }
    }));
  }

  private void addChristeningImport(final VerticalLayout result) {
    addImporter(result, "Start christening import", christeningImportService, ConcurrentUtils.wrap(new Runnable() {

      @Override
      public void run() {
        christeningContainer.removeAllItems();
        christeningContainer.addAll(ImmutableList.copyOf(christeningRepository.findAll()));
      }
    }));
  }

  @SuppressWarnings("serial")
  private void addChristeningRefresh(final VerticalLayout result) {
    result.addComponent(new Button("Refresh all christenings", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        christeningContainer.removeAllItems();
        christeningContainer.addAll(ImmutableList.copyOf(christeningRepository.findAll()));
      }
    }));
  }

  private void addChristeningTable(final VerticalLayout result) {
    final Table c = new Table(null, christeningContainer);
    c.setSizeFull();
    result.addComponent(c);
  }

  private <T> void addImporter(final VerticalLayout result, final String caption, final ImportService<T> importService,
      final Runnable importFinishedCallback) {
    final ProgressBar progressBar = new ProgressBar();
    progressBar.setVisible(false);
    final CsvUploader<T> receiver = new CsvUploader<T>(progressBar, new Importer<T>() {

      @Override
      public void importData(final File file, final ProgressBar progressBar) {
        final ListenableFuture<?> submit = executorService.submit(new ImportWorker<T>(importService, file, new ImportListener<T>() {

          @Override
          public void finishedImport() {
            progressBar.setVisible(false);
          }

          @Override
          public void progressImport(final T object, final int i, final int max) {
            if (i > 0) {
              final float progress = (float) i / (float) max;
              UI.getCurrent().access(new Runnable() {

                @Override
                public void run() {
                  progressBar.setValue(progress);
                }
              });
            }
          }

          @Override
          public void startImport(final int max) {
            progressBar.setVisible(true);
            progressBar.setValue(0f);
          }
        }));
        submit.addListener(importFinishedCallback, MoreExecutors.directExecutor());
      }
    });
    final Upload upload = new Upload(null, receiver);
    upload.setButtonCaption(caption);
    upload.addSucceededListener(receiver);
    final HorizontalLayout c = new HorizontalLayout(upload, progressBar);
    c.setSizeFull();
    c.setSpacing(true);
    c.setMargin(true);
    c.setComponentAlignment(upload, Alignment.MIDDLE_LEFT);
    c.setComponentAlignment(progressBar, Alignment.MIDDLE_RIGHT);
    result.addComponent(c);
  }

  @SuppressWarnings("serial")
  private void addMarriageDeleteAll(final VerticalLayout result) {
    result.addComponent(new Button("Delete all marriages", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        marriageRepository.deleteAll();
      }
    }));
  }

  private void addMarriageImport(final VerticalLayout result) {
    addImporter(result, "Start marriage import", marriageImportService, ConcurrentUtils.wrap(new Runnable() {

      @Override
      public void run() {
        refreshMarriageContainer();
      }
    }));
  }

  @SuppressWarnings("serial")
  private void addMarriageRefresh(final VerticalLayout result) {
    result.addComponent(new Button("Refresh all marriages", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        refreshMarriageContainer();
      }
    }));
  }

  private void addMarriageTable(final VerticalLayout result) {
    final Table c = new Table(null, marriageContainer);
    c.setSizeFull();
    result.addComponent(c);
  }

  private void addPersonTable(final VerticalLayout result) {
    final Table c = new Table(null, personContainer);
    c.setSizeFull();
    result.addComponent(c);
  }

  private Component createChristeningTab() {
    final VerticalLayout result = new VerticalLayout();
    result.setSizeFull();
    addChristeningTable(result);
    addChristeningImport(result);
    addChristeningDeleteAll(result);
    addChristeningRefresh(result);
    return result;
  }

  private Component createContent() {
    final TabSheet ts = new TabSheet();
    ts.setStyleName(ValoTheme.TABSHEET_COMPACT_TABBAR);
    final Component personTab = createPersonTab();
    final Component marriageTab = createMarriageTab();
    final Component christeningTab = createChristeningTab();
    ts.addSelectedTabChangeListener(new SelectedTabChangeListener() {

      @Override
      public void selectedTabChange(final SelectedTabChangeEvent event) {
        if (ts.getSelectedTab() == personTab) {
          refreshPersonContainer();
        }
      }
    });
    ts.addTab(personTab, "Personen");
    ts.addTab(marriageTab, "Hochzeiten");
    ts.addTab(christeningTab, "Taufen");
    final MenuBar menubar = new MenuBar();
    final MenuItem data = menubar.addItem("Daten", null, null);
    final MenuItem marriageMenu = data.addItem("Personen", new Command() {

      @Override
      public void menuSelected(final MenuItem selectedItem) {
        ts.setSelectedTab(personTab);
      }
    });
    marriageMenu.addItem("Importieren", new Command() {

      @Override
      public void menuSelected(final MenuItem selectedItem) {
        final Window window = new Window("Importieren");
        final VerticalLayout content = new VerticalLayout();
        addImporter(content, "Start person import", personImportService, ConcurrentUtils.wrap(new Runnable() {

          @Override
          public void run() {
            LOGGER.info("Import finished");
            refreshPersonContainer();
            window.close();
          }
        }));
        window.setContent(content);
        window.center();
        window.setWidth(400, Unit.PIXELS);
        window.setHeight(200, Unit.PIXELS);
        getCurrent().addWindow(window);
      }
    });
    marriageMenu.addItem("Alle löschen", new Command() {

      @Override
      public void menuSelected(final MenuItem selectedItem) {
        ConfirmDialog.show(getCurrent(), "Bitte bestätigen:", "Alle Personen löschen?", "Ja", "Nein", new ConfirmDialog.Listener() {

          @Override
          public void onClose(final ConfirmDialog dialog) {
            if (dialog.isConfirmed()) {
              personRepository.deleteAll();
              refreshPersonContainer();
            }
          }
        });
      }
    });
    marriageMenu.addItem("Ansicht aktualisieren", new Command() {

      @Override
      public void menuSelected(final MenuItem selectedItem) {
        refreshPersonContainer();
      }
    });
    data.addItem("Hochzeiten", new Command() {

      @Override
      public void menuSelected(final MenuItem selectedItem) {
        ts.setSelectedTab(marriageTab);
      }
    });
    data.addItem("Taufen", new Command() {

      @Override
      public void menuSelected(final MenuItem selectedItem) {
        ts.setSelectedTab(christeningTab);
      }
    });
    return new VerticalLayout(menubar, ts);
  }

  private Component createMarriageTab() {
    final VerticalLayout result = new VerticalLayout();
    result.setSizeFull();
    addMarriageTable(result);
    addMarriageImport(result);
    addMarriageDeleteAll(result);
    addMarriageRefresh(result);
    return result;
  }

  private Component createPersonTab() {
    final VerticalLayout result = new VerticalLayout();
    result.setSizeFull();
    addPersonTable(result);
    return result;
  }

  @Override
  protected void init(final VaadinRequest request) {
    getPage().setTitle("Altes Leipzig Suche - Administration");
    setSizeFull();
    setContent(createContent());
  }

  private void refreshMarriageContainer() {
    marriageContainer.removeAllItems();
    marriageContainer.addAll(ImmutableList.copyOf(marriageRepository.findAll()));
  }

  private void refreshPersonContainer() {
    personContainer.removeAllItems();
    personContainer.addAll(ImmutableList.copyOf(personRepository.findAll()));
  }
}