package com.mymita.al.domain;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
@TypeAlias("Person")
@Indexed
public class Person {

  public enum Gender {
    MALE, FEMALE
  }

  @GraphId
  private Long   id;
  @Indexed(unique = true)
  private String code;
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

  public Person code(final String code) {
    this.code = code;
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
    if (code == null) {
      if (other.code != null) {
        return false;
      }
    } else if (!code.equals(other.code)) {
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

  public String getCode() {
    return code;
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
    result = prime * result + (code == null ? 0 : code.hashCode());
    return result;
  }

  public Person lastName(final String lastName) {
    this.lastName = lastName;
    return this;
  }

  public Person reference(final String reference) {
    this.reference = reference;
    return this;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Person [id=").append(id).append(", code=").append(code).append(", firstName=").append(firstName).append(", lastName=")
        .append(lastName).append(", birthName=").append(birthName).append(", gender=").append(gender).append(", yearOfBirth=")
        .append(yearOfBirth).append(", yearOfDeath=").append(yearOfDeath).append(", yearsOfLife=").append(yearsOfLife)
        .append(", description=").append(description).append(", reference=").append(reference).append("]");
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