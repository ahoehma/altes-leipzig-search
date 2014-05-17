package com.mymita.al.ui.search.christening;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.mymita.al.domain.Christening;
import com.mymita.al.repository.ChristeningRepository;
import com.mymita.al.ui.search.AbstractSearch;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
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
import com.vaadin.ui.themes.Reindeer;

@Configurable
@Theme("default")
@PreserveOnRefresh
public class Search extends AbstractSearch<Christening> {

  private static final long serialVersionUID = -6780876618168616688L;
  private static final Logger LOGGER = LoggerFactory.getLogger(Search.class);

  @Autowired
  transient ChristeningRepository christeningRepository;

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
        final String yearValue = String.valueOf(number(year.getValue()));
        final boolean withName = !Strings.isNullOrEmpty(nameValue);
        final boolean withYear = !Strings.isNullOrEmpty(yearValue);
        if (withName && withYear) {
          showHits(christeningRepository.findByLastNameFatherContainingIgnoreCaseAndYear(nameValue, yearValue));
          return;
        }
        if (withName) {
          showHits(christeningRepository.findByLastNameFatherContainingIgnoreCase(nameValue));
          return;
        }
        if (withYear) {
          showHits(christeningRepository.findByYear(yearValue));
          return;
        }
        showHits(Lists.<Christening> newArrayList());
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
    descriptionPanel.setStyleName("description");
    descriptionPanel.addStyleName(Reindeer.PANEL_LIGHT);
    descriptionPanel.setCaption("Beschreibung");
    descriptionPanel.setHeight(130, Unit.PIXELS);
    descriptionPanel.setWidth(450, Unit.PIXELS);
    final VerticalLayout infoPanelLayout = new VerticalLayout();
    infoPanelLayout.addComponent(new Label("Code: " + (result != null ? result.getFamilyCode() : "")));
    // infoPanelLayout.addComponent(new Label(result != null ? result.getDescription() : ""));
    descriptionPanel.setContent(infoPanelLayout);

    final Panel imagePanel = new Panel();
    imagePanel.setStyleName(Reindeer.PANEL_LIGHT);
    imagePanel.setCaption("Bild/Vorschau");
    imagePanel.setHeight(100, Unit.PERCENTAGE);
    imagePanel.setWidth(220, Unit.PIXELS);
    final VerticalLayout imagePanelLayout = new VerticalLayout();
    imagePanelLayout.setStyleName("image-panel");
    imagePanelLayout.setSizeFull();
    final Label human = new Label("<i class=\"fi-torso image-preview\"/>", ContentMode.HTML);
    human.setSizeUndefined();
    imagePanelLayout.addComponent(human);
    imagePanelLayout.setComponentAlignment(human, Alignment.MIDDLE_CENTER);
    imagePanel.setContent(imagePanelLayout);

    final Panel referencePanel = new Panel();
    referencePanel.setStyleName(Reindeer.PANEL_LIGHT);
    referencePanel.setCaption("Quelle / Ersterwähnung");
    referencePanel.setHeight(40, Unit.PIXELS);
    referencePanel.setWidth(450, Unit.PIXELS);
    final VerticalLayout referencePanelLayout = new VerticalLayout();
    referencePanelLayout.setStyleName("reference-panel");
    referencePanelLayout.setSizeFull();
    referencePanelLayout.addComponent(new Label(result != null ? result.getReference() : ""));
    referencePanel.setContent(referencePanelLayout);

    final GridLayout infos = new GridLayout(2, 3);
    infos.setStyleName("result-details");
    infos.setMargin(new MarginInfo(true, false, false, false));
    infos.setSpacing(true);
    infos.setWidth(100, Unit.PERCENTAGE);

    infos.addComponent(descriptionPanel, 0, 1);
    infos.addComponent(referencePanel, 0, 2);
    infos.addComponent(imagePanel, 1, 1, 1, 2);
    infos.setComponentAlignment(imagePanel, Alignment.MIDDLE_RIGHT);
    infos.setComponentAlignment(descriptionPanel, Alignment.TOP_LEFT);
    infos.setComponentAlignment(referencePanel, Alignment.BOTTOM_LEFT);
    infos.setRowExpandRatio(0, 0);

    content.removeComponent("help");
    final Label icon = new Label("<i class=\"fi-info help\"/>", ContentMode.HTML);
    if (resultTable.size() == 0) {
      final Label hint = new Label("Bitte starten Sie die Suche.");
      hint.setStyleName("hint");
      final HorizontalLayout hintLayout = new HorizontalLayout(icon, hint);
      hintLayout.setSpacing(true);
      hintLayout.setHeight(40, Unit.PIXELS);
      hintLayout.setComponentAlignment(icon, Alignment.MIDDLE_LEFT);
      hintLayout.setComponentAlignment(hint, Alignment.MIDDLE_LEFT);
      content.addComponent(hintLayout, "help");
    } else if (resultTable.size() > 0 && result == null) {
      final Label hint = new Label("Bitte klicken Sie auf ein Ergebnis um weitere Informationen zu erhalten.");
      hint.setStyleName("hint");
      final HorizontalLayout hintLayout = new HorizontalLayout(icon, hint);
      hintLayout.setSpacing(true);
      hintLayout.setHeight(40, Unit.PIXELS);
      hintLayout.setComponentAlignment(icon, Alignment.MIDDLE_LEFT);
      hintLayout.setComponentAlignment(hint, Alignment.MIDDLE_LEFT);
      content.addComponent(hintLayout, "help");
    } else if (resultTable.size() > 0 && result != null) {
      final Label hint = new Label("Klicken Sie bitte hier um weitere Informationen zur gewählten Hochzeit zu erfragen");
      hint.setStyleName("hint");
      hint.addStyleName("contact");
      new BrowserWindowOpener(new ExternalResource("mailto:wehlmann@altes-leipzig.de?subject=Detailanfrage für FamilienCode '"
          + result.getFamilyCode() + "'")).extend(hint);
      final HorizontalLayout hintLayout = new HorizontalLayout(icon, hint);
      hintLayout.setSpacing(true);
      hintLayout.setHeight(40, Unit.PIXELS);
      hintLayout.setComponentAlignment(icon, Alignment.MIDDLE_LEFT);
      hintLayout.setComponentAlignment(hint, Alignment.MIDDLE_LEFT);
      content.addComponent(hintLayout, "help");
    }
    content.removeComponent("details");
    content.addComponent(infos, "details");
  }
}
