package com.mymita.al.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mymita.al.domain.Person;

@Repository
public interface PersonRepository extends PagingAndSortingRepository<Person, Long>, QuerydslPredicateExecutor<Person> {
}
