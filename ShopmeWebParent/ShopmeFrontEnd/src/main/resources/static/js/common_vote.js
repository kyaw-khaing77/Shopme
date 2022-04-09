function vote(currentLink,type) {
	requestURL = currentLink.attr("href");

	$.ajax({
		type: "POST",
		url: requestURL,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(voteResult) {
		console.log(voteResult);

		if (voteResult.successful) {
			$("#modalDialog").on("hide.bs.modal", function(e) {
				updateVoteCountAndIcons(currentLink, voteResult,type);
			});
		}

		showModalDialog("Vote "+type, voteResult.message);

	}).fail(function() {
		showErrorDialog("Error voting "+type+".");
	});	
}

function updateVoteCountAndIcons(currentLink, voteResult,type) {
	typeId = currentLink.attr(type+"Id");
	voteUpLink = $("#linkVoteUp-" + typeId);
	voteDownLink = $("#linkVoteDown-" + typeId);

	$("#voteCount-" + typeId).text(voteResult.voteCount + " Votes");

	message = voteResult.message;

	if (message.includes("successfully voted up")) {
		highlightVoteUpIcon(currentLink, voteDownLink,type);
	} else if (message.includes("successfully voted down")) {
		highlightVoteDownIcon(currentLink, voteUpLink,type);
	} else if (message.includes("unvoted down")) {
		unhighlightVoteDownIcon(voteDownLink,type);
	} else if (message.includes("unvoted up")) {
		unhighlightVoteDownIcon(voteUpLink,type);
	}
}

function highlightVoteUpIcon(voteUpLink, voteDownLink,type) {
	voteUpLink.removeClass("far").addClass("fas");
	voteUpLink.attr("title", "Undo vote up this "+type);
	voteDownLink.removeClass("fas").addClass("far");
}

function highlightVoteDownIcon(voteDownLink, voteUpLink,type) {
	voteDownLink.removeClass("far").addClass("fas");
	voteDownLink.attr("title", "Undo vote down this "+type);
	voteUpLink.removeClass("fas").addClass("far");
}

function unhighlightVoteDownIcon(voteDownLink,type) {
	voteDownLink.attr("title", "Vote down this "+type);
	voteDownLink.removeClass("fas").addClass("far");	
}

function unhighlightVoteUpIcon(voteUpLink,type) {
	voteUpLink.attr("title", "Vote up this "+type);
	voteUpLink.removeClass("fas").addClass("far");	
} 