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
import com.mymita.al.domain.Marriage;
import com.mymita.al.repository.MarriageRepository;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/context-test.xml" })
public class MarriageServiceTest extends AbstractTransactionalTestNGSpringContextTests {

  @Autowired
  transient MarriageService marriageService;
  @Autowired
  transient MarriageRepository marriageRepository;

  @Test
  public void findByLastNamePerson1OrBirthNamePerson2AndYearNeg() throws Exception {
    assertThat(marriageService.find("Höhmann", "1900"), Matchers.<Marriage> iterableWithSize(0));
    assertThat(marriageService.find("Krug", "1900"), Matchers.<Marriage> iterableWithSize(0));
  }

  @Test
  public void findByLastNamePerson1OrBirthNamePerson2AndYearPos() throws Exception {
    assertThat(marriageService.find("Höhmann", "2010"), Matchers.<Marriage> iterableWithSize(1));
    assertThat(marriageService.find("Krug", "2010"), Matchers.<Marriage> iterableWithSize(1));
  }

  @BeforeTransaction
  public void setupData() throws Exception {
    deleteFromTables("marriage");
    marriageRepository.save(new Marriage().firstNamePerson1("Andreas").lastNamePerson1("Höhmann").firstNamePerson2("Kathrin")
        .birthNamePerson2("Krug").cityPerson1("Altenburg").cityPerson2("Zwickau").familyCode("f001").personCode1("0001")
        .personCode2("0002").year("2010").dateMarriage("20.10.2010"));
    assertThat(marriageRepository.count(), Matchers.is(1L));
    assertThat(Iterables.getFirst(marriageRepository.findAll(), null).getLastNamePerson1(), is("Höhmann"));
  }
}
