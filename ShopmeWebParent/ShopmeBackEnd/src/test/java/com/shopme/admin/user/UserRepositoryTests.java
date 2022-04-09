package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateUser() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userKK = new User("name@codejava.net","nam2020","Kyaw","Khaing");
		userKK.addRole(roleAdmin);
		
		User savedUser = repo.save(userKK);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateNewUserWithTwoRoles() {
		User userMoe = new User("moe@codejava.net","moe2020","Moe","Gyi");
		Role roleEditor = new Role(3);
		Role roleAssitant = new Role(5);
		
		userMoe.addRole(roleEditor);
        userMoe.addRole(roleAssitant);
		
		User savedUser = repo.save(userMoe);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void testGetUserById() {
		User userKK = repo.findById(1).get();
		System.out.println(userKK);
		assertThat(userKK).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User userKK = repo.findById(1).get();
        userKK.setEnabled(true);
        
        repo.save(userKK);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User userMoe = repo.findById(2).get();
		Role roleEditor = new Role(3);
		Role roleSalePerson = new Role(2);
		userMoe.getRoles().remove(roleEditor);
        userMoe.addRole(roleSalePerson); 
        repo.save(userMoe);
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "kyaw3@gmail.com";
		User user = repo.getUserByEmail(email);
		assertThat(user).isNotNull();
	}
	
	
	@Test
	public void testCountById() {
		
		Long id = repo.countById(1);
		assertThat(id).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testDisableUser() {
		Integer id = 1;
		repo.updateEnabledStatus(id, false);
	}
	
	@Test
	public void testEnableUser() {
		Integer id = 1;
		repo.updateEnabledStatus(id, true);
	}
	
	@Test
	public void testListFirstPage() {
		int pageNumber = 3;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user-> System.out.println(user));
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
		
	}
	
	@Test
	public void testSearchUser() {
		
		String keyword = "bruce";
		
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(keyword,pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user-> System.out.println(user));
		
		assertThat(listUsers.size()).isGreaterThan(0);
		
	}
	
	
}
