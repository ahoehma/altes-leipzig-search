package com.mymita.al.importer;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mymita.al.importer.ImportService.CountingImportListener;

public class Importer {

  public static void main(final String[] args) throws IOException {
    final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:application-context.xml");
    try {
      new Importer().importData(ctx);
    } finally {
      ctx.close();
    }
  }

  private void importData(final ClassPathXmlApplicationContext ctx) throws BeansException, IOException {
    ctx.getBean(ImportService.class).importPersons("C:/Users/Andreas Höhmann/Dropbox/Datenbank/00 Personen Abfrage 1212.txt",
        new CountingImportListener<Object>() {

          @Override
          public void onImport(final Object object) {
            super.onImport(object);
            System.out.println(String.format("[%d/%d] Imported %s", count(object), max(object), object));
          }
        });
  }

}
