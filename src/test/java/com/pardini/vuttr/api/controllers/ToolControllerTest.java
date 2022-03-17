package com.pardini.vuttr.api.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.pardini.vuttr.api.exceptions.ResourceNotFoundException;
import com.pardini.vuttr.api.services.ToolService;
import com.pardini.vuttr.domain.model.Tool;

@AutoConfigureJsonTesters
@WebMvcTest(ToolController.class)
public class ToolControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ToolService mockToolService;

	@Autowired
	private JacksonTester<Tool> jsonTool;

	@Test
	public void shouldReturnATool_whenGetById() throws Exception {
		String mockId = "abc123";
		
		Tool mockTool = new Tool(mockId, "title", "link", "description",
				new ArrayList<>(Arrays.asList("mock1", "mock2")));
		
		String expected = jsonTool.write(mockTool).getJson();
		
		when(mockToolService.getById(mockId)).thenReturn(mockTool);

		mockMvc.perform(get("/tools/{id}", mockId))
			.andExpect(status().isOk())
			.andExpect(result-> assertEquals(expected, result.getResponse().getContentAsString()));

	}
	
	@Test
	public void shouldThrowResourceNotFoundException_whenToolDoesNotExist() throws Exception {
		when(mockToolService.getById("abc123")).thenThrow(ResourceNotFoundException.class);
		
		mockMvc.perform(get("/tools/{id}", "abc123"))
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
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
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), containsString(jsonTool.write(tool1).getJson())))
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), containsString(jsonTool.write(tool2).getJson())))
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), containsString(jsonTool.write(tool3).getJson())))
			;
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
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), not(containsString(jsonTool.write(tool1).getJson()))))
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), containsString(jsonTool.write(tool2).getJson())))
			.andExpect(result -> assertThat(result.getResponse().getContentAsString(), containsString(jsonTool.write(tool3).getJson())))
			;
	}
	
	@Test
	public void shouldReturnEmptyList_whenFilterTagNotExists() throws Exception {
		when(mockToolService.getByTag("d")).thenReturn(new ArrayList<Tool>());
		
		mockMvc.perform(get("/tools").param("tag", "d"))
			.andExpect(status().isOk())
			.andExpect(result -> assertEquals("[]", result.getResponse().getContentAsString()))
			;
	}
}
