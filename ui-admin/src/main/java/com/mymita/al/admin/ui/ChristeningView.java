package com.mymita.al.admin.ui;

import static com.google.common.collect.Lists.newArrayList;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_ROW_BORDERS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import com.mymita.al.domain.Christening;
import com.mymita.al.importer.ImportService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "christening", layout = MainView.class)
@PageTitle("Christening")
public class ChristeningView extends Div implements AfterNavigationObserver {

  @Autowired
  transient ImportService<Christening> importService;
  @Autowired
  transient CrudRepository<Christening, Long> repository;

  private final Grid<Christening> grid;

  public ChristeningView() {
    setId("christening-view");
    grid = new Grid<>(Christening.class);
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
