package com.shopme.admin.question;

import java.util.Date;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.product.ProductRepository;
import com.shopme.common.entity.User;
import com.shopme.common.entity.question.Question;
import com.shopme.common.exception.QuestionNotFoundException;

@Service
@Transactional
public class QuestionService {

    public static final int QUESTIONS_PER_PAGE = 5;
	
    @Autowired
	private QuestionRepository repo;
    
    @Autowired
	private ProductRepository productRepository;
	
	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, QUESTIONS_PER_PAGE, repo);
	}
	
	public Question questionById(Integer id) throws QuestionNotFoundException {
		try {
			  return repo.findById(id).get();
		}catch (NoSuchElementException e) {
            throw new QuestionNotFoundException("Could not find any question with ID " + id);
		}
		
	}
	
	public void delete(Integer id) throws QuestionNotFoundException {
		if (!repo.existsById(id)) {
			throw new QuestionNotFoundException("Could not find any question with ID " + id);
		}
		repo.deleteById(id);
	}
	
	public void approve(Integer id) throws QuestionNotFoundException {
		
		try {
			Question question = repo.findById(id).get();
			repo.updateApprovalStatus(question.getId(), true);
			productRepository.updateAnsweredQuestionCount(question.getProduct().getId());
		}catch (NoSuchElementException e) {
			throw new QuestionNotFoundException("Could not find any question with ID " + id);
		}
		
	}

	public void disapprove(Integer id) throws QuestionNotFoundException {
		try {
			Question question = repo.findById(id).get();
			repo.updateApprovalStatus(question.getId(), false);
			productRepository.updateAnsweredQuestionCount(question.getProduct().getId());
		}catch (NoSuchElementException e) {
			throw new QuestionNotFoundException("Could not find any question with ID " + id);
		}
	}
	
	public void savedQuestion(Question questionInForm,User answerer) throws QuestionNotFoundException {
		
		try {
			Question questionInDb = repo.findById(questionInForm.getId()).get();
			questionInDb.setQuestionContent(questionInForm.getQuestionContent());
			questionInDb.setAnswer(questionInForm.getAnswer());
			questionInDb.setApproved(questionInForm.isApproved());

			if (questionInDb.isAnswered()) {
				questionInDb.setAnswerTime(new Date());
				questionInDb.setAnswerer(answerer);
			}
			
			repo.save(questionInDb);
			productRepository.updateAnsweredQuestionCount(questionInDb.getProduct().getId());
			
		}catch (NoSuchElementException e) {
			throw new QuestionNotFoundException("Could not find any question with ID " + questionInForm.getId());
		}
	}
	

}
