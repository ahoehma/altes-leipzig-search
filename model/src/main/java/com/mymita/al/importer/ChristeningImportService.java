package com.mymita.al.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Charsets;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.mymita.al.domain.Christening;
import com.mymita.al.repository.MarriageRepository;

@Service
public class ChristeningImportService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChristeningImportService.class);

  @Autowired
  transient MarriageRepository marriageRepository;
  @Autowired
  transient Neo4jTemplate template;

  private void importChristening(final Christening christening, final ImportListener<Christening> importListener) throws IOException {
    template.save(christening);
    if (importListener != null) {
      importListener.onImport(christening);
    }
  }

  public void importChristenings(final File file, final ImportListener<Christening> importListener) throws IOException {
    // final Reader csvFile = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));
    final Reader csvFile = new InputStreamReader(new FileInputStream(file), Charsets.ISO_8859_1);
    final CSVReader<String[]> marriageReader = CSVReaderBuilder.newDefaultReader(csvFile);
    // PersCode1, FamName, MVorname, PersCode2, GebName, FVorname, Jahr, FamCode, PersMBeruf, PersMOrt, PersWBeruf, PersWOrt, DatHeirat,
    // Zeitraum, Kirche, Quelle
    marriageReader.readHeader();
    final List<String[]> marriages = marriageReader.readAll();
    if (importListener != null) {
      importListener.startImport(Christening.class, marriages.size());
    }
    LOGGER.debug("Delete all christening");
    marriageRepository.deleteAll();
    LOGGER.debug("Create '{}' christenings ...", marriages.size());
    importChristenings(marriages, importListener);
  }

  private void importChristenings(final List<String[]> marriages, final ImportListener<Christening> importListener) {
    final Transaction tx = template.getGraphDatabase().beginTx();
    try {
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
            new Christening().personCode1(kPersCode).taufKind(taufKind).year(jahr).church(kirche).familyCode(famCode)
                .personCode2(kPersCode).personCode2(mPersCode).reference(quelle).profession(taetigkeit).reference(quelle)
                .firstNameFather(vaterVName).lastNameFather(vaterName), importListener);
      }
      tx.success();
      tx.finish();
    } catch (final Exception e) {
      LOGGER.error("Can't import marriages", e);
      tx.failure();
    }
  }

  public void importChristenings(final String csv, final ImportListener<Christening> importListener) throws IOException {
    importChristenings(new File(csv), importListener);
  }
}