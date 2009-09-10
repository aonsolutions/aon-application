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
		alert("focusInFirstInput: " + ex );
	}	  		
}

var oldRow;
var tableId;

function selectFirstRow(tableName) {
	var t = document.getElementsByTagName("table");
	var table;
	var row;
	for (i=0;i<t.length;i++) {
		if (t[i].id.indexOf(tableName) > - 1 ) {
			table = t[i];
			tableId = t[i].id; 
			break; 
		}
	}
	if (table) {
		row = table.rows[1];
		focusOnRow( row );
	}
}
function changeRowSelection(rowIndex) {
	try {
		if (oldRow!=undefined) {
		    oldRow.style.backgroundColor='#ffffff';
		} 
		var table = document.getElementById(tableId);
		row = table.rows[rowIndex+1];
	    row.style.backgroundColor= '#AAAAAA';
	    oldRow=row;
	} catch(ex) {
		alert("changeRowSelection: " + ex );
	}	    
}
function focusOnRow(row) {
    e = row.getElementsByTagName("input");
    if (e && e.length>0) {
    	for (i=0;i<e.length;i++) {
    		try {
    			e[i].focus();
    			break;
    		} catch(ex) {}
    	}
    }
}
function tableKeyUp(keyCode,rowIndex) {
	rowIndex++;
	if (keyCode == DOWN) {
		var table = document.getElementById(tableId);	
		if (rowIndex<table.rows.length) {
			rowIndex++;
			row = table.rows[rowIndex];
			if (row) {
				focusOnRow ( row );
			}		
		}		
	} else if (keyCode == UP) {
		if (rowIndex>1) {
			rowIndex--;
			var table = document.getElementById(tableId);
			row = table.rows[rowIndex];
			focusOnRow ( row );		
		}
	}
}
