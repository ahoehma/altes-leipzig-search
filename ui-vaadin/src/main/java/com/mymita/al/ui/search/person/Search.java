package com.mymita.al.ui.search.person;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.io.ClassPathResource;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.Person.Gender;
import com.mymita.al.service.PersonService;
import com.mymita.al.ui.search.AbstractSearch;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Configurable
@Theme("default")
@PreserveOnRefresh
public class Search extends AbstractSearch<Person> {

  @Autowired
  transient PersonService personService;

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
    name.setWidth("150px");
    name.setRequired(false);
    name.setValidationVisible(false);
    name.focus();
    final TextField yearOfBirth = new TextField("Geburtsjahr");
    yearOfBirth.setWidth("80px");
    final TextField yearOfDeath = new TextField("Sterbejahr");
    yearOfDeath.setWidth("80px");
    final Button search = new Button("Suche starten", new Button.ClickListener() {

      @Override
      public void buttonClick(final ClickEvent event) {
        final String nameValue = name(name.getValue());
        final String yearOfBirthValue = String.valueOf(MoreObjects.firstNonNull(number(yearOfBirth.getValue()), ""));
        final String yearOfDeathValue = String.valueOf(MoreObjects.firstNonNull(number(yearOfDeath.getValue()), ""));
        showHits(ImmutableList.copyOf(personService.find(nameValue, yearOfBirthValue, yearOfDeathValue)));
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
    resultTable.setColumnWidth("firstName", 140);
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
  protected void showResultDetails(final CustomLayout content, final Table resultTable, @Nullable final Person result) {

    final Panel descriptionPanel = new Panel();
    descriptionPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
    descriptionPanel.setCaption("Beschreibung");
    descriptionPanel.setHeight(130, Unit.PIXELS);
    descriptionPanel.setWidth(470, Unit.PIXELS);
    final VerticalLayout infoPanelLayout = new VerticalLayout();
    infoPanelLayout.addComponent(new Label("Code: " + (result != null ? result.getPersonCode() : "")));
    infoPanelLayout.addComponent(new Label(result != null ? result.getDescription() : ""));
    descriptionPanel.setContent(infoPanelLayout);

    final Panel imagePanel = new Panel();
    imagePanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
    imagePanel.addStyleName("image");
    imagePanel.setCaption("Bild/Vorschau");
    imagePanel.setHeight(100, Unit.PERCENTAGE);
    imagePanel.setWidth(220, Unit.PIXELS);
    if (result != null && !Strings.isNullOrEmpty(result.getImage())) {
      final HorizontalLayout imagePanelLayout = new HorizontalLayout();
      imagePanelLayout.setSizeFull();
      imagePanelLayout.setStyleName("image-panel");
      final Image image = new Image(null, new ExternalResource("http://www.altes-leipzig.de/quelle/tumb/" + result.getImage() + ".jpg"));
      image.setSizeFull();
      imagePanelLayout.addComponent(image);
      imagePanelLayout.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
      imagePanel.setContent(imagePanelLayout);
    } else {
      final Label human = new Label("<i class=\"fi-torso image-preview\"/>", ContentMode.HTML);
      human.setSizeUndefined();
      final CssLayout imagePanelLayout = new CssLayout();
      imagePanelLayout.setStyleName("image-panel");
      imagePanel.setContent(imagePanelLayout);
    }

    final Panel referencePanel = new Panel();
    referencePanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
    referencePanel.setCaption("Quelle / Ersterwähnung");
    referencePanel.setWidth(470, Unit.PIXELS);
    final VerticalLayout referencePanelLayout = new VerticalLayout();
    referencePanelLayout.setStyleName("reference-panel");
    referencePanelLayout.setSizeFull();
    referencePanelLayout.addComponent(new Label(result != null ? result.getReference() : ""));
    referencePanel.setContent(referencePanelLayout);

    final Panel linkPanel = new Panel();
    linkPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
    linkPanel.setCaption("Weitere Informationen");
    linkPanel.setWidth(100, Unit.PERCENTAGE);
    final VerticalLayout linkPanelLayout = new VerticalLayout();
    linkPanelLayout.setStyleName("link-panel");
    linkPanelLayout.setSizeFull();
    linkPanel.setContent(linkPanelLayout);
    if (result != null) {
      final String link = result.getLink().replace("#", "");
      final Link externalLink = new Link(link, new ExternalResource(link));
      externalLink.setTargetName("_blank");
      linkPanelLayout.addComponent(externalLink);
    }

    final GridLayout infos = new GridLayout(2, 4);
    infos.setSizeFull();
    infos.setStyleName("result-details");
    infos.setWidth(100, Unit.PERCENTAGE);
    infos.addComponent(descriptionPanel, 0, 1);
    infos.addComponent(referencePanel, 0, 2);
    infos.addComponent(imagePanel, 1, 1, 1, 2);
    infos.addComponent(linkPanel, 0, 3, 1, 3);
    infos.setRowExpandRatio(0, 0);
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
      new BrowserWindowOpener(new ExternalResource("mailto:wehlmann@altes-leipzig.de?subject=Detailanfrage für PersonenCode '"
          + result.getPersonCode() + "'")).extend(hint);
      help.addComponent(hint);
    }
    content.removeComponent("help");
    content.addComponent(help, "help");
  }
}
