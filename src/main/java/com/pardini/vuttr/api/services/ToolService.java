package com.pardini.vuttr.api.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pardini.vuttr.api.exceptions.ResourceNotFoundException;
import com.pardini.vuttr.domain.dtos.ToolDto;
import com.pardini.vuttr.domain.model.Tool;
import com.pardini.vuttr.domain.repositories.ToolRepository;

@Service
public class ToolService {

	@Autowired
	private ToolRepository toolRepository;

	public List<Tool> getAll() {
		return this.toolRepository.findAll();
	}

	public Tool getById(String id) {
		return this.getRegisterById(id);
	}

	public List<Tool> getByTag(String tag) {
		return this.toolRepository.findByTags(tag);
	}

	public Tool update(String id, ToolDto objDto) {
		Tool tool = this.getRegisterById(id);
		BeanUtils.copyProperties(objDto, tool);
		return this.toolRepository.save(tool);
	}

	public Tool save(ToolDto objDto) {
		Tool obj = new Tool();
		BeanUtils.copyProperties(objDto, obj);
		return this.toolRepository.save(obj);
	}

	public void delete(String id) {
		Tool obj = this.getRegisterById(id);
		this.toolRepository.delete(obj);
	}

	private Tool getRegisterById(String id) {
		return this.toolRepository
			.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Tool does not exist"));
	}

}
