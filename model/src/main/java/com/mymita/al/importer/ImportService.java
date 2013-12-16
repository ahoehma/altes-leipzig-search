package com.mymita.al.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.Person.Gender;
import com.mymita.al.repository.PersonRepository;

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

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportService.class);

  @Autowired
  transient PersonRepository  personRepository;
  @Autowired
  transient Neo4jTemplate     template;

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

  public void importPerson(final Person person, final ImportListener<Object> importListener) throws IOException {
    template.save(person);
    if (importListener != null) {
      importListener.onImport(person);
    }
  }

  public void importPersons(final File file, final ImportListener<Object> importListener) throws IOException {
    // final Reader csvFile = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));
    final Reader csvFile = new InputStreamReader(new FileInputStream(file), Charsets.ISO_8859_1);
    final CSVReader<String[]> personReader = CSVReaderBuilder.newDefaultReader(csvFile);
    // PersCode, Nachname, Vorname, Geburtsname, Sex, Geburtsjahr, Sterbejahr, Alter, Beschreibung, Quelle, Bild/Link
    personReader.readHeader();
    final List<String[]> persons = personReader.readAll();
    if (importListener != null) {
      importListener.startImport(Person.class, persons.size());
    }
    LOGGER.debug("Delete all persons");
    personRepository.deleteAll();
    LOGGER.debug("Create '{}' Persons ...", persons.size());
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
        final String yearOfBirth = data[5];
        final String yearOfDeath = data[6];
        final String yearsOfLife = data[7];
        final String description = data[8];
        final String reference = data[9];
        final String link = data[10];
        final Person newPerson = new Person().code(code).lastName(lastName).firstName(firstName).birthName(birthName).gender(gender)
            .yearOfBirth(yearOfBirth).yearOfDeath(yearOfDeath).yearsOfLife(yearsOfLife).description(description).reference(reference);
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

}
