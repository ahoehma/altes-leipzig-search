package com.mymita.al.ui.search;

import com.google.common.collect.Lists;
import com.jensjansson.pagedtable.PagedTable;
import com.jensjansson.pagedtable.PagedTableContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;

public class PersonSearchResultTable extends PagedTable {

  private static final long serialVersionUID = -249098649321082542L;

  public PersonSearchResultTable() {
    super();
  }

  @Override
  public HorizontalLayout createControls() {
    final Label itemsPerPageLabel = new Label("Einträge pro Seite:");
    final ComboBox itemsPerPageSelect = new ComboBox(null, Lists.newArrayList("15", "30", "50", "100"));
    itemsPerPageSelect.setImmediate(true);
    itemsPerPageSelect.setNullSelectionAllowed(false);
    itemsPerPageSelect.setWidth("50px");
    itemsPerPageSelect.addValueChangeListener(new ValueChangeListener() {
      private static final long serialVersionUID = -2255853716069800092L;

      @Override
      public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
        setPageLength(Integer.valueOf(String.valueOf(event.getProperty().getValue())));
      }
    });
    itemsPerPageSelect.select("15");
    final Label pageLabel = new Label("Seite:&nbsp;", ContentMode.HTML);
    final Label currentPageTextField = new Label(String.valueOf(getCurrentPage()));
    final Label separatorLabel = new Label("&nbsp;/&nbsp;", ContentMode.HTML);
    final Label totalPagesLabel = new Label(String.valueOf(getTotalAmountOfPages()), ContentMode.HTML);
    pageLabel.setWidth(null);
    currentPageTextField.setWidth(null);
    separatorLabel.setWidth(null);
    totalPagesLabel.setWidth(null);

    final HorizontalLayout controlBar = new HorizontalLayout();
    final HorizontalLayout pageSize = new HorizontalLayout();
    final HorizontalLayout pageManagement = new HorizontalLayout();
    final Button first = new NativeButton("<<", new ClickListener() {
      @Override
      public void buttonClick(final ClickEvent event) {
        setCurrentPage(0);
      }
    });
    final Button previous = new NativeButton("<", new ClickListener() {
      @Override
      public void buttonClick(final ClickEvent event) {
        previousPage();
      }
    });
    final Button next = new NativeButton(">", new ClickListener() {
      @Override
      public void buttonClick(final ClickEvent event) {
        nextPage();
      }
    });
    final Button last = new NativeButton(">>", new ClickListener() {
      @Override
      public void buttonClick(final ClickEvent event) {
        setCurrentPage(getTotalAmountOfPages());
      }
    });

    itemsPerPageLabel.addStyleName("pagedtable-itemsperpagecaption");
    itemsPerPageSelect.addStyleName("pagedtable-itemsperpagecombobox");
    pageLabel.addStyleName("pagedtable-pagecaption");
    currentPageTextField.addStyleName("pagedtable-pagefield");
    separatorLabel.addStyleName("pagedtable-separator");
    totalPagesLabel.addStyleName("pagedtable-total");
    first.addStyleName("pagedtable-first");
    previous.addStyleName("pagedtable-previous");
    next.addStyleName("pagedtable-next");
    last.addStyleName("pagedtable-last");

    itemsPerPageLabel.addStyleName("pagedtable-label");
    itemsPerPageSelect.addStyleName("pagedtable-combobox");
    pageLabel.addStyleName("pagedtable-label");
    currentPageTextField.addStyleName("pagedtable-label");
    separatorLabel.addStyleName("pagedtable-label");
    totalPagesLabel.addStyleName("pagedtable-label");
    first.addStyleName("pagedtable-button");
    previous.addStyleName("pagedtable-button");
    next.addStyleName("pagedtable-button");
    last.addStyleName("pagedtable-button");

    pageSize.addComponent(itemsPerPageLabel);
    pageSize.addComponent(itemsPerPageSelect);
    pageSize.setComponentAlignment(itemsPerPageLabel, Alignment.MIDDLE_LEFT);
    pageSize.setComponentAlignment(itemsPerPageSelect, Alignment.MIDDLE_LEFT);
    pageSize.setSpacing(true);
    pageManagement.addComponent(first);
    pageManagement.addComponent(previous);
    pageManagement.addComponent(pageLabel);
    pageManagement.addComponent(currentPageTextField);
    pageManagement.addComponent(separatorLabel);
    pageManagement.addComponent(totalPagesLabel);
    pageManagement.addComponent(next);
    pageManagement.addComponent(last);
    pageManagement.setComponentAlignment(first, Alignment.MIDDLE_LEFT);
    pageManagement.setComponentAlignment(previous, Alignment.MIDDLE_LEFT);
    pageManagement.setComponentAlignment(pageLabel, Alignment.MIDDLE_LEFT);
    pageManagement.setComponentAlignment(currentPageTextField, Alignment.MIDDLE_LEFT);
    pageManagement.setComponentAlignment(separatorLabel, Alignment.MIDDLE_LEFT);
    pageManagement.setComponentAlignment(totalPagesLabel, Alignment.MIDDLE_LEFT);
    pageManagement.setComponentAlignment(next, Alignment.MIDDLE_LEFT);
    pageManagement.setComponentAlignment(last, Alignment.MIDDLE_LEFT);
    pageManagement.setWidth(null);
    pageManagement.setSpacing(true);
    controlBar.addComponent(pageSize);
    controlBar.addComponent(pageManagement);
    controlBar.setComponentAlignment(pageManagement, Alignment.MIDDLE_LEFT);

    addListener(new PageChangeListener() {
      @Override
      public void pageChanged(final PagedTableChangeEvent event) {
        final PagedTableContainer container = (PagedTableContainer) getContainerDataSource();
        first.setEnabled(container.getStartIndex() > 0);
        previous.setEnabled(container.getStartIndex() > 0);
        next.setEnabled(container.getStartIndex() < container.getRealSize() - getPageLength());
        last.setEnabled(container.getStartIndex() < container.getRealSize() - getPageLength());
        currentPageTextField.setValue(String.valueOf(getCurrentPage()));
        totalPagesLabel.setValue(String.valueOf(getTotalAmountOfPages()));
        itemsPerPageSelect.setValue(String.valueOf(getPageLength()));
      }
    });
    return controlBar;
  }
}
