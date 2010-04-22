var domain = 'true';
var user_entry = false;

function loadInnerWindow(){
	try {
		document.forms[0].j_username_view.focus();
		str = window.top.location.search.substring(1);
		if (str != '' ) {
			var start = 0;
			var end = str.indexOf('=', start );
			var name = str.substring( start, end );
			end = str.indexOf ( '&', start = end + 1  );
			if ( end == -1 ) {
				end = str.length;
			}
			if ( "usuario" == name ) {
				document.forms[0].j_username_view.value = str.substring( start, end );
				user_entry = true;
				document.forms[0].j_password.focus();
			}
		}
	} catch ( e ) { }
}

function concatHost() {
	if ( domain == 'true' ) {
		if (document.forms[0].j_username.value.indexOf("@") < 0) {
			var context = location.pathname.substring( 1, location.pathname.length );
			context = "/" + context.substring( 0, context.indexOf("/") );
			document.forms[0].j_username.value = document.forms[0].j_username_view.value + '@' + location.hostname + context;
		}
	}
}

function validateId(w) {
	x1 = /^[a-z\d\.]{6,16}$/i;// only alphanumerics, and length 6-16
	x2 = /[a-z]/i;			// a letter present
	return x1.test(w) && x2.test(w);
}

function validatePassword(w) {
	x1 = /^[a-z\d\-\_\$\%\(\)\?\¿]{8,16}$/i;// only alphanumerics and special characters, and length 8-16
	x2 = /[a-z]/i;			// a letter present
	x3 = /\d/;				// a digit present
	return x1.test(w) && x2.test(w) && x3.test(w);
}
