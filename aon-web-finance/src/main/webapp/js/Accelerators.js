/*
NOTA 1: en la pantalla el elemento a recorrer debe ser una tabla (TABLE)
	y el id debe contener el siguiente texto "DATALIST" o "DATASW" como por ejemplo "modelDataList" o "modelDataSW".

NOTA 2: al pulsar letras o numeros se posiciona en la fila que contenga
	ese texto, siempre buscando en la columna 1 y si no encuentra nada en la 2 (si existe).
*/

/* KEYS */
var VK_CANCEL = 3;
var VK_BACKSPACE = 8;
var VK_TAB = 9;
var VK_ENTER = 13;
var VK_CTRL = 18;
var VK_ALT = 18;
var VK_ESCAPE = 27;
var VK_PAGE_UP = 33;
var VK_PAGE_DOWN = 34;
var VK_END = 35;
var VK_HOME = 36;
var VK_LEFT = 37;
var VK_UP = 38;
var VK_RIGHT = 39;
var VK_DOWN = 40;
var VK_INSERT = 45;
var VK_DELETE = 46;
var VK_M = 77;
var VK_F1 = 112;
var VK_F2 = 113;
var VK_F3 = 114;
var VK_F4 = 115;
var VK_F5 = 116;
var VK_F6 = 117;
var VK_F7 = 118;
var VK_F8 = 119;
var VK_F9 = 120;
var VK_F10 = 121;
var VK_F11 = 122;
var VK_F12 = 123;
var VK_KP_PLUS = 107;
var VK_KP_MINUS = 109;

/* BUTTONS */
NEW_BUTTON = "RESET";
DEL_BUTTON = "REMOVE";
SAVE_BUTTON = "SAVE";
CANCEL_BUTTON = "CANCEL";

/* FORMS */
TREE_FORM = "treeMenuForm";
BREADCRUM_FORM = "breadcrumForm";

/* SPIN */
SPIN_FIRST = "first";
SPIN_LAST = "last";
SPIN_NEXT = "next";
SPIN_PREVIOUS = "previous";

/* VARIABLES */
var _navigator = navigator.appName;
var _ns = (_navigator=="Netscape");
var _ie = (_navigator=="Microsoft Internet Explorer");

var previousKey = null; //Save the Alt or Ctrl like Previous Pressed Key
var selected = null; //Selected row in a DataList or DataSW List
var table = null; //Selected table to be crossed
var menu = null; //Selected menu option

var searchArray = "";
var refresh = false;
var tmreset;

//Capture events in Netscape
if (_ns) document.captureEvents(Event.KEYDOWN | Event.KEYUP);

//Capture events in Internet Explorer
document.onkeydown = keydownfn;
document.onkeyup = clearfn;
document.onkeypress = keypressfn;

//Disable Context Menu (Right Click or Context Windows Key)
document.oncontextmenu = new Function("return false");

/* ---------------------------------------------------------------------- */
/* --------------------------- COMMON FUNCTIONS ------------------------- */
/* -------------------------------- BEGIN ------------------------------- */
/* ---------------------------------------------------------------------- */
function getContext() {
	var path = location.pathname;
	path = path.substring(0,path.indexOf("/",1));
	return path;
}

function editingText() {
    try {
    	//Get the element with focus
	var element;
	if (_ns) element = ev.target; //Netscape
	if (_ie) element = event.srcElement; //Internet Explorer
	//if the element is an editable element then return true
	if ((element.tagName == "INPUT"
	      && (element.type.toUpperCase() != "TEXT"
	      ||  element.type.toUpperCase() != "BUTTON"
	      ||  element.type.toUpperCase() != "SUBMIT"
	      ||  element.type.toUpperCase() != "IMAGE"))
	  || element.tagName == "TEXTAREA"
	  || element.tagName == "SELECT") return true;
    }
    catch (e) {}
    return false;
}

