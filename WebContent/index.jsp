<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script src="js/jquery-1.10.2.js" type="text/javascript"></script>
<script src="js/app-ajax.js" type="text/javascript"></script>

<script>
	$(document).ready(function() {
		$(".create").click(function() {
			$.ajax({
				url : 'URLConverter',
				data : {
					userName : "create####"
				},
				success : function(responseText) {
					$('#ajaxGetUserServletResponse').text(responseText);
				},
				error: function(responseText) { 
			        alert("Error is there ...."); 
			    }  
			});
		});
		
		$(".search").click(function() {
			$.ajax({
				url : 'URLConverter',
				data : {
					userName : "getUrl" + "####" + $('#searchV').val()
				},
				success : function(responseText) {
					$('#ajaxGetUserServletResponse').text(responseText);
					window.open(responseText);
					$('#searchV').val('');
				},
				error: function(responseText) { 
			        alert("Error is there ...."); 
			    }  
			});
		});
		
		$(".encode").click(function() {
			//window.location.replace("https://www.facebook.com");
			//alert(" encode ..");
			$.ajax({
				url : 'URLConverter',
				data : {
					userName : "insert" + "####" + $('#encodeV').val()
				},
				success : function(responseText) {
					$('#ajaxGetUserServletResponse').text(responseText);
					$('#encodeV').val('');
				},
				error: function(responseText) { 
			        alert("Error is there ...."); 
			    }  
			});
		});
		
		$(".delete").click(function() {
			$.ajax({
				url : 'URLConverter',
				data : {
					userName : "delete" + "####" + $('#deleteV').val()
				},
				success : function(responseText) {
					$('#ajaxGetUserServletResponse').text(responseText);
					$('#deleteV').val('');
				},
				error: function(responseText) { 
			        alert("Error is there ...."); 
			    }  
			});
		});
		
		$(".drop").click(function() {
			$.ajax({
				url : 'URLConverter',
				data : {
					userName : "drop" + "####"
				},
				success : function(responseText) {
					$('#ajaxGetUserServletResponse').text(responseText);
				},
				error: function(responseText) { 
			        alert("Error is there ...."); 
			    }  
			});
		});
	});
	
</script>
</head>
<body>
	<form>
		<input type="button" name="create" class="create" value="Create Table"/>
		<br>
		<br>
		Encode URL : <input type="text" id="encodeV" />
		<input type="button" name="encode" class="encode" value="Encode"/>
		<br>
		<br>
		Search : <input type="text" id="searchV" />
		<input type="button" name="search" class="search" value="Search"/>
		<br>
		<br>
		Delete Hash : <input type="text" id="deleteV" />
		<input type="button" name="delete" class="delete" value="Delete"/>
		<br>
		<br>
		<input type="button" name="drop" class="drop" value="Drop Table"/>
	</form>
	<br>
	<br>
	
	<strong>Status : </strong>
		<div id="ajaxGetUserServletResponse"></div>
</body>
</html>