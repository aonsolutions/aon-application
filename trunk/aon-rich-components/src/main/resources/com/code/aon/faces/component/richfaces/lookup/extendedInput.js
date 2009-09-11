var UP = 38;
var DOWN = 40;

function focusInFirstInput( formId ) {
	try {
		if (formId!=undefined) {
			mainForm = document.getElementById(formId);
		} else {
			mainForm = document.forms[0];
		}
		for (i=0;i<mainForm.length;i++) {
			el = mainForm[i];
			if(!el.isDisabled && !el.readOnly) {
				if (el.type != undefined) {
					switch (el.type.toLowerCase()) {
						case "text":
						case "textarea":
						case "checkbox":
						case "radio":
						case "file":
						case "password":
						case "select-one":
						case "select-multiple":
							el.focus(); 
							return;
					}
				}
			}
 		}
	} catch(ex) {
	}	  		
}

var oldRow;
var currentTableId;

function aonSelectFirstRow( tableId ) {
	var t = document.getElementsByTagName("table");
	for (i=0;i<t.length;i++) {
		if (t[i].id.endsWith(tableId) ) {
			table = t[i];
			currentTableId = t[i].id; 
			break; 
		}
	}
	if (table) {
		if ( table.rows.length > 0 ) {
			row = table.rows[1];
			aon_focusOnRow( row );
		}
	} else {
		currentTableId = null;
		oldRow = null;
	}
}
function aonChangeRowSelection(rowIndex) {
	try {
		if (oldRow!=undefined) {
		    oldRow.style.backgroundColor='#ffffff';
		} 
		var table = document.getElementById(currentTableId);
		row = table.rows[rowIndex+1];
	    row.style.backgroundColor= '#AAAAAA';
	    oldRow=row;
	} catch(ex) {
	}	    
}
function aon_focusOnRow(row) {
	try {
	    inputs = row.getElementsByTagName("input");
	    if (inputs && inputs.length>0) {
	    	for (i=0; i<inputs.length; i++) {
	    		input = inputs[i];
	    		if (input.id.endsWith("selectButton") ) {
					input.focus();
	    			break;
				}
	    	}
	    }
	} catch(ex) {
	}    
}
function aonTableKeyUp(keyCode,rowIndex) {
	rowIndex++;
	if (keyCode == DOWN) {
		var table = document.getElementById(currentTableId);	
		if (rowIndex<table.rows.length) {
			rowIndex++;
			row = table.rows[rowIndex];
			if (row) {
				aon_focusOnRow ( row );
			}		
		}		
	} else if (keyCode == UP) {
		if (rowIndex>1) {
			rowIndex--;
			var table = document.getElementById(currentTableId);
			row = table.rows[rowIndex];
			aon_focusOnRow ( row );		
		}
	}
}
