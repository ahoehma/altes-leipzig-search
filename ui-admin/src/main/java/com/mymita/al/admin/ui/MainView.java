package com.mymita.al.admin.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * The main view is a top-level placeholder for other views.
 */
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@Push
public class MainView extends AppLayout {

  private final Tabs menu;

  public MainView() {
    menu = createMenuTabs();
    addToNavbar(menu);
  }

  private static Tabs createMenuTabs() {
    final Tabs tabs = new Tabs();
    tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
    tabs.add(getAvailableTabs());
    return tabs;
  }

  private static Tab[] getAvailableTabs() {
    final List<Tab> tabs = new ArrayList<>();
    tabs.add(createTab("Person", PersonView.class));
    tabs.add(createTab("Marriage", MarriageView.class));
    tabs.add(createTab("Christening", ChristeningView.class));
    return tabs.toArray(new Tab[tabs.size()]);
  }

  private static Tab createTab(final String title,
      final Class<? extends Component> viewClass) {
    return createTab(populateLink(new RouterLink(null, viewClass), title));
  }

  private static Tab createTab(final Component content) {
    final Tab tab = new Tab();
    tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
    tab.add(content);
    return tab;
  }

  private static <T extends HasComponents> T populateLink(final T a, final String title) {
    a.add(title);
    return a;
  }

  @Override
  protected void afterNavigation() {
    super.afterNavigation();
    selectTab();
  }

  private void selectTab() {
    final String target = RouteConfiguration.forSessionScope()
        .getUrl(getContent().getClass());
    final Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
      final Component child = tab.getChildren().findFirst().get();
      return child instanceof RouterLink
          && ((RouterLink) child).getHref().equals(target);
    }).findFirst();
    tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
  }
}
