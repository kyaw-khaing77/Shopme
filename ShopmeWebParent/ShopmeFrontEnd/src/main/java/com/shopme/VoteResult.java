package com.shopme;

public class VoteResult {
	
	private boolean successful;
	private String message;
	private int voteCount;
	
	public static VoteResult fail(String messgae) {
		return new VoteResult(false,messgae,0);
	}
	
	public static VoteResult success(String messgae,int voteCount) {
		return new VoteResult(true,messgae,voteCount);
	}
	
	public VoteResult(boolean successful, String message, int voteCount) {
		this.successful = successful;
		this.message = message;
		this.voteCount = voteCount;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}
	
}
