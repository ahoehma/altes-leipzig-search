package com.mymita.al.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "unique_person_code", columnNames = { "personCode" }) })
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

  public Person birthName(final String birthName) {
    this.birthName = birthName;
    return this;
  }

  public Person description(final String description) {
    this.description = description;
    return this;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Person other = (Person) obj;
    if (personCode == null) {
      if (other.personCode != null) {
        return false;
      }
    } else if (!personCode.equals(other.personCode)) {
      return false;
    }
    return true;
  }

  public Person firstName(final String firstName) {
    this.firstName = firstName;
    return this;
  }

  public Person gender(final Gender gender) {
    this.gender = gender;
    return this;
  }

  public String getBirthName() {
    return birthName;
  }

  public String getDescription() {
    return description;
  }

  public String getFirstName() {
    return firstName;
  }

  public Gender getGender() {
    return gender;
  }

  public String getLastName() {
    return lastName;
  }

  public String getPersonCode() {
    return personCode;
  }

  public String getReference() {
    return reference;
  }

  public String getYearOfBirth() {
    return yearOfBirth;
  }

  public String getYearOfDeath() {
    return yearOfDeath;
  }

  public String getYearsOfLife() {
    return yearsOfLife;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (personCode == null ? 0 : personCode.hashCode());
    return result;
  }

  public Person lastName(final String lastName) {
    this.lastName = lastName;
    return this;
  }

  public Person personCode(final String code) {
    this.personCode = code;
    return this;
  }

  public Person reference(final String reference) {
    this.reference = reference;
    return this;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Person [id=").append(id).append(", personCode=").append(personCode).append(", firstName=").append(firstName)
        .append(", lastName=").append(lastName).append(", birthName=").append(birthName).append(", gender=").append(gender)
        .append(", yearOfBirth=").append(yearOfBirth).append(", yearOfDeath=").append(yearOfDeath).append(", yearsOfLife=")
        .append(yearsOfLife).append(", description=").append(description).append(", reference=").append(reference).append("]");
    return builder.toString();
  }

  public Person yearOfBirth(final String yearOfBirth) {
    this.yearOfBirth = yearOfBirth;
    return this;
  }

  public Person yearOfDeath(final String yearOfDeath) {
    this.yearOfDeath = yearOfDeath;
    return this;
  }

  public Person yearsOfLife(final String yearsOfLife) {
    this.yearsOfLife = yearsOfLife;
    return this;
  }
}