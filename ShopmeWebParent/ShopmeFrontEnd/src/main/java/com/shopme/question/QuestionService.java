package com.shopme.question;

import java.util.Date;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.question.Question;
import com.shopme.common.exception.QuestionNotFoundException;
import com.shopme.common.exception.ReviewNotFoundException;

@Service
public class QuestionService {

	public static final int QUESTION_PER_PAGE = 5;
	
	@Autowired
	private QuestionRepository repo;
	
	public Page<Question> listByAskerByPage(Customer asker, String keyword, int pageNum, String sortField,
			String sortDir) {
		
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, QUESTION_PER_PAGE, sort);

		if (keyword != null) {
			return repo.findAll(asker.getId(), keyword, pageable);
		}

		return repo.findByAsker(asker.getId(), pageable);
	}
	
	public Question getByCustomerAndId(Customer asker, Integer questionId) throws QuestionNotFoundException{
		
		try {
			  return repo.findByAskerAndId(asker.getId(), questionId);
		}catch (NoSuchElementException e) {
			throw new QuestionNotFoundException("Could not find any question with ID " + questionId);
		}
		
	}
	
	public Page<Question> list3MostRecentQuestionByProduct(Product product){
	       Sort sort = Sort.by("votes").descending();
	       Pageable pageable = PageRequest.of(0, 3, sort);
	       return repo.findByProduct(product.getId(), pageable);
	}
	
	public Page<Question> listByProduct(Product product, int pageNum, String sortField, String sortDir) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending(); 
		Pageable pageable = PageRequest.of(pageNum - 1, QUESTION_PER_PAGE, sort);

		return repo.findByProduct(product.getId(), pageable);
	}
	
	public Question save(Question question) {
		question.setAskTime(new Date());
		Question savedQuestion = repo.save(question);
		
		return savedQuestion;
	}
		

}
