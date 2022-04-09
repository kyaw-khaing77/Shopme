package com.shopme.common.entity.question;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.IdBasedEntity;
import com.shopme.common.entity.User;
import com.shopme.common.entity.product.Product;

@Entity
@Table(name = "questions")
public class Question extends IdBasedEntity {

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	@ManyToOne
	@JoinColumn(name = "asker_id")
	private Customer asker;
	
	@Column(name = "question")
	private String questionContent;
	
	private Date askTime;
	
	private String answer;
	
	@ManyToOne
	@JoinColumn(name="answerer_id")
	private User answerer;
	
	private Date answerTime;
	
	private boolean approved;
	
	private int votes;
	
	@Transient
	private boolean upvotedByCurrentCustomer;
	
	@Transient
	private boolean downvotedByCurrentCustomer;	

	public Question() {
	}
	
    public Question(Integer id) { 	
		this.id = id; 
	}

	@Transient
	public boolean isAnswered() {
		return this.answer != null && !answer.isEmpty();
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Customer getAsker() {
		return asker;
	}

	public void setAsker(Customer asker) {
		this.asker = asker;
	}

	

	public String getQuestionContent() {
		return questionContent;
	}

	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}

	public Date getAskTime() {
		return askTime;
	}

	public void setAskTime(Date askTime) {
		this.askTime = askTime;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public User getAnswerer() {
		return answerer;
	}

	public void setAnswerer(User answerer) {
		this.answerer = answerer;
	}

	public Date getAnswerTime() {
		return answerTime;
	}

	public void setAnswerTime(Date answerTime) {
		this.answerTime = answerTime;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	@Override
	public String toString() {
		return "Question [product=" + product + ", asker=" + asker + ", questionContent=" + questionContent
				+ ", askTime=" + askTime + ", answer=" + answer + ", answerer=" + answerer + ", answerTime="
				+ answerTime + ", approved=" + approved + ", votes=" + votes + "]";
	}

	public boolean isUpvotedByCurrentCustomer() {
		return upvotedByCurrentCustomer;
	}

	public void setUpvotedByCurrentCustomer(boolean upvotedByCurrentCustomer) {
		this.upvotedByCurrentCustomer = upvotedByCurrentCustomer;
	}

	public boolean isDownvotedByCurrentCustomer() {
		return downvotedByCurrentCustomer;
	}

	public void setDownvotedByCurrentCustomer(boolean downvotedByCurrentCustomer) {
		this.downvotedByCurrentCustomer = downvotedByCurrentCustomer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Question other = (Question) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
