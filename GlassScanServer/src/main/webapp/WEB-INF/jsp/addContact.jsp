<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta content="width=device-width, initial-scale=1" name="viewport"/>
		
		<link rel="stylesheet" href="css/bootstrap.min.css" type="text/css"/>
		<link rel="stylesheet" href="css/generic.css" type="text/css"/>

		<script type="text/javascript" src='js/jquery-1.11.0.min.js'></script>
		<script type="text/javascript" src='js/bootstrap.min.js'></script>
		
		<script type="text/javascript">
			
			function submitAddContactForm() {
			
				var email = $("#contactEmail").text();
				
				var data = { email : email };
	
	        	 $.ajax({
	         		   url : "addContact.htm",
	         		   dataType: "text",
	         		   data : data,
	         		   type : "POST",
	         		   beforeSend:function(jqXHR, settings){
	        		 	  $(".modal-dialog").hide();
	         			  $("#submit").after('<img id="spinner1" src="./css/ajax-loader.gif" width="25" height="25" style="margin-left:10px;margin-bottom:-6px;"/>');         		   
	         		   },
	         		   success : function(response) {
	         			  $('#spinner1').remove();
	         			  $(".modal-dialog").show();
	         			  $("#mySmallModalLabel").html("Information");
	         			  $(".modal-body").html("Information updated successfully");
	         			  $('#myModal').modal({
	         				  keyboard: false
	         			  });
	         		   },
	         		   error : function(jqXHR, textStatus, errorThrown) {
	         			 	$('#spinner1').remove();
	         			 	$(".modal-dialog").show();
	         				$("#mySmallModalLabel").html("Error");
		         			$(".modal-body").html("Sorry, an error has occured,please try again");
		         			$('#myModal').modal({
		         				  keyboard: false
		         			});		         			
	         		   }
	         		  }); 
			}
			
		</script>
		
	</head>
	
	<body>
		<div class="container">
		
			<div class="background_body">
			
				<%@ include file="header.jsp" %>
						
				<div style=" clear: both; text-align: right;margin-bottom:30px;">
					<h2 class="legend" style="margin-bottom:0px;">Add Contact</h2>
					<span style="color: black;">*indicates mandatory fields</span>
				</div>
				
				<div class="form-addEditLogo">
				
				<form:form method="POST" commandName="addContactForm" action="addContact.htm"  cssClass="form-horizontal"> 
				
					<div class="form-group">
            			<label  class="control-label col-xs-5">Contact Email* </label>
            			<div class="col-xs-5">
                			<form:input cssClass="form-control" path="contactEmail" id="contactEmail" />
                			<form:errors path="contactEmail" cssClass="error" />
            			</div>
        			</div>
        	
        			<div class="form-group">
            			<div class="col-xs-offset-5 col-xs-8">
            				<!-- <a id="submit" data-target=".bs-example-modal-sm" data-toggle="modal" onclick="submitAddContactForm()" class="btn btn-primary" href="#">Submit</a>  -->
            				<button type="submit" class="btn btn-primary" value="Submit">Add Contact</button>
                			&nbsp;&nbsp;&nbsp;<a class="btn btn-primary" href="generateQRCode.htm">Generate QR Code</a>
            			</div>
        			</div>      			
				</form:form>
				
				<div align="center">
            			<label style="color: red">${requestScope.message}</label>
        		</div>
				
				</div>
				<%@ include file="footer.jsp" %>
			</div>
		</div>
		
		<div class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true" onclick="reloadPage();">
  				<div class="modal-dialog modal-sm">
   				 <div class="modal-content">
      				<div class="modal-header">
						<button class="close" aria-hidden="true" data-dismiss="modal" type="button" onclick="reloadPage();">×</button>
						<h4 id="mySmallModalLabel" class="modal-title"></h4>
					</div>
					<div class="modal-body"> ... </div>
    			</div>
  				</div>
		</div>
		
	</body>
</html>