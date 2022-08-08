<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
     <script type="text/javascript">
     	function login(){
     		var form=document.forms['openid-form-login'].submit();
     		close();
     	}
     </script>
</head>
<body onload="javascript:login()" >
	<div style="display:none">
		<form id="openid-form-login" target="${name}" method="post" action="${act}"  
		onSubmit>

			<input name="disable-pwd-mgr-1" type="password" id="disable-pwd-mgr-1" style="display: none;" value="disable-pwd-mgr-1" />
			<input name="disable-pwd-mgr-2" type="password" id="disable-pwd-mgr-2" style="display: none;" value="disable-pwd-mgr-2" />
			<input name="disable-pwd-mgr-3" type="password" id="disable-pwd-mgr-3" style="display: none;" value="disable-pwd-mgr-3" />
								
			<input type="text" id="j_username" name="j_username" value="${username}"/>
								
			<input type="password" id="j_password" name="j_password" value="${password}" autocomplete="new-password" />
								
			<input id="login_btn" name="login_btn" type="submit" />

		</form>
	</div>
</body>
</html>
