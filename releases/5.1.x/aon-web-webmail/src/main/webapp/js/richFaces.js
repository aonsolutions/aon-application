A4J.AJAX.onExpired = function(loc,expiredMsg){
	try {
		alert( loc + " " + expireMsg );
	} catch(e) {
		alert( e );
	}	
};

A4J.AJAX.onError = function(req,status,message) {
	try {
		var status_error = document.getElementById("status_error");
		if ( status_error ) {
			status_error.style.display = "block";
		} else {
			alert( "Connection Error: " + message );
		}
	} catch(e) {
		alert( e );
	}	
};

function resetStatusError() {
	try {
		var status_error = document.getElementById("status_error");
		if ( status_error ) {
			status_error.style.display = "none";
		}
	} catch(e) {
		alert( e );
	}
}