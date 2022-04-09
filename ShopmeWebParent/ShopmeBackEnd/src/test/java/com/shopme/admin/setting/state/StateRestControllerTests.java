package com.shopme.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTests {

	@Autowired
	private StateRepository repo;
	
	@Autowired
	private CountryRepository countryRepo;
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objMapper;
	
	@Test
	@WithMockUser(username = "nam",password = "psw",roles = "ADMIN")
	public void testListByCountries() throws Exception {
		int countryId = 2;
		String url = "/states/list_by_country/"+countryId;
		
		MvcResult result=mockMvc.perform(get(url))
							       .andExpect(status().isOk())
							       .andDo(print())
							       .andReturn();
		String jsonResponse = result.getResponse().getContentAsString();
		State[] states = objMapper.readValue(jsonResponse, State[].class);
		
		assertThat(states).hasSizeLessThan(0);
		
	}
	
	@Test
	@WithMockUser(username = "nam",password = "psw",roles = "ADMIN")
	public void testCreateState() throws Exception {
		int countryId = 2;
		String url = "/states/save";
		Country country = countryRepo.findById(countryId).get();
		State state = new State("Pyay",country);
		
		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				                   .content(objMapper.writeValueAsString(state))
				                   .with(csrf()))
							       .andDo(print())
							       .andExpect(status().isOk())
							       .andReturn();
		String jsonResponse = result.getResponse().getContentAsString();
		Integer stateId = Integer.parseInt(jsonResponse);
		Optional<State> findById = repo.findById(stateId);
		
		assertThat(findById.isPresent());
		
	}
	
	@Test
	@WithMockUser(username = "nam",password = "psw",roles = "ADMIN")
	public void testUpdateState() throws Exception {
		String url = "/states/save";
		Integer stateId = 4;
		String stateName = "Magway";
		State state = repo.findById(stateId).get();
		state.setName(stateName);
		
		mockMvc.perform(post(url).contentType("application/json")
				                   .content(objMapper.writeValueAsString(state))
				                   .with(csrf()))
							       .andDo(print())
							       .andExpect(status().isOk())
							       .andExpect(content().string(String.valueOf(stateId)));
		Optional<State> findById = repo.findById(stateId);
		
		assertThat(findById.isPresent());
		
		State updatedState = findById.get();
		assertThat(updatedState.getName()).isEqualTo(stateName);
		
	}
	
	@Test
	@WithMockUser(username = "nam",password = "psw",roles = "ADMIN")
	public void testDeleteState() throws Exception {
		Integer stateId = 4;
		String url = "/states/delete/"+stateId;
		
		mockMvc.perform(get(url)).andExpect(status().isOk());
		
		Optional<State> findById = repo.findById(stateId);
		
		assertThat(findById).isNotPresent();
	
	}
	
	
}
