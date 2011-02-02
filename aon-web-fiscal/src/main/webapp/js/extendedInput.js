var UP = 38;
var DOWN = 40;

function searchFieldForFocus( name ) {
	a = document.getElementsByTagName("INPUT");
	for (i=0;i<a.length;i++) {
		if (a[i].name.indexOf(name) > - 1 ) {
			a[i].focus();
			break;
		}
	}
}

var oldRow;
var rowIndex;
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
		rowIndex = 1;
		row = table.rows[rowIndex];
		changeSelection ( row );
		
	}
}
function changeSelection(row) {
	if (oldRow!=undefined) {
	    oldRow.style.backgroundColor='#ffffff';
	} 
    row.style.backgroundColor= '#AAAAAA';
    e = row.getElementsByTagName("input");
    if (e && e.length>0) {
    	for (i=0;i<e.length;i++) {
    		try {
    			e[i].focus();
    			break;
    		} catch(ex) {}
    	}
    }
    oldRow=row;
}
function tableKeyUp( keyCode) {
	if (keyCode == DOWN) {
		if (rowIndex<16) {
			rowIndex++;
			var table = document.getElementById(tableId);
			row = table.rows[rowIndex];
			if (row) {
				changeSelection ( row );
			}		
		}		
	}
	if (keyCode == UP) {
		if (rowIndex>1) {
			rowIndex--;
			var table = document.getElementById(tableId);
			row = table.rows[rowIndex];
			changeSelection ( row );		
		}
	}
}
