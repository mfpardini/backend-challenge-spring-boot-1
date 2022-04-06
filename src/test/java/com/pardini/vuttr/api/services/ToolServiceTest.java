package com.pardini.vuttr.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.pardini.vuttr.api.exceptions.ResourceNotFoundException;
import com.pardini.vuttr.domain.dtos.ToolDto;
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
	private ToolDto toolDto;

	@BeforeEach
	public void setUp() {
		tool1 = new Tool("id1", "title1", "link1", "description1", new ArrayList<>(Arrays.asList("tag1", "tag2")));
		tool2 = new Tool("id2", "title2", "link2", "description2", new ArrayList<>(Arrays.asList("tag2", "tag3")));
		tool3 = new Tool("id3", "title3", "link3", "description3", new ArrayList<>(Arrays.asList("tag3", "tag1")));

		toolList = new ArrayList<Tool>();
		toolList.add(tool1);
		toolList.add(tool2);
		
		toolDto = new ToolDto();
		toolDto.setDescription("description4");
		toolDto.setLink("link4");
		toolDto.setTitle("title4");
		toolDto.setTags(new ArrayList<String>(Arrays.asList("tag4", "tag1")));
	}

	@AfterEach
	public void tearDown() {
		tool1 = tool2 = tool3 = null;
		toolList = null;
		toolDto = null;
	}

	/**
	 * Perform assertions when trying to get resource that doesn't exists.
	 */
	private void testWhenShouldThrowResourceNotFoundException() {
		when(mockRepository.findById("d")).thenReturn(Optional.ofNullable(null));

		ResourceNotFoundException thrown = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			toolService.getById("d");
		}, "Resource not found exception was expected");

		assertTrue(thrown.getMessage().contains("not found"));

		verify(mockRepository, times(1)).findById("d");
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
		when(mockRepository.findById("id3")).thenReturn(Optional.of(tool3));

		var returned = toolService.getById("id3");

		assertThat(returned).isSameAs(tool3);

		verify(mockRepository, times(1)).findById("id3");

	}

	@Test
	public void whenSearchNonExistingId_shouldThrowResourceNotFoundException() {
		testWhenShouldThrowResourceNotFoundException();
	}

	@Test
	public void whenSearchByTag_shouldReturnAListOfMatchedTools() {
		var expected = new ArrayList<Tool>(Arrays.asList(tool2, tool3));
		when(mockRepository.findByTags("tag3")).thenReturn(expected);

		var returned = toolService.getByTag("tag3");

		assertThat(returned).isSameAs(expected);

		verify(mockRepository, times(1)).findByTags("tag3");
	}

	@Test
	public void whenUpdate_shouldReturnUpdatedTool() {
		

		when(mockRepository.findById("id3")).thenReturn(Optional.of(tool3));

		Tool expected = new Tool("id3", "title4", "link4", "description4",
				new ArrayList<String>(Arrays.asList("tag4", "tag1")));

		when(mockRepository.save(Mockito.any(Tool.class))).thenReturn(expected);

		var returned = toolService.update("id3", toolDto);

		// Tool equals compare only by id
		assertThat(returned).isEqualTo(expected);

		assertAll("Should have the same properties", 
			() -> assertEquals(expected.getTitle(), returned.getTitle()),
			() -> assertEquals(expected.getLink(), returned.getLink()),
			() -> assertEquals(expected.getDescription(), returned.getDescription()),
			() -> assertEquals(expected.getTags(), returned.getTags())
		);

		verify(mockRepository, times(1)).findById("id3");
		verify(mockRepository, times(1)).save(Mockito.any(Tool.class));
	}
	
	@Test
	public void whenUpdateNonExistingTool_shouldThrowResourceNotFoundException() {
		testWhenShouldThrowResourceNotFoundException();
	}
	
	@Test
	public void whenSave_shouldReturnTheNewToolObject() {
		
		Tool expected = tool1;
		
		when(mockRepository.save(Mockito.any(Tool.class))).thenReturn(expected);
		
		var returned = toolService.save(toolDto);
		
		// Tool equals compare only by id
		assertThat(returned).isEqualTo(expected);

		assertAll("Should have the same properties", 
			() -> assertEquals(expected.getTitle(), returned.getTitle()),
			() -> assertEquals(expected.getLink(), returned.getLink()),
			() -> assertEquals(expected.getDescription(), returned.getDescription()),
			() -> assertEquals(expected.getTags(), returned.getTags())
		);

		verify(mockRepository, times(1)).save(Mockito.any(Tool.class));
	}
	
	@Test
	public void whenDeleteNonExistingTool_shouldThrowResourceNotFoundException() {
		testWhenShouldThrowResourceNotFoundException();
	}
	
	@Test
	public void whenDelete_shouldCallDeleteRepoMethod() {
		when(mockRepository.findById("id1")).thenReturn(Optional.of(tool1));
		
		doNothing().when(mockRepository).delete(tool1);
		
		toolService.delete("id1");
		
		verify(mockRepository, times(1)).delete(tool1);
	}

}
