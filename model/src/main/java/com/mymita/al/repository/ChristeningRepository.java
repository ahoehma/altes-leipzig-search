package com.mymita.al.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mymita.al.domain.Christening;

@Repository
public interface ChristeningRepository extends CrudRepository<Christening, Long> {

  List<Christening> findByLastNameFatherContainingIgnoreCase(String lastName);

  List<Christening> findByLastNameFatherContainingIgnoreCaseAndYear(String lastName, String year);

  List<Christening> findByYear(String year);

}