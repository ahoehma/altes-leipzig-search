package com.mymita.al.ui.search.person;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.Person.Gender;
import com.mymita.al.repository.PersonRepository;
import com.mymita.al.ui.search.AbstractSearch;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
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
public class Search extends AbstractSearch<Person> {

  private static final long serialVersionUID = -6780876618168616688L;
  private static final Logger LOGGER = LoggerFactory.getLogger(Search.class);

  @Autowired
  transient PersonRepository personRepository;

  public Search() {
    super(Person.class, new ClassPathResource("search.html", Search.class));
  }

  @Override
  protected Table createResultTable() {
    final Table resultTable = super.createResultTable();
    resultTable.setConverter("gender", new Converter<String, Gender>() {

      private static final String FEMALE = "♀";
      private static final String MALE = "♂";

      @Override
      public Gender convertToModel(final String value, final Class<? extends Gender> targetType, final Locale locale)
          throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null) {
          return null;
        }
        if (FEMALE.equals(value)) {
          return Gender.FEMALE;
        }
        if (MALE.equals(value)) {
          return Gender.MALE;
        }
        return null;
      }

      @Override
      public String convertToPresentation(final Gender value, final Class<? extends String> targetType, final Locale locale)
          throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null) {
          return null;
        }
        switch (value) {
        case FEMALE:
          return FEMALE;
        case MALE:
          return MALE;
        }
        return null;
      }

      @Override
      public Class<Gender> getModelType() {
        return Gender.class;
      }

      @Override
      public Class<String> getPresentationType() {
        return String.class;
      }
    });
    return resultTable;
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
    final TextField yearOfBirth = new TextField("Geburtsjahr");
    yearOfBirth.setStyleName("search");
    yearOfBirth.setWidth("80px");
    final TextField yearOfDeath = new TextField("Sterbejahr");
    yearOfDeath.setStyleName("search");
    yearOfDeath.setWidth("80px");
    final Button search = new NativeButton("Suche starten", new Button.ClickListener() {

      @Override
      public void buttonClick(final ClickEvent event) {
        final String nameValue = name(name.getValue());
        final String yearOfBirthValue = String.valueOf(number(yearOfBirth.getValue()));
        final String yearOfDeathValue = String.valueOf(number(yearOfDeath.getValue()));
        final boolean withName = !Strings.isNullOrEmpty(nameValue);
        final boolean withYear = !Strings.isNullOrEmpty(yearOfBirthValue) || !Strings.isNullOrEmpty(yearOfDeathValue);
        if (withName && withYear) {
          showHits(personRepository.findByLastNameContainingIgnoreCaseOrBirthNameContainingIgnoreCaseAndYearOfBirthAndYearOfDeath(nameValue,
              nameValue, yearOfBirthValue, yearOfDeathValue));
          return;
        }
        if (withName) {
          showHits(personRepository.findByLastNameContainingAndBirthNameContainingAllIgnoringCase(nameValue, nameValue));
          return;
        }
        if (withYear) {
          showHits(personRepository.findByYearOfBirthOrYearOfDeath(yearOfBirthValue, yearOfDeathValue));
          return;
        }
        showHits(Lists.<Person> newArrayList());
      }
    });
    search.setClickShortcut(KeyCode.ENTER);
    content.addComponent(name, "name");
    content.addComponent(yearOfBirth, "yearOfBirth");
    content.addComponent(yearOfDeath, "yearOfDeath");
    content.addComponent(search, "search");
    content.addComponent(resultTable, "results");
    showResultDetails(content, resultTable, null);
    setContent(content);
  }

  @Override
  protected void setResultColumns(final Table resultTable, final List<Person> results) {
    resultTable.setContainerDataSource(new BeanItemContainer<Person>(Person.class, results));
    resultTable.setColumnHeader("firstName", "Vorname");
    resultTable.setColumnHeader("birthName", "Geburtsname");
    resultTable.setColumnHeader("lastName", "Nachname");
    resultTable.setColumnHeader("gender", "");
    resultTable.setColumnHeader("yearOfBirth", "Geboren");
    resultTable.setColumnHeader("yearOfDeath", "Gestorben");
    resultTable.setColumnHeader("yearsOfLife", "Alter");
    resultTable.setColumnWidth("firstName", 100);
    resultTable.setColumnWidth("birthName", 100);
    resultTable.setColumnWidth("lastName", 100);
    resultTable.setColumnWidth("gender", 35);
    resultTable.setColumnWidth("yearOfBirth", 80);
    resultTable.setColumnWidth("yearOfDeath", 80);
    resultTable.setColumnWidth("yearsOfLife", 50);
    resultTable.setColumnAlignment("yearOfBirth", Align.CENTER);
    resultTable.setColumnAlignment("yearOfDeath", Align.CENTER);
    resultTable.setColumnAlignment("gender", Align.CENTER);
    resultTable.setVisibleColumns(new Object[] { "lastName", "birthName", "firstName", "gender", "yearOfBirth", "yearOfDeath",
    "yearsOfLife" });
    resultTable.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

      @Override
      public String generateDescription(final Component source, final Object itemId, final Object propertyId) {
        if (itemId == null) {
          return null;
        }
        if ("gender".equals(propertyId)) {
          final Gender g = ((Person) itemId).getGender();
          if (g != null) {
            switch (g) {
            case FEMALE:
              return "Frau";
            case MALE:
              return "Mann";
            }
          }
        }
        if ("yearsOfLife".equals(propertyId)) {
          final String yearsOfLife = ((Person) itemId).getYearsOfLife();
          if (yearsOfLife != null) {
            if (yearsOfLife.endsWith("*")) {
              return "Von der Person existiert kein genaues Geburts- bzw. Sterbejahr";
            }
            if (yearsOfLife.equals("<1")) {
              return "Die Person wurde nicht älter als ein Jahr";
            }
          }
        }
        return null;
      }
    });
  }

  @Override
  protected void showResultDetails(final CustomLayout content, final Table resultTable, final Person result) {

    final Panel descriptionPanel = new Panel();
    descriptionPanel.setStyleName("description");
    descriptionPanel.addStyleName(Reindeer.PANEL_LIGHT);
    descriptionPanel.setCaption("Beschreibung");
    descriptionPanel.setHeight(130, Unit.PIXELS);
    descriptionPanel.setWidth(450, Unit.PIXELS);
    final VerticalLayout infoPanelLayout = new VerticalLayout();
    infoPanelLayout.addComponent(new Label("Code: " + (result != null ? result.getPersonCode() : "")));
    infoPanelLayout.addComponent(new Label(result != null ? result.getDescription() : ""));
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
      final Label hint = new Label("Klicken Sie bitte hier um weitere Informationen zur gewählten Person zu erfragen");
      hint.setStyleName("hint");
      hint.addStyleName("contact");
      new BrowserWindowOpener(new ExternalResource("mailto:wehlmann@altes-leipzig.de?subject=Detailanfrage für PersonenCode '"
          + result.getPersonCode() + "'")).extend(hint);
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
