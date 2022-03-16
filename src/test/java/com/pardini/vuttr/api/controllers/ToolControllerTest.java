package com.pardini.vuttr.api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;

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
	private ToolService toolService;

	@Autowired
	private JacksonTester<Tool> jsonTool;

	@Test
	public void shouldReturnATool_whenGetById() throws Exception {
		String mockId = "abc123";
		
		Tool mockTool = new Tool(mockId, "title", "link", "description",
				new ArrayList<>(Arrays.asList("mock1", "mock2")));
		
		String expected = jsonTool.write(mockTool).getJson();
		
		when(toolService.getById(mockId)).thenReturn(mockTool);

		mockMvc.perform(get("/tools/{id}", mockId))
			.andExpect(status().isOk())
			.andExpect(result-> assertEquals(expected, result.getResponse().getContentAsString()));

	}
	
	@Test
	public void shouldThrowResourceNotFoundException_whenToolDoesNotExist() throws Exception {
		when(toolService.getById("abc123")).thenThrow(ResourceNotFoundException.class);
		
		mockMvc.perform(get("/tools/{id}", "abc123"))
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
	}
}
