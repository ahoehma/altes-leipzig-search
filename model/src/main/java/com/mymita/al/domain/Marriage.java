package com.mymita.al.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(uniqueConstraints = {@UniqueConstraint(name = "unique_family_code", columnNames = {"familyCode"})})
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PUBLIC)
@Getter
public class Marriage {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String familyCode;
  private String personCode1;
  private String personCode2;
  private String lastNamePerson1;
  private String firstNamePerson1;
  private String birthNamePerson2;
  private String firstNamePerson2;
  private String professionPerson1;
  private String professionPerson2;
  private String cityPerson1;
  private String cityPerson2;
  private String dateMarriage;
  private String church;
  private String reference;
  private String periodMarriage;
  private String year;

}
