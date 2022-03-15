package com.pardini.vuttr.domain.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pardini.vuttr.domain.model.Tool;

public interface ToolRepository extends MongoRepository<Tool, String>{

	public List<Tool> findByTags(String tag);
}
