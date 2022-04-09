package com.shopme.question.vote;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.question.QuestionVote;

public interface QuestionVoteRepository extends JpaRepository<QuestionVote, Integer> {

	@Query("SELECT q FROM QuestionVote q WHERE q.question.id = ?1 AND q.customer.id = ?2")
	public QuestionVote findByQuestionAndCustomer(Integer questionId, Integer customerId);

	@Query("SELECT q FROM QuestionVote q WHERE q.question.product.id = ?1 AND q.customer.id = ?2")
	public List<QuestionVote> findByProductAndCustomer(Integer productId, Integer customerId);
}
