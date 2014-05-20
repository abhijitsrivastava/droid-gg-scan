
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
			 $(document).ready(function () {
             	$("#googlelogin").click(function (event) {   
             		event.preventDefault();
    		    	window.open($(this).attr("href"), "popupWindow", "width=600,height=600,scrollbars=yes");
            	 });
        	 });
		</script>
		
	</head>
	
	<body>
		<div class="container">
		
			<div class="background_body">
			
				<div style=" clear: both; text-align: right;margin-bottom:30px;">
					<h2 class="legend" style="margin-bottom:0px;">Google Access Code for GlassScan</h2>
					<span style="color: black;">*indicates mandatory fields</span>
				</div>
				
				<div class="form-addEditLogo">
				
				
        		
        			<div align="center">
            				<a id="googlelogin" href="https://accounts.google.com/o/oauth2/auth?scope=https://mail.google.com%20https://www.google.com/m8/feeds/%20https://www.googleapis.com/auth/drive&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&client_id=523728010956-5ehm6m10hi1vqhpmm3771l9727v3ldko.apps.googleusercontent.com">
            			Click here to generate Google verification code</a>
        			</div>
        		
        		<form:form method="POST" commandName="registrationForm" action="registration.htm"  cssClass="form-horizontal"> 
  
  					<div class="form-group"></div>
  
        			<div class="form-group">
            			<label class="control-label col-xs-5"  style="font-weight: normal;">Paste Verification Code*</label>
            			<div class="col-xs-5">
                			<form:input path="code" type="text" id="code" cssClass = "form-control" autofocus="autofocus"/>
                			<form:errors path="code" cssClass="error" />
            			</div>
        			</div>
				
				
					<div class="form-group">
            			<div class="col-xs-offset-5 col-xs-8">
                			<button type="submit" class="btn btn-primary" value="Submit">Submit</button>
            			</div>
        			</div>
        		</form:form>
				</div>
			</div>
		</div>
		
	</body>
</html>
