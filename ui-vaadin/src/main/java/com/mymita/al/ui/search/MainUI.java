package com.mymita.al.ui.search;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.io.ClassPathResource;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.Tag;
import com.mymita.al.repository.PersonRepository;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
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

  private static String getYearFromDate(final Date date) {
    if (date == null) {
      return "";
    }
    return new SimpleDateFormat("yyyy").format(date);
  }

  @Autowired
  transient PersonRepository service;

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
      results.addValueChangeListener(new ValueChangeListener() {

        @Override
        public void valueChange(final ValueChangeEvent event) {
          final PersonDTO person = (PersonDTO) event.getProperty().getValue();
          if (person != null) {
            final String tagsText = Joiner.on(", ").join(person.getTags());
            if (!Strings.isNullOrEmpty(tagsText)) {
              final HorizontalLayout tags = new HorizontalLayout();
              tags.addComponent(new Label("Beschreibung:"));
              tags.addComponent(new Label(tagsText));
              content.addComponent(tags, "tags");
              return;
            }
          }
          content.removeComponent("tags");
        }
      });
      results.setImmediate(true);
      results.setPageLength(15);
      final HorizontalLayout resultsControl = results.createControls();
      resultsControl.setVisible(false);
      final TextField firstName = new TextField("Vorname");
      firstName.setNullSettingAllowed(true);
      firstName.setNullRepresentation("");
      firstName.setValue(null);
      firstName.setStyleName("search");
      firstName.setWidth("150px");
      firstName.addValidator(new StringLengthValidator(
          "Bitte geben Sie mindestens 5 aber nicht mehr als 20 Buchstaben für den Vornamen ein.", 5, 20, true));
      firstName.setRequired(false);
      firstName.setValidationVisible(false);
      final TextField lastName = new TextField("Nachname");
      lastName.setNullSettingAllowed(true);
      lastName.setNullRepresentation("");
      lastName.setValue(null);
      lastName.setStyleName("search");
      lastName.setWidth("150px");
      lastName.addValidator(new StringLengthValidator(
          "Bitte geben Sie mindestens 5 aber nicht mehr als 20 Buchstaben für den Nachnamen ein.", 5, 20, true));
      lastName.setRequired(false);
      lastName.setValidationVisible(false);
      final TextField yearOfBirth = new TextField("Geburtsjahr");
      yearOfBirth.setStyleName("search");
      yearOfBirth.setWidth("80px");
      final Button search = new NativeButton("Suche starten", new Button.ClickListener() {

        @Override
        public void buttonClick(final ClickEvent event) {
          try {
            firstName.validate();
            lastName.validate();
          } catch (final InvalidValueException e) {
            final Notification notification = new Notification(e.getLocalizedMessage(), Type.HUMANIZED_MESSAGE);
            notification.setDelayMsec(2000);
            notification.show(Page.getCurrent());
            return;
          }
          final String ln = lastName.getValue();
          final String fn = firstName.getValue();
          final String by = yearOfBirth.getValue();
          if (!Strings.isNullOrEmpty(by)) {
            final Date date = new Date(Integer.valueOf(by) - 1900, 0, 0);
            final FluentIterable<Person> hits = FluentIterable.from(service.findByDateOfBirth(date, null));
            showHits(hits);
          } else if (Strings.isNullOrEmpty(ln) && !Strings.isNullOrEmpty(fn)) {
            final FluentIterable<Person> hits = FluentIterable.from(service.findByFirstNameLike(fn, null));
            showHits(hits);
          } else if (!Strings.isNullOrEmpty(ln) && Strings.isNullOrEmpty(fn)) {
            final FluentIterable<Person> hits = FluentIterable.from(service.findByLastNameLike(ln, null));
            showHits(hits);
          } else if (!Strings.isNullOrEmpty(ln) && !Strings.isNullOrEmpty(fn)) {
            final FluentIterable<Person> hits = FluentIterable.from(service.findByLastNameLikeAndFirstNameLike(ln, fn, null));
            showHits(hits);
          } else {
            showHits(FluentIterable.<Person> from(Lists.<Person> newArrayList()));
          }
        }

        private void showHits(final FluentIterable<Person> hits) {
          dataSource.removeAllItems();
          dataSource.addAll(hits.transform(new Function<Person, PersonDTO>() {
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
          if (!hits.isEmpty()) {
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
      content.addComponent(firstName, "firstName");
      content.addComponent(lastName, "lastName");
      content.addComponent(yearOfBirth, "yearOfBirth");
      content.addComponent(search, "search");
      content.addComponent(results, "results");
      setContent(content);
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
