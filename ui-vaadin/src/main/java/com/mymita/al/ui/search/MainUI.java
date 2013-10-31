package com.mymita.al.ui.search;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.neo4j.annotation.QueryType;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.conversion.EntityResultConverter;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.Tag;
import com.mymita.al.repository.PersonRepository;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Configurable
@Theme("default")
@PreserveOnRefresh
public class MainUI extends UI {

  public static class PersonDTO implements Serializable {

    String   code;
    String   yearOfBirth;
    String   yearOfDeath;
    String   firstName;
    String   lastName;
    String   birthName;
    String   yearsOfLife;
    Set<Tag> tags;

    public String getBirthName() {
      return birthName;
    }

    public String getCode() {
      return code;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public ImmutableList<String> getTags() {
      return FluentIterable.from(tags).transform(new Function<Tag, String>() {
        @Override
        public String apply(final Tag input) {
          return input.getName();

        }
      }).toList();
    }

    public String getYearOfBirth() {
      return yearOfBirth;
    }

    public String getYearOfDeath() {
      return yearOfDeath;
    }

    public String getYearsOfLife() {
      return yearsOfLife;
    }

    public void setBirthName(final String birthName) {
      this.birthName = birthName;
    }

    public void setCode(final String code) {
      this.code = code;
    }

    public void setFirstName(final String firstName) {
      this.firstName = firstName;
    }

    public void setLastName(final String lastName) {
      this.lastName = lastName;
    }

    public void setTags(final Set<Tag> tags) {
      this.tags = tags;

    }

    public void setYearOfBirth(final String yearOfBirth) {
      this.yearOfBirth = yearOfBirth;
    }

    public void setYearOfDeath(final String yearOfDeath) {
      this.yearOfDeath = yearOfDeath;
    }

    public void setYearsOfLife(final String yearsOfLife) {
      this.yearsOfLife = yearsOfLife;
    }
  }

  private static Date asDate(final String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      return new SimpleDateFormat("dd.MM.yyyyy hh:mm:ss").parse(String.format("1.1.%s 00:00:00", value));
    } catch (final ParseException e) {
    }
    return null;
  }

  private static String getYearFromDate(final Date date) {
    if (date == null) {
      return "";
    }
    return new SimpleDateFormat("yyyy").format(date);
  }

  @Autowired
  transient PersonRepository service;
  @Autowired
  transient Neo4jTemplate    template;

