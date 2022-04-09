package com.shopme.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.question.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
	
	@Query("SELECT q FROM Question q WHERE q.asker.id = ?1")
	public Page<Question> findByAsker(Integer askerId, Pageable pageable);

	@Query("SELECT q FROM Question q WHERE q.asker.id = ?1 AND "
			+ "(q.questionContent LIKE %?2% OR "
			+ "q.answer LIKE %?2% OR q.product.name LIKE %?2%)" )
	public Page<Question> findAll(Integer askerId,String keyword, Pageable pageable);
	
	@Query("SELECT q FROM Question q WHERE q.asker.id = ?1 AND q.id = ?2")
	public Question findByAskerAndId(Integer askerId, Integer questionId);
	
	@Query("SELECT q FROM Question q WHERE q.product.id = ?1 AND q.approved = true")
	public Page<Question> findByProduct(Integer productId, Pageable pageable);
	
	@Query("UPDATE Question q SET q.votes = COALESCE((SELECT SUM(v.votes) FROM QuestionVote v"
			+ " WHERE v.question.id=?1), 0) WHERE q.id = ?1")
	@Modifying
	public void updateVoteCount(Integer questionId);
	
	 @Query("SELECT q.votes FROM Question q WHERE q.id = ?1")
	 public Integer getVoteCount(Integer questionId);
}
