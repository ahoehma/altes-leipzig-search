package com.mymita.al.domain;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import com.google.common.collect.Sets;

@NodeEntity
@TypeAlias("Person")
@Indexed
public class Person {

  public enum Gender {
    MALE, FEMALE
  }

  @GraphId
  private Long           id;

  @Indexed(unique = true)
  private String         code;

  private String         firstName;
  private String         lastName;
  private String         birthName;
  private Gender         gender;
  private Date           dateOfBirth;
  private Date           dateOfChristening;
  private Date           dateOfDeath;
  private Date           dateOfBuried;
  private String         description;
  private String         yearsOfLife;

  @RelatedTo(type = "HAS_TAG")
  @Fetch
  private final Set<Tag> tags = Sets.newHashSet();

  public Person birthName(final String birthName) {
    this.birthName = birthName;
    return this;
  }

  public Person code(final String code) {
    this.code = code;
    return this;
  }

  public Person dateOfBirth(final Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  public Person dateOfBuried(final Date dateOfBuried) {
    this.dateOfBuried = dateOfBuried;
    return this;
  }

  public Person dateOfChristening(final Date dateOfChristening) {
    this.dateOfChristening = dateOfChristening;
    return this;
  }

  public Person dateOfDeath(final Date dateOfDeath) {
    this.dateOfDeath = dateOfDeath;
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

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public Date getDateOfBuried() {
    return dateOfBuried;
  }

  public Date getDateOfChristening() {
    return dateOfChristening;
  }

  public Date getDateOfDeath() {
    return dateOfDeath;
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

  public Set<Tag> getTags() {
    return tags;
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

  @Override
  public String toString() {
    return "Person [code=" + code + ", firstName=" + firstName + ", lastName=" + lastName + ", gender=" + gender + ", dateOfBirth="
        + dateOfBirth + ", dateOfChristening=" + dateOfChristening + ", dateOfDeath=" + dateOfDeath + ", dateOfBuried=" + dateOfBuried
        + ", description=" + description + ", birthName=" + birthName + ", yearsOfLife=" + yearsOfLife + ", tags=" + tags + ", id=" + id
        + "]";
  }

  public Person withTag(final Collection<Tag> tags) {
    this.tags.addAll(tags);
    return this;
  }

  public Person yearsOfLife(final String yearsOfLife) {
    this.yearsOfLife = yearsOfLife;
    return this;
  }
}