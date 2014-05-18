package com.mymita.al.ui.search;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Reindeer;

public abstract class AbstractSearch<T> extends UI {

  private static final long serialVersionUID = -2765748679653124722L;

  protected static String name(final String value) {
    final String result = Objects.firstNonNull(value, "").replaceAll("'", "").replaceAll("%", "");
    if (result.length() < 3) {
      return null;
    }
    return result;
  }

  protected static Number number(final String value) {
    if (!Strings.isNullOrEmpty(value)) {
      try {
        return Integer.valueOf(value);
      } catch (final NumberFormatException ignore) {
      }
    }
    return null;
  }

  private final ClassPathResource classPathResource;
  private final Class<T> entityClass;

  private Table resultTable;
  private CustomLayout content;

  public AbstractSearch(final Class<T> entityClass, final ClassPathResource templateResource) {
    this.entityClass = entityClass;
    this.classPathResource = templateResource;
  }

  protected Table createResultTable() {
    final Table resultTable = new Table();
    resultTable.setContainerDataSource(new BeanItemContainer<T>(entityClass));
    return resultTable;
  }

  @Override
  protected final void init(final VaadinRequest request) {
    getPage().setTitle("Altes Leipzig Suche");
    try {
      content = new CustomLayout(classPathResource.getInputStream());
    } catch (final IOException e) {
      return;
    }
    resultTable = initResultTable();
    setResultColumns(resultTable, Lists.<T> newArrayList());
    initContent(content, resultTable);
    showResultDetails(content, resultTable, null);
    setContent(content);
  }

  protected abstract void initContent(final CustomLayout content, final Table resultTable);

  private Table initResultTable() {
    final Table results = createResultTable();
    results.setStyleName(Reindeer.TABLE_BORDERLESS);
    results.addStyleName(Reindeer.TABLE_STRONG);
    results.setWidth(100, Unit.PERCENTAGE);
    results.setHeight(350, Unit.PIXELS);
    results.setSelectable(true);
    results.setMultiSelect(false);
    results.setImmediate(true);
    results.setPageLength(15);
    results.addValueChangeListener(new ValueChangeListener() {
      @Override
      public void valueChange(final ValueChangeEvent event) {
        showResultDetails(content, resultTable, (T) event.getProperty().getValue());
      }
    });
    return results;
  }

  protected abstract void setResultColumns(final Table resultTable, final List<T> results);

  protected void showHits(final List<T> results) {
    setResultColumns(resultTable, results);
    if (results.isEmpty()) {
      final Notification notification = new Notification("Ihre Suche ergab leider keine Ergebnisse", Type.HUMANIZED_MESSAGE);
      notification.setDelayMsec(2000);
      notification.show(Page.getCurrent());
    } else {
      final Notification notification = new Notification("Ihre Suche ergab " + results.size() + " Ergebnisse", Type.HUMANIZED_MESSAGE);
      notification.setDelayMsec(2000);
      notification.show(Page.getCurrent());
    }
    showResultDetails(content, resultTable, null);
  }

  protected abstract void showResultDetails(final CustomLayout content, final Table resultTable, final T result);
}
