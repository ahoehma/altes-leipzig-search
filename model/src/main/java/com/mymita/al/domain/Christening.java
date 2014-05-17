package com.mymita.al.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
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

  public Christening church(final String church) {
    this.church = church;
    return this;
  }

  public Christening familyCode(final String familyCode) {
    this.familyCode = familyCode;
    return this;
  }

  public Christening firstNameFather(final String firstName) {
    firstNameFather = firstName;
    return this;
  }

  public String getChurch() {
    return church;
  }

  public String getFamilyCode() {
    return familyCode;
  }

  public String getFirstNameFather() {
    return firstNameFather;
  }

  public Long getId() {
    return id;
  }

  public String getLastNameFather() {
    return lastNameFather;
  }

  public String getPersonCode1() {
    return personCode1;
  }

  public String getPersonCode2() {
    return personCode2;
  }

  public String getProfession() {
    return profession;
  }

  public String getReference() {
    return reference;
  }

  public String getTaufKind() {
    return taufKind;
  }

  public String getYear() {
    return year;
  }

  public Christening lastNameFather(final String lastName) {
    lastNameFather = lastName;
    return this;
  }

  public Christening personCode1(final String personCode1) {
    this.personCode1 = personCode1;
    return this;
  }

  public Christening personCode2(final String personCode2) {
    this.personCode2 = personCode2;
    return this;
  }

  public Christening profession(final String profession) {
    this.profession = profession;
    return this;
  }

  public Christening reference(final String reference) {
    this.reference = reference;
    return this;
  }

  public Christening taufKind(final String taufKind) {
    this.taufKind = taufKind;
    return this;
  }

  public Christening year(final String year) {
    this.year = year;
    return this;
  }

}
