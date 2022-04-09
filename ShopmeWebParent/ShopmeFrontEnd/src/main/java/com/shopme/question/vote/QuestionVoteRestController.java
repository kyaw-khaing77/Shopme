package com.shopme.question.vote;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.ControllerHelper;
import com.shopme.VoteResult;
import com.shopme.VoteType;
import com.shopme.common.entity.Customer;

@RestController
public class QuestionVoteRestController {

	@Autowired
	private QuestionVoteService service;
	
	@Autowired
	private ControllerHelper helper;
	
	
	@PostMapping("/vote_question/{id}/{type}")
	public VoteResult voteReview(@PathVariable(name="id") Integer questionId,@PathVariable(name="type") String type,HttpServletRequest request) {
		Customer customer = helper.getAuthenticatedCustomer(request);
		if(customer == null) {
			return VoteResult.fail("You must login to vote the question.");
		}
		
		VoteType voteType = VoteType.valueOf(type.toUpperCase());
		return service.doVote(questionId, customer, voteType);
	}
}
