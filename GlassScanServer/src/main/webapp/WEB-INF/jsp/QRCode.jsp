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
		
	</head>
	
	<body>
		<div class="container">

			<div class="background_body">
			
				<div style=" clear: both; text-align: right;margin-bottom:30px;">
					<h2 class="legend" style="margin-bottom:0px;">GlassScan QR Code</h2>
				</div>
				
				<div class="form-addEditLogo">
				
				<form  action="" class="form-horizontal"> 
				
        			<div class="form-group">
            			<div class="col-xs-12">
                			<center><img src="${requestScope.QRCodeImageURL}" class="img-thumbnail img-responsive" align="middle" style="height:auto; width:auto"/></center>
            			</div>
        			</div>
   
				</form>
				</div>
				<%@ include file="footer.jsp" %>
			</div>
		</div>
	</body>
</html>