package com.mymita.al.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Table(uniqueConstraints = { @UniqueConstraint(name = "unique_person_code", columnNames = { "personCode" }) })
@Entity
@Getter
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Builder
public class Person {

  public enum Gender {
    MALE, FEMALE
  }

  private @Id @GeneratedValue(strategy = GenerationType.AUTO) final Long id;
  private final String personCode;
  private final String firstName;
  private final String lastName;
  private final String birthName;
  private final Gender gender;
  private final String yearOfBirth;
  private final String yearOfDeath;
  private final String yearsOfLife;
  private final String description;
  private final String reference;
}