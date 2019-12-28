package com.mymita.al.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PUBLIC)
@Getter
public class Christening {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String personCode1;
  private String personCode2;
  private String church;
  private String reference;
  private String familyCode;
  private String lastNameFather;
  private String firstNameFather;
  private String profession;
  private String year;
  private String taufKind;
}
