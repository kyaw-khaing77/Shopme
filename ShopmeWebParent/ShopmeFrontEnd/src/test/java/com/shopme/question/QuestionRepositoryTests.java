package com.shopme.question;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.question.Question;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class QuestionRepositoryTests {
	
	@Autowired 
	private QuestionRepository repo;

	@Test
	public void testFindByAskerNoKeyword() {
		Integer askerId = 51;
		Pageable pageable = PageRequest.of(0, 5);

		Page<Question> page = repo.findByAsker(askerId, pageable);
		long totalElements = page.getTotalElements();

		assertThat(totalElements).isGreaterThan(1);		
	}
	
	@Test
	public void testFindByAskerWithKeyword() {
		Integer askerId = 51;
		String keyword = "Pelican";
		Pageable pageable = PageRequest.of(0, 5);

		Page<Question> page = repo.findAll(askerId, keyword, pageable);
		long totalElements = page.getTotalElements();

		assertThat(totalElements).isGreaterThan(0);		
	}
	
	@Test
	public void testFindByAskerAndId() {
		Integer askerId = 51;
		Integer questionId = 7;

		Question question = repo.findByAskerAndId(askerId, questionId);
		assertThat(question).isNotNull();
	}

	@Test
	public void testFindByProduct() {
		Integer productId = 5;
		
		Pageable pageable = PageRequest.of(0, 5);
		
		Page<Question> page = repo.findByProduct(productId, pageable);
		long totalElements = page.getTotalElements();
		
		List<Question> questionLists = page.getContent();
		questionLists.forEach(System.out::println);

		assertThat(totalElements).isGreaterThan(0);	
		
	}
	
	@Test
	public void testUpdateVoteCount() {
		Integer questionId = 14;
		repo.updateVoteCount(questionId);
		long updateVoteCount = repo.getVoteCount(questionId);
		System.out.println(updateVoteCount);

	}
	

}
