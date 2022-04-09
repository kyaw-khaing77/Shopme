package com.shopme.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class StateRepositoryTests {

	@Autowired
	private StateRepository repo;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	public void testCreateCountry() {
		Integer countryId = 2;
		Country country = entityManager.find(Country.class, countryId);
		State state = repo.save(new State("Mandalay",country));
		assertThat(state.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListStatesByCountry() {
		Integer countryId = 2;
		Country country = entityManager.find(Country.class, countryId);
		List<State> listStates = repo.findByCountryOrderByNameAsc(country);
		
		  listStates.forEach(System.out::println);
		 
		
		assertThat(listStates.size()).isGreaterThan(0);
	}
	
	@Test
	public void testUpdateState() {
		Integer stateId = 2;
		String stateName = "Pyay";
		
		State state = repo.findById(stateId).get();
		state.setName(stateName);
		
		State updatedState = repo.save(state);
		
		assertThat(updatedState.getName()).isEqualTo(stateName);
		
	}
	
	@Test
	public void testGetState() {
		Integer id = 2;
		State state = repo.findById(id).get();
		assertThat(state).isNotNull();
	}
	
	@Test
	public void testDeleteState() {
		Integer id = 2;
		repo.deleteById(id);
		assertThat(repo.findById(id)).isEmpty();
	}
	
	
}
