package com.mymita.al.importer;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.testng.annotations.Test;

import com.mymita.al.domain.Person;
import com.mymita.al.repository.PersonRepository;

@ContextConfiguration(locations = {"classpath:/META-INF/spring/context-test.xml"})
public class PersonImportServiceTest extends AbstractTransactionalTestNGSpringContextTests {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersonImportServiceTest.class);

  @Autowired
  transient ImportService<Person> personImportService;
  @Autowired
  transient PersonRepository personRepository;

  @Test
  public void importCsv() throws Exception {
    personImportService.importData(new ClassPathResource("00 Personen Abfrage.txt", PersonImportServiceTest.class),
        new ImportListener<Person>() {

          @Override
          public void finishedImport(final int count) {
          }

          @Override
          public void progressImport(final Person object, final int i, final int max) {
            LOGGER.debug("[{}/{}] Imported person {}", i, max, object);
          }

          @Override
          public void startImport(final int max) {
          }
        });
    assertThat(personRepository.count(), Matchers.is(24L));
  }

  @BeforeTransaction
  public void setupData() throws Exception {
    deleteFromTables("person");
    assertThat(personRepository.count(), Matchers.is(0L));
  }
}
