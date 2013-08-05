package com.mymita.al.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.Person.Gender;
import com.mymita.al.domain.Tag;
import com.mymita.al.repository.PersonRepository;
import com.mymita.al.repository.TagRepository;

@Service
public class ImportService {

  public static class CountingImportListener<T> implements ImportListener<T> {

    private final Map<Class<? extends Object>, Integer> count = Maps.newHashMap();
    private final Map<Class<? extends Object>, Integer> max   = Maps.newHashMap();

    final protected int count(final T object) {
      return count.get(object.getClass());
    }

    final protected int max(final T object) {
      return max.get(object.getClass());
    }

    @Override
    public void onImport(final T object) {
      count.put(object.getClass(), 1 + Objects.firstNonNull(count.get(object.getClass()), 0));
    }

    @Override
    public void startImport(final Class<? extends Object> clazz, final int size) {
      count.put(clazz, 0);
      max.put(clazz, size);
    }
  }

  public interface ImportListener<T> {

    void onImport(final T object);

    void startImport(final Class<? extends Object> clazz, final int size);
  }

  private static final Logger    LOGGER = LoggerFactory.getLogger(ImportService.class);

  @Autowired
  transient PersonRepository     personRepository;
  @Autowired
  transient TagRepository        tagRepository;
  @Autowired
  transient Neo4jTemplate        template;

  private final Map<String, Tag> tags   = Maps.newHashMap();

  private Date asDate(final String value) throws IOException {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      return new SimpleDateFormat("dd.MM.yyyyy hh:mm:ss").parse(value);
    } catch (final ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  private Gender asGender(final String value) throws IOException {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    if (value.equals("m")) {
      return Gender.MALE;
    }
    if (value.equals("w")) {
      return Gender.FEMALE;
    }
    return null;
  }

  private Set<Tag> getTags(final Iterable<String> tags) {
    final Set<Tag> result = Sets.newHashSet();
    for (final String s : tags) {
      if (Strings.isNullOrEmpty(s)) {
        continue;
      }
      if (this.tags.containsKey(s)) {
        result.add(this.tags.get(s));
      } else {
        result.add(new Tag(s));
      }
    }
    return result;
  }

  public void importPerson(final Person person, final ImportListener<Object> importListener) throws IOException {
    template.save(person);
    if (importListener != null) {
      importListener.onImport(person);
    }
  }

  public void importPersons(final File file, final ImportListener<Object> importListener) throws IOException {
    final Reader csvFile = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));
    final CSVReader<String[]> personReader = CSVReaderBuilder.newDefaultReader(csvFile);
    // PersCode;Name;Vorname;GebName;Sex;geboren;getauft;gestorben;begraben;Jahre;Bemerkung
    personReader.readHeader();
    final List<String[]> persons = personReader.readAll();
    if (importListener != null) {
      importListener.startImport(Person.class, persons.size());
    }
    System.out.println("Delete all persons");
    personRepository.deleteAll();
    System.out.println("Delete all tags");
    tagRepository.deleteAll();
    System.out.println("Create Tags ...");
    importTags(persons, importListener);
    System.out.println("Create Persons ...");
    importPersons(persons, importListener);
  }

  private void importPersons(final List<String[]> persons, final ImportListener<Object> importListener) {
    final Transaction tx = template.getGraphDatabase().beginTx();
    try {
      for (final String[] data : persons) {
        final String code = data[0];
        final String lastName = data[1];
        final String firstName = data[2];
        final String birthName = data[3];
        final Gender gender = asGender(data[4]);
        final Date dateOfBirth = asDate(data[5]);
        final Date dateOfChristening = asDate(data[6]);
        final Date dateOfDeath = asDate(data[7]);
        final Date dateOfBuried = asDate(data[8]);
        final String yearsOfLife = data[9];
        final String description = Objects.firstNonNull(data[10], "");
        final Person newPerson = new Person().code(code).lastName(lastName).firstName(firstName).birthName(birthName).gender(gender)
            .dateOfBirth(dateOfBirth).dateOfDeath(dateOfDeath).dateOfChristening(dateOfChristening).dateOfBuried(dateOfBuried)
            .yearsOfLife(yearsOfLife).withTag(getTags(Splitter.on(",").trimResults().omitEmptyStrings().split(description.trim())));
        importPerson(newPerson, importListener);
      }
      tx.success();
      tx.finish();
    } catch (final Exception e) {
      LOGGER.error("Can't import persons", e);
      tx.failure();
    }
  }

  public void importPersons(final String csv, final ImportListener<Object> importListener) throws IOException {
    importPersons(new File(csv), importListener);
  }

  private void importTags(final List<String[]> persons, final ImportListener<Object> importListener) {
    final Transaction tx = template.getGraphDatabase().beginTx();
    try {
      for (final String[] data : persons) {
        final String description = Objects.firstNonNull(data[10], "");
        importTags(getTags(Splitter.on(",").trimResults().omitEmptyStrings().split(description.trim())), importListener);
      }
      tx.success();
      tx.finish();
    } catch (final Exception e) {
      LOGGER.error("Can't import tags", e);
      tx.failure();
    }
  }

  private Set<Tag> importTags(final Set<Tag> tags, final ImportListener<Object> importListener) {
    final Set<Tag> result = Sets.newHashSet();
    for (final Tag t : tags) {
      if (t == null) {
        continue;
      }
      if (Strings.isNullOrEmpty(t.getName())) {
        continue;
      }
      if (this.tags.containsKey(t.getName())) {
        result.add(this.tags.get(t.getName()));
        continue;
      }
      template.save(t);
      if (importListener != null) {
        importListener.startImport(Tag.class, 1);
        importListener.onImport(t);
      }
      this.tags.put(t.getName(), t);
      result.add(t);
    }
    return result;
  }
}
