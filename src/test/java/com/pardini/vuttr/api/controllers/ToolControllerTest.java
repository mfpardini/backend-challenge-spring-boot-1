package com.pardini.vuttr.api.controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pardini.vuttr.api.exceptions.ResourceNotFoundException;
import com.pardini.vuttr.api.services.ToolService;
import com.pardini.vuttr.domain.dtos.ToolDto;
import com.pardini.vuttr.domain.model.Tool;

@AutoConfigureJsonTesters
@WebMvcTest(ToolController.class)
public class ToolControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ToolService mockToolService;
	
	private static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}

	@Test
	public void shouldReturnATool_whenGetById() throws Exception {
		String mockId = "abc123";
		
		Tool mockTool = new Tool(mockId, "title", "link", "description",
				new ArrayList<>(Arrays.asList("mock1", "mock2")));
		
		String expected = asJsonString(mockTool);
		
		when(mockToolService.getById(mockId)).thenReturn(mockTool);

		mockMvc.perform(get("/tools/{id}", mockId))
			.andExpect(status().isOk())
			.andExpect(result-> assertEquals(expected, result.getResponse().getContentAsString()));

		verify(mockToolService, times(1)).getById(mockId);
	}
	
	@Test
	public void shouldThrowResourceNotFoundException_whenSearchedToolDoesNotExist() throws Exception {
		when(mockToolService.getById("abc123")).thenThrow(ResourceNotFoundException.class);
		
		mockMvc.perform(get("/tools/{id}", "abc123"))
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
		
		verify(mockToolService, times(1)).getById("abc123");
	}
	
	@Test
	public void shouldReturnAListOfAllTools_whenDoNotHaveQueryParams() throws Exception {
		var tool1 = new Tool("a", "a", "a", "a", new ArrayList<>(Arrays.asList("a", "b")));
		var tool2 = new Tool("b", "b", "b", "b", new ArrayList<>(Arrays.asList("b", "c")));
		var tool3 = new Tool("c", "c", "c", "c", new ArrayList<>(Arrays.asList("c", "a")));
		
		List<Tool> toolsList = new ArrayList<>(Arrays.asList(tool1, tool2, tool3));
		
		when(mockToolService.getAll()).thenReturn(toolsList);
		
		mockMvc.perform(get("/tools"))
			.andExpect(status().isOk())
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), containsString(asJsonString(tool1))))
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), containsString(asJsonString(tool2))))
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), containsString(asJsonString(tool3))))
			;
		
		verify(mockToolService, times(1)).getAll();
	}
	
	@Test
	public void shouldReturnAListOfSpecificTools_whenFilterTag() throws Exception {
		var tool1 = new Tool("a", "a", "a", "a", new ArrayList<>(Arrays.asList("a", "b")));
		var tool2 = new Tool("b", "b", "b", "b", new ArrayList<>(Arrays.asList("b", "c")));
		var tool3 = new Tool("c", "c", "c", "c", new ArrayList<>(Arrays.asList("c", "a")));
		
		List<Tool> toolsList = new ArrayList<>(Arrays.asList(tool2, tool3));
		
		when(mockToolService.getByTag("c")).thenReturn(toolsList);
		
		mockMvc.perform(get("/tools").param("tag", "c"))
			.andExpect(status().isOk())
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), not(containsString(asJsonString(tool1)))))
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), containsString(asJsonString(tool2))))
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), containsString(asJsonString(tool3))))
			;
		
		verify(mockToolService, times(1)).getByTag("c");
	}
	
	@Test
	public void shouldReturnEmptyList_whenFilterTagNotExists() throws Exception {
		when(mockToolService.getByTag("d")).thenReturn(new ArrayList<Tool>());
		
		mockMvc.perform(get("/tools").param("tag", "d"))
			.andExpect(status().isOk())
			.andExpect(result -> assertEquals("[]", result.getResponse().getContentAsString()))
			;
		
		verify(mockToolService, times(1)).getByTag("d");
	}
	
	@Test
	public void shouldThrowResourceNotFoundException_whenUpdatedToolDoesNotExist() throws Exception {
		var toolDtoMock = new ToolDto();
		toolDtoMock.setTitle("title");
		toolDtoMock.setLink("link");
		toolDtoMock.setDescription("description more than 15");
		toolDtoMock.setTags(new ArrayList<>(Arrays.asList("a1", "a2")));
				
		when(mockToolService.update("a", toolDtoMock)).thenThrow(ResourceNotFoundException.class);
		
		mockMvc.perform(put("/tools/{id}", "a")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(toolDtoMock))
				.characterEncoding("utf-8")
			)
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
			;
		
		verify(mockToolService, times(1)).update("a", toolDtoMock);
	}
	
	@Test
	public void shouldUpdateAndReturnUpdatedTool_whenPutRequest() throws Exception {
		var toolDtoMock = new ToolDto();
		toolDtoMock.setTitle("title");
		toolDtoMock.setLink("link");
		toolDtoMock.setDescription("description more than 15");
		toolDtoMock.setTags(new ArrayList<>(Arrays.asList("a1", "a2")));
		
		var newToolMock = new Tool("a",	"title", "link", "description more than 15", new ArrayList<>(Arrays.asList("a1", "a2")));
				
		when(mockToolService.update("a", toolDtoMock)).thenReturn(newToolMock);
		
		mockMvc.perform(put("/tools/{id}", "a")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(toolDtoMock))
				.characterEncoding("utf-8")
			)
			.andExpect(status().isOk())
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), containsString(asJsonString(newToolMock))))
			;
		
		verify(mockToolService, times(1)).update("a", toolDtoMock);
	}
	
	@Test
	public void shouldCreateNewTool_whenPostRequest() throws Exception {
		var toolDtoMock = new ToolDto();
		toolDtoMock.setTitle("title");
		toolDtoMock.setLink("link");
		toolDtoMock.setDescription("description more than 15");
		toolDtoMock.setTags(new ArrayList<>(Arrays.asList("a1", "a2")));
		
		var newToolMock = new Tool("abc",	"title", "link", "description more than 15", new ArrayList<>(Arrays.asList("a1", "a2")));
				
		when(mockToolService.save(toolDtoMock)).thenReturn(newToolMock);
		
		mockMvc.perform(post("/tools")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(toolDtoMock))
				.characterEncoding("utf-8")
			)
			.andExpect(status().isCreated())
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), containsString(asJsonString(newToolMock))))
			;
		
		verify(mockToolService, times(1)).save(toolDtoMock);
	}
	
	// TODO: find a way to test when some attribute passed by post or put is not valid
	
	@Test
	public void shouldThrowResourceNotFoundException_whenDeletedToolDoesNotExist() throws Exception {
		doThrow(ResourceNotFoundException.class).when(mockToolService).delete("a");
		
		mockMvc.perform(delete("/tools/{id}", "a"))
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
			;
		
		verify(mockToolService, times(1)).delete("a");
	}
	
	@Test
	public void shouldReturnVoidWithStatusOk_whenDeleteIsSuccessful() throws Exception {
		doNothing().when(mockToolService).delete("a");
		
		mockMvc.perform(delete("/tools/{id}", "a"))
			.andExpect(status().isOk())
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), is("")))
			;
		
		verify(mockToolService, times(1)).delete("a");
	}
	
}
