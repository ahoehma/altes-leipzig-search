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

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.mymita.al.domain.Marriage;
import com.mymita.al.repository.MarriageRepository;

@Service
public class MarriageImportService implements ImportService<Marriage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MarriageImportService.class);

  @Autowired
  transient MarriageRepository marriageRepository;

  @Override
  public void importData(final File file, final ImportListener<Marriage> importListener) {
    final List<String[]> marriages = readMarriages(file, importListener);
    if (marriages == null) {
      LOGGER.warn("Nothing to import from marriages file '{}'", file.getAbsolutePath());
      return;
    }
    LOGGER.debug("Delete all marriages");
    marriageRepository.deleteAll();
    LOGGER.debug("Create '{}' marriages ...", marriages.size());
    importMarriages(marriages, importListener);
    LOGGER.debug("Created '{}' marriages", marriages.size());
  }

  private void importMarriage(final Marriage marriage, final ImportListener<Marriage> importListener) {
    marriageRepository.save(marriage);
    if (importListener != null) {
      importListener.progressImport(marriage);
    }
  }

  private void importMarriages(final List<String[]> marriages, final ImportListener<Marriage> importListener) {
    for (final String[] data : marriages) {
      final String persCode1 = data[0];
      final String famName = data[1];
      final String mVorname = data[2];
      final String persCode2 = data[3];
      final String gebName = data[4];
      final String fVorname = data[5];
      final String jahr = data[6];
      final String famCode = data[7];
      final String persMBeruf = data[8];
      final String persMOrt = data[9];
      final String persWBeruf = data[10];
      final String persWOrt = data[11];
      final String datHeirat = data[12];
      final String zeitraum = data[13];
      final String kirche = data[14];
      final String quelle = data[15];
      final Marriage newMarriage = new Marriage().familyCode(famCode).personCode1(persCode1).personCode2(persCode2).dateMarriage(datHeirat)
          .church(kirche).reference(quelle).periodMarriage(zeitraum).year(jahr).professionPerson1(persMBeruf).professionPerson2(persWBeruf)
          .cityPerson1(persMOrt).cityPerson2(persWOrt).lastNamePerson1(famName).birthNamePerson2(gebName).firstNamePerson1(mVorname)
          .firstNamePerson2(fVorname);
      importMarriage(newMarriage, importListener);
    }
  }

  public void importMarriages(final String csv, final ImportListener<Marriage> importListener) throws IOException {
    importData(new File(csv), importListener);
  }

  @Nullable
  private ImmutableList<String[]> readMarriages(final File file, final ImportListener<Marriage> importListener) {
    try {
      final Reader csvFile = new InputStreamReader(new FileInputStream(file), Charsets.ISO_8859_1);
      final CSVReader<String[]> marriageReader = CSVReaderBuilder.newDefaultReader(csvFile);
      // PersCode1, FamName, MVorname, PersCode2, GebName, FVorname, Jahr, FamCode, PersMBeruf, PersMOrt, PersWBeruf, PersWOrt, DatHeirat,
      // Zeitraum, Kirche, Quelle
      marriageReader.readHeader();
      final List<String[]> marriages = marriageReader.readAll();
      if (importListener != null) {
        importListener.startImport(Marriage.class, marriages.size());
      }
      return ImmutableList.copyOf(marriages);
    } catch (final IOException e) {
      LOGGER.error("Can't import marriages from file '{}'", file.getAbsolutePath(), e);
      return null;
    }
  }
}