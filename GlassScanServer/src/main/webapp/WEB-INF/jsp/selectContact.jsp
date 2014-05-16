
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
		int contactSelectedCount= 0;
		function changeEmailSelectedCount(id) {
			alert(id);
			var status = $('#selectAll').is(':checked');
			if(status) {
				$('.noClass').prop('checked', true);
			} else {
				$('.noClass').prop('checked', false);
			}
		}
		
		
		
		</script>
		
	</head>
	
	<body>
		<div class="container">
		
			<div class="background_body">
			
				<div style=" clear: both; text-align: right;margin-bottom:30px;">
					<h2 class="legend" style="margin-bottom:0px;">Select Google contacts</h2>
				</div>
				
				<div class="form-addEditLogo">
				
				
        		
        			<div align="center">Select Google Contacts for QR Code
        			</div>
        		
        		<form:form commandName="selectContactForm" action="selectedEmailsForQRCode.htm" method="POST" id="logoStatusChangeForm"> 
					 
					<table class="table table-hover table-striped">
						<thead class="header_table">
        					<tr>
        						<th></th>
            					<th>Email Id</th>
								<div class="form-group">
									<div class="col-xs-offset-9 col-xs-8">
										<button type="submit" class="btn btn-primary" value="Submit">Generate QR Code</button>
									</div>
								</div>
							</tr>
       					 </thead>
       					 <tbody>
       					 	<c:forEach var="contact" items="${sessionScope.contactsList}">
       					 		<tr>
       					 			<td>
       					 				 <form:checkbox path="selectedEmailList" value="${contact}" cssClass="noClass"/>
       					 				 <input id="action" type="hidden" value="" name="action"/>
       					 			</td>
									<td>
										${contact}
									</td>
								</tr>
       					 	</c:forEach>
       					 </tbody>
					</table>
					</form:form>
				</div>
			</div>
		</div>
		
	</body>
</html>
