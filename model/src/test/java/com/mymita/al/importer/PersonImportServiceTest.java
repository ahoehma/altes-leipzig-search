package com.mymita.al.importer;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.testng.annotations.Test;

import com.mymita.al.domain.Person;
import com.mymita.al.repository.PersonRepository;

@SpringApplicationConfiguration(classes = PersonImportServiceTest.TestConfig.class)
public class PersonImportServiceTest extends AbstractTransactionalTestNGSpringContextTests {

  @Configuration
  @EnableAutoConfiguration
  @ComponentScan
  @EnableJpaRepositories(basePackages = { "com.mymita.al.repository" })
  @EntityScan(basePackages = { "com.mymita.al.domain" })
  static class TestConfig {
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(PersonImportServiceTest.class);

  @Autowired
  transient PersonImportService personImportService;
  @Autowired
  transient PersonRepository personRepository;

  @Test
  public void importCsv() throws Exception {
    personImportService.importPersons("C:/Users/Andreas Höhmann/Dropbox/Datenbank/00 Personen Abfrage.txt",
        new CountingImportListener<Person>() {

          @Override
          public void progressImport(final Person object) {
            super.progressImport(object);
            LOGGER.debug("[{}/{}] Imported person {}", count(object), max(object), object);
          }
        });
  }

  @BeforeTransaction
  public void setupData() throws Exception {
    deleteFromTables("person");
    assertThat(personRepository.count(), Matchers.is(0L));
  }
}
