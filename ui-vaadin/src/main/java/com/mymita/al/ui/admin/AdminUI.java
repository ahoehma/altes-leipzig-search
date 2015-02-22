package com.mymita.al.ui.admin;

import java.io.File;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
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
    result.addComponent(new NativeButton("Delete all christenings", new Button.ClickListener() {

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
    c.setWidth(700, Unit.PIXELS);
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
    addImporter(result, "Start marriage import", marriageImportService, ConcurrentUtils.wrap(new Runnable() {

      @Override
      public void run() {
        marriageContainer.removeAllItems();
        marriageContainer.addAll(ImmutableList.copyOf(marriageRepository.findAll()));
      }
    }));
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
    addImporter(result, "Start person import", personImportService, ConcurrentUtils.wrap(new Runnable() {

      @Override
      public void run() {
        LOGGER.info("Import finished");
        personContainer.removeAllItems();
        personContainer.addAll(ImmutableList.copyOf(personRepository.findAll()));
      }
    }));
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