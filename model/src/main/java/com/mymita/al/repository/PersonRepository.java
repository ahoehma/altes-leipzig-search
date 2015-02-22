package com.mymita.al.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mymita.al.domain.Person;

@Transactional
@Repository
public interface PersonRepository extends CrudRepository<Person, Long>, QueryDslPredicateExecutor<Person> {
}