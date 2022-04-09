var questionContent;
var productIdForQuestion;

$(document).ready(function() {
	$(".linkVoteQuestion").on("click", function(e) {
		e.preventDefault();
		vote($(this),'question');
	 });

    $("#cannotAskQuestion").on("click", function(e) {
	    e.preventDefault();
		showModalDialog("Cannot Ask Question ", "You must login to ask a question.");
	 });


    $( "#questionForm" ).submit(function( e ) {
	  e.preventDefault();
      submitQuestionForm();
	});

     questionContent =  $("#questionContent");
     productIdForQuestion = $("#productIdForQuestion");
    
});

function submitQuestionForm(){
	questionContent =questionContent.val();
	productIdForQuestion = productIdForQuestion.val();
	requestBody = {questionContent: questionContent, product: {id:productIdForQuestion}};
	url = contextPath +"post_question";
	$.ajax({
		type:'POST',
		url : url,
		beforeSend:function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		},
		data: JSON.stringify(requestBody),
		contentType: 'application/json'
		
	}).done(function(response){
		showModalDialog("Post Question",response);
		$("#questionContent").val("");
	}).fail(function(){
		showErrorDialog("Uploaded Question Fail","Error while uploading a question");
	});
}
