function aonFocusFirstInput( formId ) {
	try {
		mainForm = null;
		if (formId!=undefined) {
			var forms = document.getElementsByTagName("form");
			for (i=0; i<forms.length; i++) {
				if (forms[i].id.endsWith(formId) ) {
					mainForm = forms[i];
					break;
				}
			}
		} else {
			if ( document.forms.length>0 ) {
				mainForm = document.forms[0];
			}
		}
		if ( mainForm != null ) {		
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
	 	}
	} catch(ex) {
	}	  		
}

var aon_tableId;

function aonSelectFirstRow( tableId ) {
	var t = document.getElementsByTagName("table");
	for (i=0;i<t.length;i++) {
		if (t[i].id.endsWith(tableId) ) {
			table = t[i];
			aon_tableId = t[i].id; 
			break; 
		}
	}
	if (table) {
		if ( table.rows.length > 0 ) {
			row = table.rows[1];
			aon_focusOnRow( row );
		}
	} else {
		aon_tableId = null;
	}
}

function aon_focusOnRow(row) {
	try {
	    inputs = row.getElementsByTagName("a");
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

var UP_KEY_CODE = 38;
var DOWN_KEY_CODE = 40;

function aonTableKeyUp(keyCode,rowIndex) {
	rowIndex++;
	if (keyCode == DOWN_KEY_CODE) {
		var table = document.getElementById(aon_tableId);	
		if (rowIndex<table.rows.length) {
			rowIndex++;
			row = table.rows[rowIndex];
			if (row) {
				aon_focusOnRow ( row );
			}		
		}		
	} else if (keyCode == UP_KEY_CODE) {
		if (rowIndex>1) {
			rowIndex--;
			var table = document.getElementById(aon_tableId);
			row = table.rows[rowIndex];
			aon_focusOnRow ( row );		
		}
	}
}
