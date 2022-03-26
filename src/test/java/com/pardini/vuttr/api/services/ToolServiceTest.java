package com.pardini.vuttr.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.pardini.vuttr.api.exceptions.ResourceNotFoundException;
import com.pardini.vuttr.domain.model.Tool;
import com.pardini.vuttr.domain.repositories.ToolRepository;

@SpringBootTest
public class ToolServiceTest {

	@Autowired
	private ToolService toolService;

	@MockBean
	private ToolRepository mockRepository;

	private Tool tool1;
	private Tool tool2;
	private Tool tool3;
	private List<Tool> toolList;

	@BeforeEach
	public void setUp() {
		tool1 = new Tool("a", "a", "a", "a", new ArrayList<>(Arrays.asList("a1", "a2")));
		tool2 = new Tool("b", "b", "b", "b", new ArrayList<>(Arrays.asList("b1", "b2")));
		tool3 = new Tool("c", "c", "c", "c", new ArrayList<>(Arrays.asList("c1", "c2")));

		toolList = new ArrayList<Tool>();
		toolList.add(tool1);
		toolList.add(tool2);
	}

	@AfterEach
	public void tearDown() {
		tool1 = tool2 = tool3 = null;
		toolList = null;
	}

	@Test
	public void whenGetAll_thenReturnListOfAllTools() {
		when(mockRepository.findAll()).thenReturn(toolList);

		var returned = toolService.getAll();

		assertThat(returned).isSameAs(toolList);

		verify(mockRepository, times(1)).findAll();
	}

	@Test
	public void whenSearchById_shoulReturnATool() {
		when(mockRepository.findById("c")).thenReturn(Optional.of(tool3));

		var returned = toolService.getById("c");

		assertThat(returned).isSameAs(tool3);

		verify(mockRepository, times(1)).findById("c");

	}

	@Test
	public void whenSearchNonExistingId_shouldThrowResourceNotFoundException() {
		when(mockRepository.findById("d")).thenReturn(Optional.ofNullable(null));

		ResourceNotFoundException thrown = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			toolService.getById("d");
		}, "Resource not found exception was expected");

		assertTrue(thrown.getMessage().contains("not found"));

		verify(mockRepository, times(1)).findById("d");
	}
}
