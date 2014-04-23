package com.mymita.al.importer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mymita.al.domain.Christening;
import com.mymita.al.domain.Marriage;
import com.mymita.al.domain.Person;

public class Importer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Importer.class);

  public static void main(final String[] args) throws IOException {
    final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:application-context.xml");
    try {
      final Importer importer = new Importer();
      // importer.importPersons(ctx);
      importer.importMarriages(ctx);
      // importer.importChristenings(ctx);
    } finally {
      ctx.close();
    }
  }

  private void importChristenings(final ClassPathXmlApplicationContext ctx) throws BeansException, IOException {
    ctx.getBean(ChristeningImportService.class).importChristenings("C:/Users/Andreas Höhmann/Dropbox/Datenbank/02 Taufe Abfrage.txt",
        new CountingImportListener<Christening>() {

          @Override
          public void onImport(final Christening object) {
            super.onImport(object);
            LOGGER.debug("[{}/{}] Imported christening {}", count(object), max(object), object);
          }
        });
  }

  private void importMarriages(final ClassPathXmlApplicationContext ctx) throws BeansException, IOException {
    ctx.getBean(MarriageImportService.class).importMarriages("C:/Users/Andreas Höhmann/Dropbox/Datenbank/01 Heirat Abfrage.txt",
        new CountingImportListener<Marriage>() {

          @Override
          public void onImport(final Marriage object) {
            super.onImport(object);
            LOGGER.debug("[{}/{}] Imported marriage {}", count(object), max(object), object);
          }
        });
  }

  private void importPersons(final ClassPathXmlApplicationContext ctx) throws BeansException, IOException {
    ctx.getBean(PersonImportService.class).importPersons("C:/Users/Andreas Höhmann/Dropbox/Datenbank/00 Personen Abfrage.txt",
        new CountingImportListener<Person>() {

          @Override
          public void onImport(final Person object) {
            super.onImport(object);
            LOGGER.debug("[{}/{}] Imported person {}", count(object), max(object), object);
          }
        });
  }

}
