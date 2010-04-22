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

var oldOnSubmit;

function startUpload() {
	try {
		var form = document.getElementById( "documentModalPanelForm" );
		if ( form ) {
			oldOnSubmit = form.onsubmit;
			form.onsubmit = null;
		}
		var fileItems = document.getElementById( "documentModalPanelForm:upload:fileItems");
		if ( fileItems ) {
			fileItems.style.height = "70px";
		} else {
			alert( "fileItems not found !!" );
		}
	} catch(e) {
		alert( e );
	}
}

function endUpload() {
	try {
		var form = document.getElementById( "documentModalPanelForm" );
		if ( form ) {
			form.onsubmit = oldOnSubmit;
		}
		var fileItems = document.getElementById( "documentModalPanelForm:upload:fileItems");
		if ( fileItems ) {
			fileItems.style.height = "0px";
		} else {
			alert( "fileItems not found !!" );
		}
	} catch(e) {
		alert( e );
	}
}