function isMenuFocus() {
    try {
		var element;
		if (_ns) element = ev.target; //Netscape
		if (_ie) element = event.srcElement; //Internet Explorer
		if (element.tagName == "A") {
			var id = element.id.toUpperCase();
			if (id.indexOf("_MENU") >= 0
			 || id.indexOf("MENUID") >= 0
			 || id.indexOf("NODEID") >= 0
			 || id.indexOf("_ID") >= 0) return true;
		}
		return false;
    }
    catch (e) {}
}

function localizeTable() {
    try {
		elements = document.getElementsByTagName("TABLE");
		for (i=0;i<elements.length;i++) {
			id = elements[i].id.toUpperCase();
			if (id.indexOf("DATALIST") >= 0 || id.indexOf("DATASW") >= 0) table = elements[i];
		}
    }
    catch (e) {}
}

function formFocus() {
    try {
		forms = document.forms;
		for (var i = 0; i < forms.length; i++) {
			if (forms[i].id != TREE_FORM && forms[i].id != BREADCRUM_FORM) {
				controls = forms[i].elements;
				for (var j = 0; j < controls.length; j++) {
					if(controls[j].disabled == false){
						if (controls[j].tagName == 'INPUT') {
							controls[j].focus();
							return;
						}
					}
				}
			}
		}
    }
    catch (e) {}
}

function cancelEvent(ev) {
    try {
		if (_ns) { //Netscape
			ev.returnValue = false;
			ev.cancelBubble = true;
			ev.stopPropagation();
		}
		if (_ie) window.event.returnValue = false; //Internet Explorer
    }
    catch (e) {}
}

function onCancel() {
	close();
}
/* ---------------------------------------------------------------------- */
/* --------------------------- COMMON FUNCTIONS ------------------------- */
/* --------------------------------- END -------------------------------- */
/* ---------------------------------------------------------------------- */

