package com.mymita.al.ui.search.christening;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.io.ClassPathResource;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.mymita.al.domain.Christening;
import com.mymita.al.service.ChristeningService;
import com.mymita.al.ui.search.AbstractSearch;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Configurable
@Theme("default")
@PreserveOnRefresh
public class Search extends AbstractSearch<Christening> {

  @Autowired
  transient ChristeningService christeningService;

  public Search() {
    super(Christening.class, new ClassPathResource("search.html", Search.class));
  }

  @Override
  protected void initContent(final CustomLayout content, final Table resultTable) {
    final TextField name = new TextField("Name");
    name.setNullSettingAllowed(true);
    name.setNullRepresentation("");
    name.setValue(null);
    name.setStyleName("search");
    name.setWidth("150px");
    name.setRequired(false);
    name.setValidationVisible(false);
    name.focus();
    final TextField year = new TextField("Jahr");
    year.setStyleName("search");
    year.setWidth("80px");
    final Button search = new NativeButton("Suche starten", new Button.ClickListener() {

      @Override
      public void buttonClick(final ClickEvent event) {
        final String nameValue = name(name.getValue());
        final String yearValue = String.valueOf(MoreObjects.firstNonNull(number(year.getValue()), ""));
        showHits(ImmutableList.copyOf(christeningService.find(nameValue, yearValue)));
      }
    });
    search.setClickShortcut(KeyCode.ENTER);
    content.addComponent(name, "lastNameFather");
    content.addComponent(year, "year");
    content.addComponent(search, "search");
    content.addComponent(resultTable, "results");
    showResultDetails(content, resultTable, null);
    setContent(content);
  }

  @Override
  protected void setResultColumns(final Table resultTable, final List<Christening> results) {
    resultTable.setContainerDataSource(new BeanItemContainer<Christening>(Christening.class, results));
    resultTable.setColumnHeader("firstNameFather", "Vorname Vater");
    resultTable.setColumnHeader("lastNameFather", "Nachname Vater");
    resultTable.setColumnHeader("taufKind", "Taufkind");
    resultTable.setColumnHeader("year", "Jahr");
    resultTable.setColumnHeader("church", "Kirche");
    resultTable.setColumnWidth("firstNameFather", 130);
    resultTable.setColumnWidth("lastNameFather", 130);
    resultTable.setColumnWidth("taufKind", 130);
    resultTable.setColumnWidth("year", 50);
    resultTable.setColumnWidth("church", 100);
    resultTable.setColumnAlignment("year", Align.CENTER);
    resultTable.setVisibleColumns(new Object[] { "firstNameFather", "lastNameFather", "taufKind", "year", "church" });
  }

  @Override
  protected void showResultDetails(final CustomLayout content, final Table resultTable, final Christening result) {

    final Panel descriptionPanel = new Panel();
    descriptionPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
    descriptionPanel.setCaption("Beschreibung");
    descriptionPanel.setHeight(130, Unit.PIXELS);
    descriptionPanel.setWidth(470, Unit.PIXELS);
    final VerticalLayout infoPanelLayout = new VerticalLayout();
    infoPanelLayout.addComponent(new VerticalLayout(new Label("Code: " + (result != null ? result.getFamilyCode() : "")), new Label(
        "Beruf: " + (result != null ? result.getProfession() : ""))));
    descriptionPanel.setContent(infoPanelLayout);

    final Panel referencePanel = new Panel();
    referencePanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
    referencePanel.setCaption("Quelle / Ersterwähnung");
    referencePanel.setWidth(470, Unit.PIXELS);
    final VerticalLayout referencePanelLayout = new VerticalLayout();
    referencePanelLayout.setStyleName("reference-panel");
    referencePanelLayout.setSizeFull();
    referencePanelLayout.addComponent(new Label(result != null ? result.getReference() : ""));
    referencePanel.setContent(referencePanelLayout);

    final GridLayout infos = new GridLayout(2, 3);
    infos.setSizeFull();
    infos.setStyleName("result-details");
    infos.setSpacing(true);
    infos.addComponent(descriptionPanel, 0, 1, 1, 1);
    infos.addComponent(referencePanel, 0, 2, 1, 2);
    infos.setComponentAlignment(descriptionPanel, Alignment.TOP_LEFT);
    infos.setComponentAlignment(referencePanel, Alignment.BOTTOM_LEFT);
    content.removeComponent("details");
    content.addComponent(infos, "details");

    final HorizontalLayout help = new HorizontalLayout();
    help.setStyleName("help");
    help.setMargin(true);
    if (resultTable.size() == 0) {
      final Label hint = new Label("<i class=\"fi-info help\"/> Bitte starten Sie die Suche.", ContentMode.HTML);
      help.addComponent(hint);
    } else if (resultTable.size() > 0 && result == null) {
      final Label hint = new Label("<i class=\"fi-info help\"/> Bitte klicken Sie auf ein Ergebnis um weitere Informationen zu sehen.",
          ContentMode.HTML);
      help.addComponent(hint);
    } else if (resultTable.size() > 0 && result != null) {
      final Label hint = new Label("<i class=\"fi-info help\"/> Hier erhalten Sie weitere Informationen per Email", ContentMode.HTML);
      hint.addStyleName("email-contact");
      new BrowserWindowOpener(new ExternalResource("mailto:wehlmann@altes-leipzig.de?subject=Detailanfrage für FamilienCode '"
          + result.getFamilyCode() + "'")).extend(hint);
      help.addComponent(hint);
    }
    content.removeComponent("help");
    content.addComponent(help, "help");
  }
}
