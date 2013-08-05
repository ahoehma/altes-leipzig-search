package com.mymita.al.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Service;

import com.mymita.al.domain.Tag;

@Service
public interface TagRepository extends GraphRepository<Tag> {
}