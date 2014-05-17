package com.mymita.al.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mymita.al.domain.Marriage;

@Repository
public interface MarriageRepository extends CrudRepository<Marriage, Long> {

  List<Marriage> findByLastNamePerson1ContainingIgnoreCaseOrBirthNamePerson2ContainingIgnoreCaseAndYear(String lastNamePerson1,
      String birthNamePerson2, String year);

  List<Marriage> findByLastNamePerson1ContainingOrBirthNamePerson2ContainingAllIgnoreCase(String lastNamePerson1, String birthNamePerson2);

  List<Marriage> findByYear(String year);
}