/* ---------------------------------------------------------------------- */
/* ------------------------------- KEYDOWN ------------------------------ */
/* -------------------------------- BEGIN ------------------------------- */
/* ---------------------------------------------------------------------- */
function keydownfn( ev ) {
    try {
    	//Get the Pressed Key
		var key;
		if (_ns) key = ev.which; //Netscape
		if (_ie) key = event.keyCode; //Internet Explorer

		switch ( key ) {
			case VK_F1: //(F1) Help Key
				if (previousKey == VK_ALT) openHelpPage();
				else openAcceleratorsPage();
				cancelEvent(ev);
				return false;

			case VK_M: //(ALT + M) Menu Focus Key
				if (previousKey == VK_ALT) {
					menuFocus();
					cancelEvent(ev);
					return false;
				}
				break;

			case VK_BACKSPACE: //(<--)
				var element;
				if (_ns) element = ev.target;
				if (_ie) element = event.srcElement;
				if (element.tagName != "INPUT" && element.tagName != "TEXTAREA") {
					cancelEvent(ev);
					return false;
				}
				break;

			case VK_UP: //(UP Arrow)
				if (!editingText()) {
					if (!isMenuFocus()) {
						selectPrev();
						cancelEvent(ev);
					}
					else {
						selectPrevMenu();
						cancelEvent(ev);
					}
					return false;

				}
				break;

			case VK_DOWN: //(DOWN Arrow)
				if (!editingText()) {
					if (!isMenuFocus()) {
						selectNext();
						cancelEvent(ev);
					}
					else {
						selectNextMenu();
						cancelEvent(ev);
					}
					return false;
				}
				break;

			case VK_LEFT: //(LEFT Arrow)
				if (!editingText()) {
					if (!isMenuFocus()) {
						pageUp();
						cancelEvent(ev);
						return false;
					}
				}
				break;

			case VK_RIGHT: //(RIGHT Arrow)
				if (!editingText()) {
					if (!isMenuFocus()) {
						pageDown();
						cancelEvent(ev);
						return false;
					}
				}
				break;

			case VK_HOME: //(HOME Key)
				if (!editingText()) {
					if (!isMenuFocus()) {
						selectFirstPage();
					}
					else {
						selectFirstMenu();
					}
					return false;
				}
				break;

			case VK_END: //(END Key)
				if (!editingText()) {
					if (!isMenuFocus()) {
						selectLastPage();
					}
					return false;
				}
				break;

			case VK_PAGE_UP: //(PAGE UP Key)
				if (!editingText()) {
					if (!isMenuFocus()) {
						pageUp();
						cancelEvent(ev);
						return false;
					}
				}
				break;

			case VK_PAGE_DOWN: //(PAGE DOWN Key)
				if (!editingText()) {
					if (!isMenuFocus()) {
						pageDown();
						cancelEvent(ev);
						return false;
					}
				}
				break;

			case VK_ENTER: //(INTRO Key)
				if (!editingText()) {
					if (!isMenuFocus()) {
						if ( selected ){
							sendRow(selected);
							cancelEvent(ev);
							return false;
						}
					}
				}
				break;

			case VK_ESCAPE: //(ESC Key)
				if (!isMenuFocus()) {
					if (!executeButton(CANCEL_BUTTON)) onCancel();
					cancelEvent(ev);
				}
				else {
					formFocus();
					cancelEvent(ev);
				}
				break;

			case VK_F12: //(F12) Save button
				executeButton(SAVE_BUTTON);
				cancelEvent(ev);
				return false;

			case VK_INSERT: //(Ins) New button
				if (!editingText()) {
					executeButton(NEW_BUTTON);
					cancelEvent(ev);
					return false;
				}
				break;

			case VK_DELETE: //(Del) Remove button
				if (!editingText()) {
					executeButton(DEL_BUTTON);
					cancelEvent(ev);
					return false;
				}
				break;

			case VK_KP_PLUS: //(+) Open menu
				if (!editingText()) {
					if (isMenuFocus()) {
						openMenu();
						cancelEvent(ev);
						return false;
					}
				}
				break;

			case VK_KP_MINUS: //(-) Close menu
				if (!editingText()) {
					if (isMenuFocus()) {
						closeMenu();
						cancelEvent(ev);
						return false;
					}
				}
				break;
		}
		if(key == VK_ALT || key == VK_CTRL) previousKey = key;
		else previousKey = null;
		return true;
    }
    catch (e) {}
}
/* ---------------------------------------------------------------------- */
/* ------------------------------- KEYDOWN ------------------------------ */
/* --------------------------------- END -------------------------------- */
/* ---------------------------------------------------------------------- */

/* ---------------------------------------------------------------------- */
/* -------------------------------- KEYUP ------------------------------- */
/* -------------------------------- BEGIN ------------------------------- */
/* ---------------------------------------------------------------------- */
function clearfn( ev ) {
	previousKey = null;
}
/* ---------------------------------------------------------------------- */
/* -------------------------------- KEYUP ------------------------------- */
/* --------------------------------- END -------------------------------- */
/* ---------------------------------------------------------------------- */

/* ---------------------------------------------------------------------- */
/* ------------------------------- KEYPRESS ----------------------------- */
/* -------------------------------- BEGIN ------------------------------- */
/* ---------------------------------------------------------------------- */
function keypressfn( ev ) {
    try {
		var letter = String.fromCharCode(event.keyCode);
		searchArray = searchArray + letter.toUpperCase();
		var row = selectByChar( searchArray );
		if ( row ) {
			selectRowList( row );
		}
		resetChars();
    }
    catch (e) {}
}

function selectByChar( ch ){
    try {
		if (table == null) localizeTable();
		var rows = table.rows;
		for ( i = 1; i < rows.length; ++i ){
			var row = rows[i];
			if ( startsWith ( row, ch ) )
				return row;
		}
		return null;
    }
    catch (e) {}
}

