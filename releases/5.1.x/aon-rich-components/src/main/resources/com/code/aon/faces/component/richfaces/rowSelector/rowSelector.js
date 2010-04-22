var styleHolder;
var rowSelectorDisabled = false;

function onDataTableOver(elmt, selectedRowClassName, hoveredRowClassName) {
	if(elmt.className.indexOf(selectedRowClassName)==-1)  { 
		styleHolder = elmt.className; 
		elmt.className = hoveredRowClassName;
	}
} 
        
function onDataTableOut(elmt,selectedRowClassName) { 
	if(elmt.className.indexOf(selectedRowClassName)==-1)  { 
		elmt.className = styleHolder; 
	} 
}			

function enableRowSelector() {
	rowSelectorDisabled = false;
}

function disableRowSelector() {
	rowSelectorDisabled = true;
}

function isRowSelectorDisabled() {
	return rowSelectorDisabled;
}