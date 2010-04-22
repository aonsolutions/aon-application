function selectRow( element ) {
	changeRowStyle( element, "aon-table-row-hover" );
}

function unselectRow( element ) {
	changeRowStyle( element, "" );
}

function changeRowStyle( element, className ) {
	var parent = element;
	while ( parent ){
		if ( parent.tagName == "TR" ) {			
			actual_style=parent.className;
			actual_style_left=actual_style.lastIndexOf("aon-table-row-hover");
			if(actual_style_left>=0){
				left_style=actual_style.substr(0,actual_style_left-1);
			}
			else{
				left_style=parent.className;
			}			
			parent.className=left_style + " " + className;
			break;
		}
		parent = parent.parentNode;
	}
}

rowEnabled = true;

function enableRowSubmit( prop) {
	rowEnabled = prop;
}

function sendRow( e ){
	if ( rowEnabled ) {
		element = e.firstChild;
		link = getTblLink( element );
		if ( link ) {
			if ( !link.disabled ) {
				link.onclick();
			}
		}
	}
}

function getTblLink( e ){
	while ( e ){
		if ( e.tagName == "A" && e.className=="aon-table-row-selected") {
			return e;
		} else {
			child = e.firstChild;
			if (child) {
				r = getTblLink( child );
				if (r) {
					return r
				}
			}
			e = e.nextSibling;
		}
	}
	return null;
}

function disableTblLink( element, prop ){
	par = element.parentNode;
	while(par.tagName != "TR"){
		par = par.parentNode;
	}
	link = getTblLink( par );
	link.disabled = prop;
}