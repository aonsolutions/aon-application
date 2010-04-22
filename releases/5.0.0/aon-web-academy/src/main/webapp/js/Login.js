function init(){
	try {
	login.j_username.focus();
	}
	catch ( e ) { }
	var search = parent.location.search;
	if ( search.indexOf('error') >= 0 ){
		var error = document.getElementById('error');
		error.style.display ='block';
	}
}
window.onload=init;