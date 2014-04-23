package com.mymita.al.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.NamedIndexRepository;
import org.springframework.stereotype.Repository;

import com.mymita.al.domain.Marriage;

@Repository
public interface MarriageRepository extends GraphRepository<Marriage>, NamedIndexRepository<Marriage> {

}