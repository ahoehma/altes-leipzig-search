package com.mymita.al.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "unique_person_code", columnNames = { "personCode" }) })
@lombok.Builder
@lombok.Data
public class Person {

  public enum Gender {
    MALE, FEMALE
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String personCode;
  private String firstName;
  private String lastName;
  private String birthName;
  private Gender gender;
  private String yearOfBirth;
  private String yearOfDeath;
  private String yearsOfLife;
  private String description;
  private String reference;
}