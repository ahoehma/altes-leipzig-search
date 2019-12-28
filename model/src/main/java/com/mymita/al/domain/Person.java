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

@Table(uniqueConstraints = {@UniqueConstraint(name = "unique_person_code", columnNames = {"personCode"})})
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PUBLIC)
@Getter
public class Person {

  public enum Gender {
    MALE, FEMALE
  }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
  private String link;
  private String image;
}
