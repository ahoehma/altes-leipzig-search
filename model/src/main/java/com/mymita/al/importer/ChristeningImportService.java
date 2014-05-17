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
import com.mymita.al.domain.Christening;
import com.mymita.al.repository.ChristeningRepository;

@Service
public class ChristeningImportService implements ImportService<Christening> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChristeningImportService.class);

  @Autowired
  transient ChristeningRepository christeningRepository;

  private void importChristening(final Christening christening, final ImportListener<Christening> importListener) {
    christeningRepository.save(christening);
    if (importListener != null) {
      importListener.progressImport(christening);
    }
  }

  private void importChristenings(final List<String[]> marriages, final ImportListener<Christening> importListener) {
    for (final String[] data : marriages) {
      final String kPersCode = data[0];
      final String taufKind = data[1];
      final String jahr = data[2];
      final String kirche = data[3];
      final String famCode = data[4];
      final String mPersCode = data[5];
      final String vaterName = data[6];
      final String vaterVName = data[8];
      final String taetigkeit = data[9];
      final String quelle = data[9];
      importChristening(
          new Christening().personCode1(kPersCode).taufKind(taufKind).year(jahr).church(kirche).familyCode(famCode).personCode2(kPersCode)
          .personCode2(mPersCode).reference(quelle).profession(taetigkeit).reference(quelle).firstNameFather(vaterVName)
          .lastNameFather(vaterName), importListener);
    }
  }

  public void importChristenings(final String csv, final ImportListener<Christening> importListener) throws IOException {
    importData(new File(csv), importListener);
  }

  @Override
  public void importData(final File file, final ImportListener<Christening> importListener) {
    final List<String[]> christenings = readChristenings(file, importListener);
    if (christenings == null) {
      LOGGER.warn("Nothing to import from christenings file '{}'", file.getAbsolutePath());
      return;
    }
    LOGGER.debug("Delete all christenings");
    christeningRepository.deleteAll();
    LOGGER.debug("Create '{}' christenings ...", christenings.size());
    importChristenings(christenings, importListener);
    LOGGER.debug("Created '{}' christenings", christenings.size());
  }

  @Nullable
  private ImmutableList<String[]> readChristenings(final File file, final ImportListener<Christening> importListener) {
    try {
      final Reader csvFile = new InputStreamReader(new FileInputStream(file), Charsets.ISO_8859_1);
      final CSVReader<String[]> christeningReader = CSVReaderBuilder.newDefaultReader(csvFile);
      // PersCode1, FamName, MVorname, PersCode2, GebName, FVorname, Jahr, FamCode, PersMBeruf, PersMOrt, PersWBeruf, PersWOrt, DatHeirat,
      // Zeitraum, Kirche, Quelle
      christeningReader.readHeader();
      final List<String[]> christenings = christeningReader.readAll();
      if (importListener != null) {
        importListener.startImport(Christening.class, christenings.size());
      }
      return ImmutableList.copyOf(christenings);
    } catch (final IOException e) {
      LOGGER.error("Can't import christenings from file '{}'", file.getAbsolutePath(), e);
      return null;
    }
  }
}