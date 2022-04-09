$(document).ready(function() {
	$(".linkVoteReview").on("click", function(e) {
		e.preventDefault();
		vote($(this),'review');
	 });
});