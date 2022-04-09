
package com.shopme.question.vote;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ReviewVote;
import com.shopme.common.entity.question.Question;
import com.shopme.common.entity.question.QuestionVote;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class QuestionVoteRepositoryTests {
	
	@Autowired 
	private QuestionVoteRepository repo;
	
	@Test
	public void testSaveVote() {
		Integer customerId = 7;
		Integer questionId = 14;

		QuestionVote vote = new QuestionVote();
		vote.setCustomer(new Customer(customerId));
		vote.setQuestion(new Question(questionId));
		vote.voteUp();

		QuestionVote savedVote = repo.save(vote);
		assertThat(savedVote.getId()).isGreaterThan(0);		
	}
	
	@Test
	public void testFindByQuestionAndCustomer() {
		Integer customerId = 51;
		Integer questionId = 5;	

		QuestionVote vote = repo.findByQuestionAndCustomer(questionId, customerId);
		assertThat(vote).isNotNull();

		System.out.println(vote);
	}
	
	@Test
	public void testFindByProductAndCustomer() {
		Integer customerId = 51;
		Integer productId = 5;

		List<QuestionVote> listVotes = repo.findByProductAndCustomer(productId, customerId);
		assertThat(listVotes.size()).isGreaterThan(0);

		listVotes.forEach(System.out::println);
	}



}
