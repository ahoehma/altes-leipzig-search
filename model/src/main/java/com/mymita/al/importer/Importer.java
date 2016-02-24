package com.mymita.al.importer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

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
    ctx.getBean(ChristeningImportService.class).importData(
        new FileSystemResource("C:/Users/Andreas Höhmann/Dropbox/Datenbank/02 Taufe Abfrage.txt"), new ImportListener<Christening>() {

          @Override
          public void finishedImport() {
          }

          @Override
          public void progressImport(final Christening object, final int i, final int max) {
            LOGGER.debug("[{}/{}] Imported Christening {}", i, max, object);
          }

          @Override
          public void startImport(final int max) {
          }
        });
  }

  private void importMarriages(final ClassPathXmlApplicationContext ctx) throws BeansException, IOException {
    ctx.getBean(MarriageImportService.class).importData(
        new FileSystemResource("C:/Users/Andreas Höhmann/Dropbox/Datenbank/01 Heirat Abfrage.txt"), new ImportListener<Marriage>() {

          @Override
          public void finishedImport() {
          }

          @Override
          public void progressImport(final Marriage object, final int i, final int max) {
            LOGGER.debug("[{}/{}] Imported Marriage {}", i, max, object);
          }

          @Override
          public void startImport(final int max) {
          }
        });
  }

  private void importPersons(final ClassPathXmlApplicationContext ctx) throws BeansException, IOException {
    ctx.getBean(PersonImportService.class).importData(
        new FileSystemResource("C:/Users/Andreas Höhmann/Dropbox/Datenbank/00 Personen Abfrage.txt"), new ImportListener<Person>() {

          @Override
          public void finishedImport() {
          }

          @Override
          public void progressImport(final Person object, final int i, final int max) {
            LOGGER.debug("[{}/{}] Imported Person {}", i, max, object);
          }

          @Override
          public void startImport(final int max) {
          }
        });
  }

}
