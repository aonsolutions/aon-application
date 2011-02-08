function getTop(el) {
	var top = 0;
	while ( el ) {
		top += el.offsetTop ;
		top += el.clientTop ;
		el = el.offsetParent;
	} 
	return top;
}

function getLeft(el) {
	var top = 0;
	while ( el ) {
		top += el.offsetLeft ;
		top += el.clientLeft ;
		el = el.offsetParent;
	} 
	return top;
}

function isWhiteSpace ( ch ){
	return ' \f\n\r\t\v\u00A0'.indexOf( ch ) >= 0;
	
}

function trim( str ) {
}
function ltrim( str ) {
	while( str.length > 0  ){
		var ch = str.charAt(0);
		if ( isWhiteSpace ( ch ) ){
			str = str.substring( 1 );
		}
		else {
			break;
		}
	}
	return str;
}

function rtrim( str ) {
}
 
function getParent( element , name ){
	var parent = element.parentNode;
	while ( parent ){
		if ( parent.nodeName == name )
			break;
		else 
			parent = parent.parentNode;
	}		
	return parent;
}


function getChild( element , name ){
	var child = element.firstChild;
	while ( child ){
		if ( child.nodeName == name )
			break;
		else 
			child = child.nextSibling;
	}		
	return child;
}

function getParameter ( param, search ) {
	var str = !search ? location.search : search;
	var start = str.indexOf('?')+1;
	while ( start < str.length ) {
		var end = str.indexOf('=', start );
		var name = str.substring( start, end );
		end = str.indexOf ( '&', start = end + 1  );
		if ( end == -1 ) {
			end = str.length;
		}
		if ( name == param ) {
			return str.substring( start, end );
		}
		start = end + 1;
	}
	return null;
}

function getCookie( key ) {
	var cookies = document.cookie;
	cookie = cookies.split(";");
	for (i=0;i<cookie.length;i++) {
		nameValue = cookie[i].split("=");
		if ( nameValue[0] == key ) {
			return nameValue[1];
		}
	}
	return -1;
}

function setCookie( key, value) {
	document.cookie = key + "=" + escape(value);
}

function toUpperCase(obj) {
	obj.value = obj.value.toUpperCase();
}

function toLowerCase(obj) {
	obj.value = obj.value.toLowerCase();
}