  @Override
  protected void init(final VaadinRequest request) {
    getPage().setTitle("Altes Leipzig Suche");
    setSizeFull();
    try {
      final CustomLayout content = new CustomLayout(new ClassPathResource("/search.html").getInputStream());
      content.setSizeFull();
      final BeanItemContainer<PersonDTO> dataSource = new BeanItemContainer<PersonDTO>(PersonDTO.class);
      final PersonSearchResultTable results = new PersonSearchResultTable();
      results.setStyleName(Reindeer.TABLE_BORDERLESS);
      results.setColumnHeader("code", "Code");
      results.setColumnHeader("firstName", "Vorname");
      results.setColumnHeader("lastName", "Nachname");
      results.setColumnHeader("yearOfBirth", "Geburtsjahr");
      results.setColumnHeader("yearOfDeath", "Sterbejahr");
      results.setColumnHeader("yearsOfLife", "Alter");
      results.setContainerDataSource(dataSource);
      results.setVisibleColumns(new Object[] { "code", "lastName", "birthName", "firstName", "yearOfBirth", "yearOfDeath", "yearsOfLife" });
      results.setVisible(false);
      results.setSelectable(true);
      results.setMultiSelect(true);
      results.addValueChangeListener(new ValueChangeListener() {

        @Override
        public void valueChange(final ValueChangeEvent event) {
          final Collection<PersonDTO> persons = (Collection<PersonDTO>) event.getProperty().getValue();
          if (!persons.isEmpty()) {
            final VerticalLayout infos = new VerticalLayout();
            infos.setMargin(new MarginInfo(true, false, true, false));
            infos.setSpacing(true);
            infos.addComponent(new Label("<b>Informationen zu den gewählten Personen</b>", ContentMode.HTML));
            for (final PersonDTO person : persons) {
              final HorizontalLayout hl = new HorizontalLayout();
              hl.addComponent(new Label(String.format("<b>%s</b>:&nbsp;", person.getCode()), ContentMode.HTML));
              hl.addComponent(new Label(Joiner.on("").join(person.getTags())));
              infos.addComponent(hl);
            }
            content.addComponent(infos, "description");
          } else {
            content.removeComponent("description");
          }
        }
      });
      results.setImmediate(true);
      results.setPageLength(15);
      final HorizontalLayout resultsControl = results.createControls();
      resultsControl.setVisible(false);
      final TextField name = new TextField("Name");
      name.setNullSettingAllowed(true);
      name.setNullRepresentation("");
      name.setValue(null);
      name.setStyleName("search");
      name.setWidth("150px");
      name.setRequired(false);
      name.setValidationVisible(false);
      final TextField yearOfBirth = new TextField("Geburtsjahr");
      yearOfBirth.setStyleName("search");
      yearOfBirth.setWidth("80px");
      final Button search = new NativeButton("Suche starten", new Button.ClickListener() {

        @Override
        public void buttonClick(final ClickEvent event) {
          final String nameValue = name.getValue();
          final String yearOfBirthValue = yearOfBirth.getValue();
          if (!Strings.isNullOrEmpty(nameValue) && !Strings.isNullOrEmpty(yearOfBirthValue)) {
            final String q = String.format("START person=node:__types__(className='Person') " + "WHERE (person.birthName =~ '(?i)%s' "
                + "OR person.lastName =~ '(?i)%s') AND person.dateOfBirth! = {dateOfBirth} " + "RETURN person", nameValue, nameValue);
            final Map<String, Object> params = Maps.newHashMap();
            params.put("dateOfBirth", asDate(yearOfBirthValue));
            final List<Person> persons = Lists.newArrayList(template.getGraphDatabase().queryEngineFor(QueryType.Cypher).query(q, params)
                .to(Person.class, new EntityResultConverter<Object, Person>(template.getConversionService(), template)).iterator());
            showHits(persons);
          } else {
            if (!Strings.isNullOrEmpty(nameValue)) {
              final String q = String.format("START person=node:__types__(className='Person') " + "WHERE (person.birthName =~ '(?i)%s' "
                  + "OR person.lastName =~ '(?i)%s') " + "RETURN person", nameValue, nameValue);
              final List<Person> persons = Lists.newArrayList(template.getGraphDatabase().queryEngineFor(QueryType.Cypher).query(q, null)
                  .to(Person.class, new EntityResultConverter<Object, Person>(template.getConversionService(), template)).iterator());
              showHits(persons);
            }
            if (!Strings.isNullOrEmpty(yearOfBirthValue)) {
              final String q = "START person=node:__types__(className='Person') "
                  + "WHERE person.dateOfBirth! = {dateOfBirth} RETURN person";
              final Map<String, Object> params = Maps.newHashMap();
              params.put("dateOfBirth", asDate(yearOfBirthValue));
              final List<Person> persons = Lists.newArrayList(template.getGraphDatabase().queryEngineFor(QueryType.Cypher).query(q, params)
                  .to(Person.class, new EntityResultConverter<Object, Person>(template.getConversionService(), template)).iterator());
              showHits(persons);
            }
          }
        }

        private void showHits(final List<Person> persons) {
          dataSource.removeAllItems();
          dataSource.addAll(FluentIterable.from(persons).transform(new Function<Person, PersonDTO>() {
            @Override
            public PersonDTO apply(final Person input) {
              final PersonDTO result = new PersonDTO();
              result.setCode(input.getCode());
              result.setYearOfBirth(getYearFromDate(input.getDateOfBirth()));
              result.setYearOfDeath(getYearFromDate(input.getDateOfDeath()));
              result.setFirstName(input.getFirstName());
              result.setLastName(input.getLastName());
              result.setBirthName(input.getBirthName());
              result.setYearsOfLife(input.getYearsOfLife());
              result.setTags(input.getTags());
              return result;

            }
          }).toList());
          if (!persons.isEmpty()) {
            results.setContainerDataSource(dataSource);
            results.setVisibleColumns(new Object[] { "code", "lastName", "birthName", "firstName", "yearOfBirth", "yearOfDeath",
                "yearsOfLife" });
            results.setVisible(true);
            content.addComponent(results.createControls(), "results-control");
          } else {
            results.setVisible(false);
            content.removeComponent("results-control");
            final Notification notification = new Notification("Ihre Suche ergab leider keine Ergebnisse", Type.HUMANIZED_MESSAGE);
            notification.setDelayMsec(2000);
            notification.show(Page.getCurrent());
          }
        }

      });
      search.setClickShortcut(KeyCode.ENTER);
      content.addComponent(name, "name");
      content.addComponent(yearOfBirth, "yearOfBirth");
      content.addComponent(search, "search");
      content.addComponent(results, "results");
      setContent(content);
    } catch (final IOException e) {
    }
  }
}
