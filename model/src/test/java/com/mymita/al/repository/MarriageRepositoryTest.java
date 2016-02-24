package com.mymita.al.repository;

import static com.mymita.al.domain.QMarriage.marriage;
import static com.mysema.query.types.expr.BooleanExpression.anyOf;
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

@ContextConfiguration(locations = { "classpath:/META-INF/spring/context-test.xml" })
public class MarriageRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {

  @Autowired
  transient MarriageRepository marriageRepository;

  @Test
  public void findByLastNameOrBirthNameAndYearOfDeathNeg() throws Exception {
    assertThat(
        marriageRepository.findAll(anyOf(marriage.lastNamePerson1.containsIgnoreCase("höh"),
            marriage.birthNamePerson2.containsIgnoreCase("")).and(marriage.year.eq("1900"))), Matchers.<Marriage> iterableWithSize(0));
  }

  @Test
  public void findByLastNameOrBirthNameAndYearOfDeathPos() throws Exception {
    assertThat(
        marriageRepository.findAll(anyOf(marriage.lastNamePerson1.containsIgnoreCase("höh"),
            marriage.birthNamePerson2.containsIgnoreCase("")).and(marriage.year.eq("2010"))), Matchers.<Marriage> iterableWithSize(1));
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
