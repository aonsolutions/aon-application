A4J.AJAX.onExpired = function(loc,expiredMsg){
	// obtenemos el componente que implementa la ventana modal para mostrarlo
	if (! disableAjax ) {
		$('sessionTimeOutWindow').component.show();
	}
}

A4J.AJAX.onError = function(req,status,message) {
	if (! disableAjax ) {
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
	}
}

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

var disableAjax = false;

function disableRFAjax() {
	disableAjax = true;
}