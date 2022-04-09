$(document).ready(function() {
	
	decimalSeparator = decimalPointType == 'COMMA'?',':'.';
	thousandSeparator = thousandPointType == 'COMMA'?',':'.';
	
	$(".linkMinus").on("click", function(e) {
		e.preventDefault();
		decreaseQuantity($(this));

	});

	$(".linkPlus").on("click", function(e) {
		e.preventDefault();
		increaseQuantity($(this));

	});

	$(".link-remove").on("click", function(e) {
		e.preventDefault();
		removeProduct($(this));
	});
});


function decreaseQuantity(link) {
	productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) - 1;
	if (newQuantity > 0) {
		quantityInput.val(newQuantity);
		updateQuantity(productId, newQuantity);
	} else {
		showWarningDialog("Minimum quantity is 1")
	}
}

function increaseQuantity(link) {
	productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) + 1;
	if (newQuantity <= 5) {
		quantityInput.val(newQuantity);
		updateQuantity(productId, newQuantity);

	} else {
		showWarningDialog("Maximum quantity is 5")
	}
}

function updateQuantity(productId, quantity) {
	url = contextPath + "cart/update/" + productId + "/" + quantity;
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}

	}).done(function(updatedSubtotal) {
		updateSubtotal(updatedSubtotal, productId);
		updatetotal();
	}).fail(function() {
		showErrorDialog("Shopping cart", "Error while updating product quantity");
	});
}

function updateSubtotal(updatedSubtotal, productId) {
	$("#subtotal" + productId).text(formatCurrency(updatedSubtotal));

}

function updatetotal() {
	total = 0.0;
	productCount = 0;
	$(".subtotal").each(function(index, element) {
		productCount++;
		total += parseFloat(clearCurrencyFormat(element.innerHTML));
	});
	
	if(productCount < 1){
		showEmptyShoppingCart();
	}else{
	    $("#total").text(formatCurrency(total));
	}	
	
}

function showEmptyShoppingCart(){
	$("#sectionTotal").hide();
	$("sectionEmptyCartMessage").removeClass("d-none");
}

function removeProduct(link) {
	url = link.attr("href");
	$.ajax({
		type: "DELETE",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}

	}).done(function(response) {
	   rowNumber = link.attr("rowNumber")
       removeProductHTML(rowNumber);
       updatetotal();
       updateCountNumbers();
	}).fail(function() {
		showErrorDialog("Shopping cart", "Error while deleting product.");
	});
}


function removeProductHTML(rowNumber){
	$("#row"+rowNumber).remove();
	$("#blankline"+rowNumber).remove();

}

function updateCountNumbers(){
	$(".divCount").each(function(index,element){
		element.innerHTML =""+(index + 1);
	})
}

function formatCurrency(amount){
	return $.number(amount,decimalDigits,decimalSeparator,thousandSeparator);
}

function clearCurrencyFormat(numberString){
	result = numberString.replaceAll(thousandSeparator,"");
	return result.replaceAll(decimalSeparator,".");
}
