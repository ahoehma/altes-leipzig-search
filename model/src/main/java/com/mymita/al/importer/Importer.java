package com.mymita.al.importer;

import java.io.File;
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
      importer.importPersons(ctx);
      importer.importMarriages(ctx);
      importer.importChristenings(ctx);
    } finally {
      ctx.close();
    }
  }

  private void importChristenings(final ClassPathXmlApplicationContext ctx) throws BeansException, IOException {
    ctx.getBean(ChristeningImportService.class).importData(new File("C:/Users/Andreas Höhmann/Dropbox/Datenbank/02 Taufe Abfrage.txt"),
        new CountingImportListener<Christening>() {

          @Override
          public void progressImport(final Christening object) {
            super.progressImport(object);
            LOGGER.debug("[{}/{}] Imported christening {}", count(object), max(object), object);
          }
        });
  }

  private void importMarriages(final ClassPathXmlApplicationContext ctx) throws BeansException, IOException {
    ctx.getBean(MarriageImportService.class).importData(new File("C:/Users/Andreas Höhmann/Dropbox/Datenbank/01 Heirat Abfrage.txt"),
        new CountingImportListener<Marriage>() {

          @Override
          public void progressImport(final Marriage object) {
            super.progressImport(object);
            LOGGER.debug("[{}/{}] Imported marriage {}", count(object), max(object), object);
          }
        });
  }

  private void importPersons(final ClassPathXmlApplicationContext ctx) throws BeansException, IOException {
    ctx.getBean(PersonImportService.class).importData(new File("C:/Users/Andreas Höhmann/Dropbox/Datenbank/00 Personen Abfrage.txt"),
        new CountingImportListener<Person>() {

          @Override
          public void progressImport(final Person object) {
            super.progressImport(object);
            LOGGER.debug("[{}/{}] Imported person {}", count(object), max(object), object);
          }
        });
  }

}
