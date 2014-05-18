package com.mymita.al.importer;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.testng.annotations.Test;

import com.mymita.al.domain.Person;
import com.mymita.al.repository.PersonRepository;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/context-test.xml" })
public class PersonImportServiceTest extends AbstractTransactionalTestNGSpringContextTests {

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
