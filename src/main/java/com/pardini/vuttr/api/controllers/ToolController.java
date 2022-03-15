package com.pardini.vuttr.api.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pardini.vuttr.api.services.ToolService;
import com.pardini.vuttr.domain.dtos.ToolDto;
import com.pardini.vuttr.domain.model.Tool;

@RestController()
@RequestMapping("/tools")
public class ToolController {

	@Autowired
	private ToolService toolService;

	@GetMapping(value = "/{id}")
	public ResponseEntity<Tool> getById(@PathVariable String id) {
		return ResponseEntity.ok(this.toolService.getById(id));
	}

	@GetMapping
	public ResponseEntity<List<Tool>> getRegisters(@RequestParam(required = false) String tag) {
		if (tag == null) {
			List<Tool> tools = this.toolService.getAll();
			return ResponseEntity.ok(tools);
		}
		List<Tool> tools = this.toolService.getByTag(tag);
		return ResponseEntity.ok(tools);
	}

	@PutMapping(value = "{id}")
	public ResponseEntity<Tool> update(@PathVariable String id, @RequestBody @Valid ToolDto objDto) {
		Tool updatedTool = this.toolService.update(id, objDto);
		return ResponseEntity.ok(updatedTool);
	}

	@PostMapping
	public ResponseEntity<Tool> create(@RequestBody @Valid ToolDto objDto) {
		Tool newTool = this.toolService.save(objDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(newTool);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		this.toolService.delete(id);
		return ResponseEntity.ok().build();
	}
}