function startsWith ( row, ch ){
    try {
		//Look for in first cell
		var data = row.cells[0].innerText;
		var result = data.toUpperCase().indexOf(ch);
		if (result != 0) {
			//Look for in second cell
			data = row.cells[1].innerText;
			result = data.toUpperCase().indexOf(ch);
		}
		return ( result == 0  );
    }
    catch (e) {}
    return false;
}

function resetChars() {
    try {
		if (tmreset != null) clearTimeout(tmreset);
		tmreset = setTimeout("resetSearchArray()", 1000);
    }
    catch (e) {}
}

function resetSearchArray() {
    try {
		searchArray = "";
		clearTimeout(tmreset);
    }
    catch (e) {}
}

/* ---------------------------------------------------------------------- */
/* ------------------------------- KEYPRESS ----------------------------- */
/* --------------------------------- END -------------------------------- */
/* ---------------------------------------------------------------------- */

/* ---------------------------------------------------------------------- */
/* -------------------------- LIST CROSS METHODS ------------------------ */
/* -------------------------------- BEGIN ------------------------------- */
/* ---------------------------------------------------------------------- */
function selectRowList(row){
    try {
		if ( selected ) unselectRow( selected );
		selectRow( selected = row );
		if (!refresh) refreshSelected();
    }
    catch (e) {}
}

function refreshSelected() {
    try {
		refresh = true;
		if ( selected ) selectRow( selected );
		setTimeout("refreshSelected()", 500);
    }
    catch (e) {}
}

function selectFirst() {
    try {
		if (table == null) localizeTable();
		var rows = table.rows;
		if ( rows.length > 1 ) {
			selectRowList( rows [ 1 ] );
		}
    }
    catch (e) {}
}

function selectPrev(){
    try {
		if ( !selected ) {
			selectFirst();
		}
		else if ( selected.rowIndex > 1 ) {
			unselectRow( selected );
			selectRow( selected = selected.previousSibling );
		}
    }
    catch (e) {}
}

function selectNext(){
    try {
		if ( selected ){
			if ( selected.nextSibling ){
				unselectRow( selected );
				selectRow( selected = selected.nextSibling );
			}
		}
		else {
			selectFirst();
		}
    }
    catch (e) {}
}

function selectLast(){
    try {
		if ( !selected ) {
			selectFirst();
		}
		var rows = table.rows;
		var count = rows.length;
		if ( count > 1 )
			selectRow( rows [ count - 1 ] );
	}
    catch (e) {}
}

function selectFirstPage() {
    try {
		elements = document.getElementsByTagName("A");
		for (i=0;i<elements.length;i++) {
			id = elements[i].id;
			if (id.indexOf(SPIN_FIRST) >= 0) {
				elements[i].click();
				return;
			}
		}
    }
    catch (e) {}
}

function pageUp() {
    try {
		elements = document.getElementsByTagName("A");
		for (i=0;i<elements.length;i++) {
			id = elements[i].id;
			if (id.indexOf(SPIN_PREVIOUS) >= 0) {
				elements[i].click();
				return;
			}
		}
    }
    catch (e) {}
}

function pageDown(){
    try {
		elements = document.getElementsByTagName("A");
		for (i=0;i<elements.length;i++) {
			id = elements[i].id;
			if (id.indexOf(SPIN_NEXT) >= 0) {
				elements[i].click();
				return;
			}
		}
    }
    catch (e) {}
}

function selectLastPage() {
    try {
		elements = document.getElementsByTagName("A");
		for (i=0;i<elements.length;i++) {
			id = elements[i].id;
			if (id.indexOf(SPIN_LAST) >= 0) {
				elements[i].click();
				return;
			}
		}
    }
    catch (e) {}
}
/* ---------------------------------------------------------------------- */
/* -------------------------- LIST CROSS METHODS ------------------------ */
/* --------------------------------- END -------------------------------- */
/* ---------------------------------------------------------------------- */

/* ---------------------------------------------------------------------- */
/* -------------------------- MENU CROSS METHODS ------------------------ */
/* -------------------------------- BEGIN ------------------------------- */
/* ---------------------------------------------------------------------- */
function menuFocus() {
    try {
		if (menu != null) menu.focus();
		else {
			element = document.getElementById("_menu");
			element.focus();
		}
    }
    catch (e) {}
}

