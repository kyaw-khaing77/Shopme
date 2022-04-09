var buttonLoad4States;
var dropDownCountry4States;
var dropDownStates;
var buttonAddStates;
var buttonUpdateStates;
var buttonDeleteStates;
var labelStateName;
var fieldStateName;

$(document).ready(function(){
	buttonLoad4States = $("#buttonLoadCountriesForStates");
	dropDownCountry4States   = $("#dropDownCountriesForStates");
	dropDownStates   = $("#dropDownStates");
	buttonAddStates   = $("#buttonAddState");
	buttonUpdateStates   = $("#buttonUpdateState");
	buttonDeleteStates   = $("#buttonDeleteState");
	labelStateName   = $("#labelStateName");
	fieldStateName   = $("#fieldStateName");
	
	buttonLoad4States.click(function(){
		loadCountries4States();
	});
	
	dropDownCountry4States.on("change",function(){
		loadStates4Country();
	});
	
	dropDownStates.on("change",function(){
		changeFormStateToSelectedState();
	});
	
	buttonAddStates.click(function(){
		if(buttonAddStates.val() == "Add"){
			addState();
		}else{
			changeFormStateToNew();
		}
		
	});
	
	buttonUpdateStates.click(function(){
		updateState();
	});
	
	buttonDeleteStates.click(function(){
		deleteState();
	})
	
});

function deleteState(){
	stateId = dropDownStates.val();
	
	 url = contextPath +"states/delete/"+stateId;
     $.ajax({
		type : 'DELETE',
		url  : url ,
		beforeSend : function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		}
     }).done(function(){
	    $("#dropDownStates option[value='"+stateId+"']").remove();
        changeFormStateToNew();
		showToastMessage("The state have been deleted");
	}).fail(function(){
		showToastMessage("ERROR:Could not loaded");		
	})
}

function updateState(){
	
	if(!validateFormState()) return;
	
    url = contextPath +"states/save";
	stateName = fieldStateName.val();
	stateId   =  dropDownStates.val();
	
	selectedCountry = $("#dropDownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	countryName = selectedCountry.text();
	
	jsonData = {id:stateId,name:stateName,country:{id:countryId,name:countryName}}
	
	$.ajax({
		type : 'POST',
		url  : url ,
		beforeSend : function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		},
		data : JSON.stringify(jsonData),
		contentType : 'application/json'
	}).done(function(stateId){
		$("#dropDownStates option:selected").text(stateName);
		showToastMessage("The state has been updated");
	}).fail(function(){
	    showToastMessage("ERROR:Sever encountered error");		
	})
}

function addState(){
	
	if(!validateFormState()) return;
	
	url = contextPath +"states/save";
	stateName = fieldStateName.val();
	
	selectedCountry = $("#dropDownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	countryName = selectedCountry.text();
	
	jsonData = {name:stateName,country:{id:countryId,name:countryName}}
	
	$.ajax({
		type : 'POST',
		url  : url ,
		beforeSend : function(xhr){
			xhr.setRequestHeader(csrfHeaderName,csrfValue);
		},
		data : JSON.stringify(jsonData),
		contentType : 'application/json'
	}).done(function(stateId){
		selectNewlyAddedState(stateId,stateName);
		showToastMessage("The new state has been added");
	}).fail(function(){
	    showToastMessage("ERROR:Sever encountered error");		
	})
	
}

function selectNewlyAddedState(stateId,stateName){
	$("<option>").val(stateId).text(stateName).appendTo(dropDownStates);
	
	$("#dropDownStates option[value='"+stateId+"']").prop("selected",true);
	
	fieldStateName.val("").focus();

}


function changeFormStateToSelectedState(){
	selectedState = $("#dropDownStates option:selected");
	stateName = selectedState.text();
	
	fieldStateName.val(stateName);
	labelStateName.text("Selected State/Province:");
	
    buttonAddStates.val("New");
	buttonUpdateStates.prop("disabled",false);
	buttonDeleteStates.prop("disabled",false);
	
}

function loadStates4Country(){
	selectedCountry = $("#dropDownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	url = contextPath +"states/list_by_country/" + countryId;
	
	$.get(url,function(responseJson){
		dropDownStates.empty();
		$.each(responseJson,function(index,state){
			$("<option>").val(state.id).text(state.name).appendTo(dropDownStates);
		})
	}).done(function(){
		changeFormStateToNew();
		showToastMessage("All states have been loaded for country "+selectedCountry.text());		
	}).fail(function(){
		showToastMessage("ERROR:Could not loaded");		
	})
	
};


function loadCountries4States(){
	url = contextPath +"countries/list";
	$.get(url,function(responseJson){
		dropDownCountry4States.empty();
		$.each(responseJson,function(index,country){
			optionValue = country.id;
			$("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry4States);
		})
	}).done(function(){
		buttonLoad4States.val("Refresh Country List");
		showToastMessage("All countries have been loaded");
	}).fail(function(){
		showToastMessage("ERROR:Could not loaded");		
	})
}

function showToastMessage(message){
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}

function changeFormStateToNew(){
	buttonAddStates.val("Add");
	labelStateName.text("State/Province Name:");
	
	buttonUpdateStates.prop("disabled",true);
	buttonDeleteStates.prop("disabled",true);
	
	fieldStateName.val("").focus();

}

function validateFormState(){
    formCountry = document.getElementById("formState");
	if(!formState.checkValidity()){
		formState.reportValidity();
		return false;
	}
	
	return true;
}

