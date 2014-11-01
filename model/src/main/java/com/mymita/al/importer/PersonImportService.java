package com.mymita.al.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.Person.Gender;
import com.mymita.al.repository.PersonRepository;

@Service
public class PersonImportService implements ImportService<Person> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersonImportService.class);

  @Autowired
  transient PersonRepository personRepository;

  private Gender asGender(final String value) {
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

  @Override
  @Transactional
  public void importData(final File file, final ImportListener<Person> importListener) {
    final List<String[]> persons = readPersons(file, importListener);
    if (persons == null) {
      LOGGER.warn("Nothing to import from person file '{}'", file.getAbsolutePath());
      return;
    }
    LOGGER.debug("Delete all persons");
    personRepository.deleteAll();
    LOGGER.debug("Create '{}' persons ...", persons.size());
    importPersons(persons, importListener);
    LOGGER.debug("Created '{}' persons", persons.size());
  }

  private Person importPersonInternal(final Person person, final ImportListener<Person> importListener) {
    final Person importedPerson = personRepository.save(person);
    if (importListener != null) {
      importListener.progressImport(person);
    }
    return importedPerson;
  }

  private void importPersons(final List<String[]> persons, final ImportListener<Person> importListener) {
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
      importPersonInternal(
          Person.builder().personCode(code).lastName(lastName).firstName(firstName).birthName(birthName).gender(gender)
              .yearOfBirth(yearOfBirth).yearOfDeath(yearOfDeath).yearsOfLife(yearsOfLife).description(description).reference(reference)
              .build(), importListener);
    }
  }

  public void importPersons(final String csv, final ImportListener<Person> importListener) throws IOException {
    importData(new File(csv), importListener);
  }

  @Nullable
  private ImmutableList<String[]> readPersons(final File file, final ImportListener<Person> importListener) {
    try {
      final Reader csvFile = new InputStreamReader(new FileInputStream(file), Charsets.ISO_8859_1);
      final CSVReader<String[]> personReader = CSVReaderBuilder.newDefaultReader(csvFile);
      // PersCode, Nachname, Vorname, Geburtsname, Sex, Geburtsjahr, Sterbejahr, Alter, Beschreibung, Quelle, Bild/Link
      personReader.readHeader();
      final List<String[]> persons = personReader.readAll();
      if (importListener != null) {
        importListener.startImport(Person.class, persons.size());
      }
      return ImmutableList.copyOf(persons);
    } catch (final IOException e) {
      LOGGER.error("Can't import persons from file '{}'", file.getAbsolutePath(), e);
      return null;
    }
  }

}
