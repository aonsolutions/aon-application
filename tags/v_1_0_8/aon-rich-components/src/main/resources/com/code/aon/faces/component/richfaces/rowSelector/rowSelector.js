var styleHolder;

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
