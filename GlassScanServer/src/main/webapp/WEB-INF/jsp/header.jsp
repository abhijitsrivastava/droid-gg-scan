<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<div style=" clear: both; text-align: right;">
	<span style="color: #8AC43C; font-weight: bold;">Welcome:</span> 
	<span style="font-weight: bold; color: black;">${sessionScope.user.email}</span> 
</div>

<div style="display: inline;">
	<span style="font-weight: bold; float:right"><a href="signOut.htm">Sign out</a></span>
</div>