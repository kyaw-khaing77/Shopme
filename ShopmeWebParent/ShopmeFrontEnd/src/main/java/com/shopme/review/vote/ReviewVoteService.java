package com.shopme.review.vote;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.VoteResult;
import com.shopme.VoteType;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.ReviewVote;
import com.shopme.review.ReviewRepository;

@Service
@Transactional
public class ReviewVoteService {

	@Autowired
	private ReviewRepository reviewRepo;
	
	@Autowired
	private ReviewVoteRepository voteRepo;
	
	public VoteResult undoVote(ReviewVote vote,Integer reviewId,VoteType voteType) {
		voteRepo.delete(vote);
		reviewRepo.updateVoteCount(reviewId);
		Integer voteCount = reviewRepo.getVoteCount(reviewId);
		
		return VoteResult.success("You have unvoted "+ voteType + " that review.", voteCount);
	}
	
	public VoteResult doVote(Integer reviewId,Customer customer,VoteType voteType) {
		Review review = null;
		try {
			review = reviewRepo.findById(reviewId).get();
		} catch (NoSuchElementException e) {
			return VoteResult.fail("The review ID "+reviewId + " no longer exists.");
		}
		
		ReviewVote reviewVote = voteRepo.findByReviewAndCustomer(reviewId, customer.getId());
		
		if(reviewVote != null) {
			if(reviewVote.isUpvoted() && voteType.equals(VoteType.UP) || 
			   reviewVote.isDownvoted() && voteType.equals(VoteType.DOWN)) {
				return undoVote(reviewVote, reviewId, voteType);
			}else if(reviewVote.isUpvoted() && voteType.equals(VoteType.DOWN)) {
				reviewVote.voteDown();
			}else if(reviewVote.isDownvoted() && voteType.equals(VoteType.UP)) {
				reviewVote.voteUp();
			}
		}else {
		   reviewVote = new ReviewVote();
		   reviewVote.setCustomer(customer);
		   reviewVote.setReview(review);
		   
		   if(voteType.equals(VoteType.UP)) {
			   reviewVote.voteUp();
		   }else {
			   reviewVote.voteDown();
		   }
		   
		}
		
		voteRepo.save(reviewVote);
		reviewRepo.updateVoteCount(reviewId);
		Integer voteCount=reviewRepo.getVoteCount(reviewId);
		
		return VoteResult.success("You have successfully voted "+ voteType + " that review.", voteCount);		
		
	}
	
	public void makeReviewsVotedForProductByCustomer(List<Review> listReview,Integer productId,Integer customerId) {
		List<ReviewVote> listVotes = voteRepo.findByProductAndCustomer(productId, customerId);
		
		for(ReviewVote vote:listVotes) {
			Review votedReview = vote.getReview();
			
			if(listReview.contains(votedReview)) {
				int index = listReview.indexOf(votedReview);
				Review review = listReview.get(index);
				
				if(vote.isUpvoted()) {
					review.setUpvotedByCurrentCustomer(true);
				}else if(vote.isDownvoted()) {
					review.setDownvotedByCurrentCustomer(true);
				}
			}
		}
	}
}
