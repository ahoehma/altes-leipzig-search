package com.mymita.al.ui.admin.view;

import java.io.File;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.repository.CrudRepository;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.mymita.al.importer.ImportService;
import com.mymita.al.ui.admin.importer.ImportWorker;
import com.mymita.al.ui.admin.importer.ImportWorker.Callback;
import com.mymita.al.ui.admin.importer.Importer;
import com.mymita.al.ui.admin.importer.Uploader;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

@Configurable(preConstruction = true)
public abstract class AbstractView<T> extends CustomComponent implements View {

  protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractView.class);

  @Autowired
  protected transient ListeningExecutorService executorService;
  @Autowired
  transient ImportService<T> importService;
  @Autowired
  transient CrudRepository<T, Long> repository;

  private final BeanItemContainer<T> container;

  public AbstractView(final Class<T> domainClazz) {
    super();
    container = new BeanItemContainer<T>(domainClazz);
    final VerticalLayout content = new VerticalLayout();
    addTable(content);
    addImportButton(content);
    addDeleteAllButton(content);
    addRefreshButton(content);
    setCompositionRoot(content);
  }

  private void addDeleteAllButton(final VerticalLayout content) {
    content.addComponent(new NativeButton("Delete all", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        deleteAll();
        refresh();
      }
    }));
  }

  private void addImportButton(final VerticalLayout content) {
    addImporter(content, "Start import", importService, new Callback() {

      @Override
      public void finishedImport() {
        executorService.submit(new Callable<Void>() {

          @Override
          public Void call() throws Exception {
            refresh();
            return null;
          }
        });
      }
    });
  }

  protected final void addImporter(final VerticalLayout result, final String caption, final ImportService<T> importService,
      final Callback callback) {
    final ProgressBar progressBar = new ProgressBar();
    final Uploader<T> uploader = new Uploader<T>(getUI(), progressBar, new Importer<T>() {

      @Override
      public void importData(final File file, final ProgressBar progressBar) {
        LOGGER.debug("Start importing data");
        getUI().access(new Runnable() {

          @Override
          public void run() {
            progressBar.setCaption("Importing ...");
          }
        });
        executorService.submit(new ImportWorker<T>(getUI(), importService, file, progressBar, callback));
      }
    });
    final Upload upload = new Upload(null, uploader);
    upload.setButtonCaption(caption);
    upload.addSucceededListener(uploader);
    upload.addStartedListener(uploader);
    upload.addProgressListener(uploader);
    final HorizontalLayout c = new HorizontalLayout(upload, progressBar);
    c.setWidth(500, Unit.PIXELS);
    c.setComponentAlignment(upload, Alignment.MIDDLE_LEFT);
    c.setComponentAlignment(progressBar, Alignment.MIDDLE_RIGHT);
    result.addComponent(c);
  }

  private void addRefreshButton(final VerticalLayout content) {
    content.addComponent(new NativeButton("Refresh all", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        refresh();
      }
    }));
  }

  private void addTable(final VerticalLayout content) {
    content.addComponent(new Table(null, container));
  }

  private void deleteAll() {
    repository.deleteAll();
  }

  @Override
  public void enter(final ViewChangeEvent event) {
  }

  private void refresh() {
    container.removeAllItems();
    container.addAll(ImmutableList.copyOf(repository.findAll()));
  }

}