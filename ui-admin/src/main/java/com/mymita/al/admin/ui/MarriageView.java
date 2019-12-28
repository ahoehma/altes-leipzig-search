package com.mymita.al.admin.ui;

import static com.google.common.collect.Lists.newArrayList;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_ROW_BORDERS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import com.mymita.al.domain.Marriage;
import com.mymita.al.importer.ImportService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "Marriage", layout = MainView.class)
@PageTitle("marriage")
public class MarriageView extends Div implements AfterNavigationObserver {

  @Autowired
  transient ImportService<Marriage> importService;
  @Autowired
  transient CrudRepository<Marriage, Long> repository;

  private final Grid<Marriage> grid;

  public MarriageView() {
    setId("marriage-view");
    grid = new Grid<>(Marriage.class);
    grid.setId("list");
    grid.addThemeVariants(LUMO_NO_BORDER, LUMO_NO_ROW_BORDERS);
    grid.setHeightFull();
    add(grid);
  }

  @Override
  public void afterNavigation(final AfterNavigationEvent event) {
    // Lazy init of the grid items, happens only when we are sure the view will be shown to the user
    grid.setItems(newArrayList(repository.findAll()));
  }
}
