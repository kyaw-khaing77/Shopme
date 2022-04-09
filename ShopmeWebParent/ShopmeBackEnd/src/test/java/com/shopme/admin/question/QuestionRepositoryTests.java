package com.shopme.admin.question;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.User;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.question.Question;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class QuestionRepositoryTests {

	@Autowired
	private QuestionRepository repo;
         	
	@Test
	public void testCreateQuestion() {
		
		Integer productId = 5;
		Product product = new Product(productId);

		Integer askerId = 16;
		Customer asker = new Customer(askerId);
		
		String questionContent = "How is this product?";
		
		Question question = new Question();
		question.setProduct(product);
		question.setAsker(asker);
		question.setQuestionContent(questionContent);
		question.setAskTime(new Date());
		
		Question savedQuestion = repo.save(question);
		assertThat(savedQuestion.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testAnswerQuestion() {
		
		User user = new User();
		user.setId(1);
		
		String answer = "It is so good";
		
		Question question = repo.findById(1).get();
		question.setAnswer(answer);
		question.setAnswerer(user);
		question.setAnswerTime(new Date());
		
		Question updatedQuestion = repo.save(question);
		assertThat(updatedQuestion.getAnswer()).isEqualTo(answer);
	}
	
	@Test
	public void testGetQuestions() {
		
		List<Question> questionList = (List<Question>) repo.findAll();
		for (Question question : questionList) {
			System.out.println(question);
		}
	}
	
	@Test
	public void testDeleteQuestionById() {
		Integer id = 6;
		repo.deleteById(id);

		Optional<Question> findById = repo.findById(id);

		assertThat(findById).isNotPresent();
	}
	
	@Test
	public void testApproveQuestion() {
		int questionId = 1;
		boolean isApproved = true;
		repo.updateApprovalStatus(questionId, isApproved);
	}
	
	@Test
	public void testListFirstPage() {
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<Question> page = repo.findAll(pageable);
		
		List<Question> listQuesitions = page.getContent();
		
		listQuesitions.forEach(question-> System.out.println(question));
		
		assertThat(listQuesitions.size()).isEqualTo(pageSize);
		
	}
	
	@Test
	public void testSearchQuestion() {
		
		String keyword = "Christopher";
		
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<Question> page = repo.findAll(keyword,pageable);
		
		List<Question> listQuestions = page.getContent();
		
		listQuestions.forEach(question-> System.out.println(question));
		
		assertThat(listQuestions.size()).isGreaterThan(0);
		
	}
}
