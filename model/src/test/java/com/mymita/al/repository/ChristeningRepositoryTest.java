package com.mymita.al.repository;

import static com.mymita.al.domain.QChristening.christening;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.mymita.al.domain.Christening;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/context-test.xml" })
public class ChristeningRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {

  @Autowired
  transient ChristeningRepository repository;

  @Test
  public void findByLastNameFatherAndYearOfDeathNeg() throws Exception {
    assertThat(repository.findAll(christening.lastNameFather.containsIgnoreCase("höh").and(christening.year.eq("1900"))),
        Matchers.<Christening> iterableWithSize(0));
  }

  @Test
  public void findByLastNameFatherAndYearOfDeathPos() throws Exception {
    assertThat(repository.findAll(christening.lastNameFather.containsIgnoreCase("höh").and(christening.year.eq("2010"))),
        Matchers.<Christening> iterableWithSize(1));
  }

  @BeforeTransaction
  public void setupData() throws Exception {
    deleteFromTables("christening");
    repository.save(new Christening().lastNameFather("Höhmann").familyCode("f001").personCode1("0001").personCode2("0002").year("2010"));
    assertThat(repository.count(), Matchers.is(1L));
    assertThat(Iterables.getFirst(repository.findAll(), null).getLastNameFather(), is("Höhmann"));
  }

}
