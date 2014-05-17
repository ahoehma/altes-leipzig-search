package com.mymita.al.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.mymita.al.domain.Marriage;

@SpringApplicationConfiguration(classes = MarriageRepositoryTest.TestConfig.class)
public class MarriageRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {

  @Configuration
  @EnableAutoConfiguration
  @ComponentScan
  @EnableJpaRepositories(basePackages = { "com.mymita.al.repository" })
  @EntityScan(basePackages = { "com.mymita.al.domain" })
  static class TestConfig {
  }

  @Autowired
  transient MarriageRepository marriageRepository;

  @Test
  public void findByLastNamePerson1ContainingIgnoreCaseOrBirthNamePerson2ContainingIgnoreCaseAndYear() throws Exception {
    assertThat(marriageRepository.findByLastNamePerson1ContainingIgnoreCaseOrBirthNamePerson2ContainingIgnoreCaseAndYear("höh", "", "")
        .size(), is(1));
    assertThat(marriageRepository.findByLastNamePerson1ContainingIgnoreCaseOrBirthNamePerson2ContainingIgnoreCaseAndYear("", "höh", "")
        .size(), is(1));
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