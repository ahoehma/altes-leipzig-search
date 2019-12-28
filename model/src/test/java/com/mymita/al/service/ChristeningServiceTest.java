package com.mymita.al.service;

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
import com.mymita.al.repository.ChristeningRepository;

@ContextConfiguration(locations = {"classpath:/META-INF/spring/context-test.xml"})
public class ChristeningServiceTest extends AbstractTransactionalTestNGSpringContextTests {

  @Autowired
  transient ChristeningService service;
  @Autowired
  transient ChristeningRepository repository;

  @Test
  public void findByLastNameFatherAndYearPos() throws Exception {
    assertThat(service.find("Höhmann", "2010"), Matchers.<Christening> iterableWithSize(1));
  }

  @Test
  public void findByLastNameFatherYearNeg() throws Exception {
    assertThat(service.find("Höhmann", "1900"), Matchers.<Christening> iterableWithSize(0));
  }

  @BeforeTransaction
  public void setupData() throws Exception {
    deleteFromTables("christening");
    repository.save(
        Christening.builder().lastNameFather("Höhmann").familyCode("f001").personCode1("0001").personCode2("0002").year("2010").build());
    assertThat(repository.count(), Matchers.is(1L));
    assertThat(Iterables.getFirst(repository.findAll(), null).getLastNameFather(), is("Höhmann"));
  }
}