function openMenu() {
    try {
		if ( menu ) {
			p = menu.parentNode;
			c = p.firstChild;
			while (c.tagName != "A" && c.id.toUpperCase().indexOf("FOLDERID") < 0) {
				c = c.nextSibling;
			}
			c.click();
		}
    }
    catch (e) {}
}

function closeMenu() {
	openMenu();
}

function selectFirstMenu() {
    try {
		elements = document.getElementsByTagName("A");
		for (i=0;i<elements.length;i++) {
			id = elements[i].id.toUpperCase();
			if (id.indexOf("MENUID") >= 0 || id.indexOf("NODEID") >= 0 || id.indexOf("_ID") >= 0) {
				menu = elements[i];
				menu.focus();
				return;
			}
		}
    }
    catch (e) {}
}

function selectNextMenu(){
    try {
		if ( menu ) {
			id2 = menu.id.toUpperCase();
			var find = false;
			elements = document.getElementsByTagName("A");
			for (i=0;i<elements.length;i++) {
				id = elements[i].id.toUpperCase();
				if (id.indexOf("MENUID") >= 0 || id.indexOf("NODEID") >= 0 || id.indexOf("_ID") >= 0) {
					if (find) {
						menu = elements[i];
						menu.focus();
						return;
					}
					else if (id == id2) find = true;
				}
			}
		}
		else {
			selectFirstMenu();
		}
    }
    catch (e) {}
}

function selectPrevMenu(){
    try {
		if ( menu ) {
			id2 = menu.id.toUpperCase();
			var find = false;
			var prev = null;
			elements = document.getElementsByTagName("A");
			for (i=0;i<elements.length;i++) {
				id = elements[i].id.toUpperCase();
				if (id.indexOf("MENUID") >= 0 || id.indexOf("NODEID") >= 0 || id.indexOf("_ID") >= 0) {
					if (find) {
						menu = prev;
						menu.focus();
						return;
					}
					else if (id == id2) find = true;
						 else prev = elements[i];
				}
			}
			if (find) {
				menu = prev;
				menu.focus();
				return;
			}
		}
		else {
			selectFirstMenu();
		}
    }
    catch (e) {}
}
/* ---------------------------------------------------------------------- */
/* -------------------------- MENU CROSS METHODS ------------------------ */
/* --------------------------------- END -------------------------------- */
/* ---------------------------------------------------------------------- */

/* ---------------------------------------------------------------------- */
/* ---------------------------- OTHER METHODS --------------------------- */
/* -------------------------------- BEGIN ------------------------------- */
/* ---------------------------------------------------------------------- */
function executeButton(text){
    try {
		elements = document.getElementsByTagName("INPUT");
		for (i=0;i<elements.length;i++) {
			if (elements[i].disabled == false && (elements[i].type.toUpperCase() == "SUBMIT" || elements[i].type.toUpperCase() == "IMAGE")) {
				id = elements[i].id.toUpperCase();
				if (id.indexOf(text) >= 0) {
					elements[i].click();
					return;
				}
			}
		}
    }
    catch (e) {}
}

function openHelpPage() {
	var context = getContext();
	window.open( context + '/help.pdf' );
}

function openAcceleratorsPage() {
	var context = getContext();
	window.open( context + '/facelet/about/accelerators.faces','','height=401,width=500,status=no,toolbar=no,menubar=no,location=no,resizable=no');
}

function openAboutPage() {
	var context = getContext();
	window.open( context + '/facelet/about/content.faces','','height=401,width=500,status=no,toolbar=no,menubar=no,location=no,resizable=no');
}
/* ---------------------------------------------------------------------- */
/* ---------------------------- OTHER METHODS --------------------------- */
/* --------------------------------- END -------------------------------- */
/* ---------------------------------------------------------------------- */