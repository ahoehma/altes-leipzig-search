package com.mymita.al.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mymita.al.domain.Marriage;

@Repository
public interface MarriageRepository extends CrudRepository<Marriage, Long>, QueryDslPredicateExecutor<Marriage> {
}