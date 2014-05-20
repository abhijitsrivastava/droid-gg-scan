
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

			<div style="clear: both; text-align: right; margin-bottom: 30px;">
				<h2 class="legend" style="margin-bottom: 0px;">Select Google Contacts</h2>
			</div>
			
			<div class="form-addEditLogo">
				<div align="center"><b>Select at most 20 Google Contacts for GlassScan</b></div>
				
				<c:if test="${not empty noContactSelected}">
					<div class="control-group error" align="center">Please select at least one contact</div>
				</c:if>
				
				<c:if test="${not empty moreContactSelected}">
					<div class="control-group error" align="center">You can select at most 20 contacts</div>
				</c:if>
				<br/>
				<form:form commandName="selectContactForm"
					action="selectedEmailsForQRCode.htm" method="POST"
					id="logoStatusChangeForm">
					
					<!-- <thead class="header_table">
							<tr class="row">
								<th></th>
								<th >Email Id</th>
								<div>
									<th class="col-md-2 col-md-offset-9"></th>
								</div>
							</tr>
						</thead> -->
					<div class="row">
						<div class="col-md-3 col-md-offset-2" style="margin-top: 10px"><b>Email</b></div>
  						<div class="col-md-3 col-md-offset-4"><button type="submit" class="btn btn-primary" value="Submit">Generate QR Code</button></div>
					</div>	
					<br/>
					<table class="table table-hover table-striped">
						<tbody >
							<c:forEach var="contact" items="${sessionScope.contactsList}">
								<tr>
									<td><form:checkbox path="selectedEmailList"
											value="${contact}" cssClass="noClass" /> <input id="action"
										type="hidden" value="" name="action" /></td>
									<td>${contact}</td>
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
