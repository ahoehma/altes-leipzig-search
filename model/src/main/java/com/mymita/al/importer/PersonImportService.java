package com.mymita.al.importer;

import static com.googlecode.jcsv.reader.internal.CSVReaderBuilder.newDefaultReader;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.googlecode.jcsv.reader.CSVReader;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.Person.Gender;
import com.mymita.al.repository.PersonRepository;

@Service
public class PersonImportService implements ImportService<Person> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersonImportService.class);

  private static final Function<String[], Person> TO_PERSON = data -> {
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
    final String image = data[10];
    final String link = data[11];
    return Person.builder()
        .personCode(code)
        .lastName(lastName)
        .firstName(firstName)
        .birthName(birthName)
        .gender(gender)
        .yearOfBirth(yearOfBirth)
        .yearOfDeath(yearOfDeath)
        .yearsOfLife(yearsOfLife)
        .description(description)
        .reference(reference)
        .link(link)
        .image(image)
        .build();
  };

  @Nullable
  private static Gender asGender(final String value) {
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

  @Autowired
  private PersonRepository personRepository;

  @Override
  public void importData(final Resource resource, final ImportListener<Person> importListener) {
    final List<String[]> persons = readPersons(resource);
    if (persons == null) {
      LOGGER.warn("Nothing to import persons from '{}'", resource);
      return;
    }
    importPersons(persons, importListener);
    LOGGER.debug("Added '{}' persons successfully", persons.size());
  }

  void importPersons(final List<String[]> persons, final ImportListener<Person> importListener) {
    LOGGER.debug("Before import lets delete all persons first");
    importListener.startDelete(personRepository.count());
    personRepository.deleteAllInBatch();
    importListener.finishedDelete();
    LOGGER.debug("Before import deleted all persons");
    final int max = persons.size();
    importListener.startImport(max);
    int i = 1;
    final int chunkSize = 5000;
    LOGGER.debug("Start adding given '{}' persons in chunks with '{}' persons", max, chunkSize);
    for (final List<String[]> data : Lists.partition(persons, chunkSize)) {
      try {
        final Iterable<Person> importedPersons = personRepository.saveAll(data.stream().map(TO_PERSON).collect(toList()));
        for (final Person importedPerson : importedPersons) {
          importListener.progressImport(importedPerson, i, max);
          i++;
        }
      } catch (final org.springframework.dao.DataIntegrityViolationException e) {
        throw new org.springframework.dao.DataIntegrityViolationException(format("Can't import persons '%s'", data), e);
      }
    }
    importListener.finishedImport(i - 1);
  }

  @Nullable
  private ImmutableList<String[]> readPersons(final Resource resource) {
    try (final CSVReader<String[]> personReader = newDefaultReader(new InputStreamReader(resource.getInputStream(), ISO_8859_1))) {
      personReader.readHeader();
      return ImmutableList.copyOf(personReader.readAll());
    } catch (final IOException e) {
      LOGGER.error("Can't import persons from '{}'", resource, e);
      return null;
    }
  }

}
