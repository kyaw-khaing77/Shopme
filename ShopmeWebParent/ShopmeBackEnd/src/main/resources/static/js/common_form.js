 $(document).ready(function(){
       $("#buttonCanel").on("click",function(){
          window.location = moduleURL;
       });
       
       $("#fileImage").change(function(){
          if(!checkFileSize(this)){
	        return;
          }
          showImageThumbnail(this);
          
       });
  });

  function checkFileSize(fileInput){
	
	fileSize = fileInput.files[0].size;
          if(fileSize > MAX_FILE_SIZE){
          
	          fileInput.setCustomValidity("You must choose an image less than "+ MAX_FILE_SIZE +" bytes");
	          fileInput.reportValidity();
              return false;
          }else{
               fileInput.setCustomValidity("");
               return true;
          }
   }
  
  
  function showImageThumbnail(fileInput){
     var file = fileInput.files[0];
     var reader = new FileReader();
     reader.readAsDataURL(file);
     reader.onload = function(e){
          $("#thumbnail").attr("src",e.target.result)
     };
  }

function showModalModal(title, message) {
	$("#modalTitle").text(title);
	$("#modalBody").text(message);
	$("#modalDialog").modal();
}

function showErrorModal(message) {
	showModalModal("Error", message)
}

function showWarningModal(message) {
	showModalModal("Warning", message)
}