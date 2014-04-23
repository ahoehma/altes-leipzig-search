package com.mymita.al.domain;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
@TypeAlias("Marriage")
public class Marriage {

  @GraphId
  private Long id;
  @Indexed(unique = true)
  private String familyCode;
  private String personCode1;
  private String personCode2;
  @Indexed
  private String lastNamePerson1;
  private String firstNamePerson1;
  @Indexed
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

  public Marriage birthNamePerson2(final String birthName) {
    birthNamePerson2 = birthName;
    return this;
  }

  public Marriage church(final String church) {
    this.church = church;
    return this;
  }

  public Marriage cityPerson1(final String city) {
    this.cityPerson1 = city;
    return this;
  }

  public Marriage cityPerson2(final String city) {
    this.cityPerson2 = city;
    return this;
  }

  public Marriage dateMarriage(final String date) {
    this.dateMarriage = date;
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
    final Marriage other = (Marriage) obj;
    if (familyCode == null) {
      if (other.familyCode != null) {
        return false;
      }
    } else if (!familyCode.equals(other.familyCode)) {
      return false;
    }
    return true;
  }

  public Marriage familyCode(final String code) {
    this.familyCode = code;
    return this;
  }

  public Marriage firstNamePerson1(final String firstName) {
    firstNamePerson1 = firstName;
    return this;
  }

  public Marriage firstNamePerson2(final String firstName) {
    firstNamePerson2 = firstName;
    return this;
  }

  public String getBirthNamePerson2() {
    return birthNamePerson2;
  }

  public String getChurch() {
    return church;
  }

  public String getCityPerson1() {
    return cityPerson1;
  }

  public String getCityPerson2() {
    return cityPerson2;
  }

  public String getDateMarriage() {
    return dateMarriage;
  }

  public String getFamilyCode() {
    return familyCode;
  }

  public String getFirstNamePerson1() {
    return firstNamePerson1;
  }

  public String getFirstNamePerson2() {
    return firstNamePerson2;
  }

  public Long getId() {
    return id;
  }

  public String getLastNamePerson1() {
    return lastNamePerson1;
  }

  public String getPeriodMarriage() {
    return periodMarriage;
  }

  public String getPersonCode1() {
    return personCode1;
  }

  public String getPersonCode2() {
    return personCode2;
  }

  public String getProfessionPerson1() {
    return professionPerson1;
  }

  public String getProfessionPerson2() {
    return professionPerson2;
  }

  public String getReference() {
    return reference;
  }

  public String getYear() {
    return year;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (familyCode == null ? 0 : familyCode.hashCode());
    return result;
  }

  public Marriage lastNamePerson1(final String lastName) {
    lastNamePerson1 = lastName;
    return this;
  }

  public Marriage periodMarriage(final String period) {
    this.periodMarriage = period;
    return this;
  }

  public Marriage personCode1(final String personCode1) {
    this.personCode1 = personCode1;
    return this;
  }

  public Marriage personCode2(final String personCode2) {
    this.personCode2 = personCode2;
    return this;
  }

  public Marriage professionPerson1(final String profession) {
    this.professionPerson1 = profession;
    return this;
  }

  public Marriage professionPerson2(final String profession) {
    this.professionPerson2 = profession;
    return this;
  }

  public Marriage reference(final String reference) {
    this.reference = reference;
    return this;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Marriage [id=").append(id).append(", familyCode=").append(familyCode).append(", personCode1=").append(personCode1)
    .append(", personCode2=").append(personCode2).append(", lastNamePerson1=").append(lastNamePerson1).append(", firstNamePerson1=")
    .append(firstNamePerson1).append(", birthNamePerson2=").append(birthNamePerson2).append(", firstNamePerson2=")
    .append(firstNamePerson2).append(", professionPerson1=").append(professionPerson1).append(", professionPerson2=")
    .append(professionPerson2).append(", cityPerson1=").append(cityPerson1).append(", cityPerson2=").append(cityPerson2)
    .append(", dateMarriage=").append(dateMarriage).append(", church=").append(church).append(", reference=").append(reference)
    .append(", periodMarriage=").append(periodMarriage).append(", year=").append(year).append("]");
    return builder.toString();
  }

  public Marriage year(final String year) {
    this.year = year;
    return this;
  }

}
