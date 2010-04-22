var formId; 	// reference to the main form
var winId;		// reference to the popup window
var formToSubmit;
var mustSubmitForm = false;


function selectWindowAndSubmit(action, form, target) {
	selectWindow(action, form, target);
	mustSubmitForm = true;
	formToSubmit = action.form;
}

function lookupWindow(action, form, target, type, ids) {
	formId=action.form.id;
	hform=document.forms[form];
	hform.action=target + "?type=" + type + ( (ids) ? "&ids=" + ids : "");
    var win3 = centerWindow(450, 600 );
	winId = window.open(hform.action, hform.target);
}

function lookupListWindow(action, form, target, ids) {
	lookupWindow(action, form, target, "list", ids);
}

function lookupSearchWindow(action, form, target, ids) {
	lookupWindow(action, form, target, "search", ids);
}

function lookupNewWindow(action, form, target, ids) {
	lookupWindow(action, form, target, "new", ids);
}

function userTextWindow(element, form, action, glprofile) {
	formId=element.form.id;
	hform=document.forms[form];
	elementId=element.id.substring(element.id.indexOf('_')+1);
	hform.action=action + "?DSProperty=" +elementId+ "&GLProfile=" + glprofile;
    var win3 = centerWindow(400, 600);
	winId = window.open(hform.action, hform.target);
}

// This function is called from the popup window 
// when a user clicks on a row from the list.
// The selected row values are copied to a set of text fields in the main form.
function update(to) {
	var xmlDoc;
    if (window.ActiveXObject) { 
        xmlDoc = new ActiveXObject("MSXML2.DOMDocument"); 
        xmlDoc.loadXML(to); 
    } else if(document.implementation) { 
        var parser = new DOMParser(); 
        xmlDoc = parser.parseFromString(to,"text/xml");     
    }     
	var items = xmlDoc.getElementsByTagName("item")[0];
	// loop through <item> elements, and update each lookup element
    for (var i = 0; i < items.childNodes.length; i++) {
    	try {
    	    var child = items.childNodes[i];
    		var nodeName = child.getAttribute('name');
    		var node = document.getElementById(nodeName);
    		if ( node ) {
				node.value = child.getAttribute('value');
				// MyFaces disabledOnClientSide
	   			if(node.onchange != null){
				node.onchange();
            }
            }
    	} catch(e) {
    	   alert( e );
        }
    }
    // Se cierra manualmente en la ventana de Select Window
	// winId.close();
	if (mustSubmitForm) {
		formToSubmit.submit();
	}
}

function reportWindow(element, form, action, id) {
	reportKey=element.id;
	hform=document.forms[form];
	var url = action + "?reportKey=" +reportKey;
	if ( id ) {
		url += "&id=" + id; 
	}
	hform.action = url;
    var win3 = centerWindow(600, 250);
	winId = window.open(hform.action, hform.target);
	hform.submit();// Forces actionListener to be executed.
}

function birtReportWindow(element, form, action, id) {
	hform=document.forms[form];
	var url = action;
	if ( id ) {
		url += "?id=" + id; 
	}
	hform.action = url;
	var win3 = centerWindow(600, 400);
	winId = window.open(hform.action, hform.target);
	hform.submit();// Forces actionListener to be executed.
}

function fileUploadWindow( form, listener, action, returnPage, bundle) {
	hform=document.forms[form];
	var fullAction = action + "?listener=" + listener + "&returnPage=" + returnPage;
	if ( bundle ) {
		fullAction += "&bundle=" + bundle;
	}
	hform.action = fullAction;
	var win3 = centerWindow(450, 210);
	winId = window.open(hform.action, hform.target);
	hform.submit();// Forces actionListener to be executed.
}

function downloadWindow( form, controller, action) {
	hform=document.forms[form];
	var fullAction = action + "?controllerName=" + controller;
	hform.action = fullAction;
	var win3 = centerWindow(450, 120);
	winId = window.open(hform.action, hform.target);
	hform.submit();// Forces actionListener to be executed.
}

function centerWindow(popW, popH, target) {
	if (!target || target == "") {
		target = "list";
	}
	var w = 480, h = 340;
	if (document.all) {
	   /* the following is only available after onLoad */
	   w = document.body.clientWidth;
	   h = document.body.clientHeight;
	} else 
		if (document.layers) {
			w = window.innerWidth;
			h = window.innerHeight;
		}
	var leftPos = (w-popW)/2, topPos = (h-popH)/2;
	features='width=' + popW + 
			 ',height='+popH+
			 ',top='+topPos+
			 ',left='+leftPos+
			 ',toolbar=0,location=0,directories=0,status=1,menubar=0,scrollbars=0,resizable=1';
	return window.open('',target,features);
}

/*this function set the body height and the scroll to a fixed number*/
function aonResizeBody(){
	if (document.body) {
		y = document.body.clientHeight;
	}else if (document.documentElement && document.documentElement.clientHeight) {
		y = document.documentElement.clientHeight;		
	}
	if (self.innerHeight) {
		//FF
		y = self.innerHeight;
	}
	
	var headerHeight=0;
	var toolbarHeight=0;
	var subtitleHeight=0;
	var headerRegion=document.getElementById("aon-content-region-header");	
	var toolbar=document.getElementById("aon-content-toolbar");
	var subtitle=document.getElementById("aon-content-subtitle");
	var errors=document.getElementById("aon-errors");
	var windowPopUpHeader=document.getElementById("aon-window-popup-header");
	var windowPopUpFooter=document.getElementById("aon-window-popup-footer");
	var windowPopUpTitle=document.getElementById("aon-window-popup-title");
	var contentMenu=document.getElementById("aon-content-menu");
	
	
	var headerHeight=0;
	var toolbarHeight=0;
	var subtitleHeight=0;
	var errorsHeight=0;
	var windowPopUpHeaderHeight=0;
	var windowPopUpFooterHeight=0;
	var windowPopUpTitleHeight=0;
	var contentMenuHeight=0;
	
	if(headerRegion!=null){
		var headerHeight=headerRegion.offsetHeight;	
				
	}
	if(toolbar!=null){
		var toolbarHeight=toolbar.offsetHeight;	
			
	}
	if(subtitle!=null){
		var subtitleHeight=subtitle.offsetHeight;
			
	}
	if(errors!=null){
		var errorsHeight=errors.offsetHeight;	
		
	}
	if(windowPopUpHeader!=null){
		var windowPopUpHeaderHeight=windowPopUpHeader.offsetHeight;			
	}
	if(windowPopUpFooter!=null){
		var windowPopUpFooterHeight=windowPopUpFooter.offsetHeight;			
	}
	if(windowPopUpTitle!=null){
		var windowPopUpTitleHeight=windowPopUpTitle.offsetHeight;			
	}
	
	if(contentMenu!=null){
		var contentMenuHeight=contentMenu.offsetHeight;			
	}
	var remain_height=0;
	
	remain_height = headerHeight + toolbarHeight + subtitleHeight + errorsHeight;
	remain_height+=windowPopUpHeaderHeight+ windowPopUpFooterHeight + windowPopUpTitleHeight+contentMenuHeight;


	
	if((y-remain_height)>0){
		document.getElementById("aon-scroll-area").style.height= (y-remain_height) + "px";
	}
	else{
		document.getElementById("aon-scroll-area").style.height="0px";
	}
	
	return false;
}

/*this function set the focus*/
function aonFocus(){
	try {
		forms = document.forms;
		for (var i = 0; i < forms.length; i++) {
			controls = forms[i].elements;
			for (var j = 0; j < controls.length; j++) {
				if(controls[j].disabled == false){
					if (controls[j].tagName == 'INPUT') {
				     	   if (controls[j].type == 'text'){
					        controls[j].focus();
					        controls[j].select();
					        return;
				           }
				    	}
					if (controls[j].tagName == 'SELECT') {
						if (controls[j].id.indexOf("Application_menus") < 0) {
							controls[j].focus();
				        		return;
				        	}
				        }
				}    
			}
		}
	} catch(e) {
	}
}

function focusTableRow() {
	try {
		var mylist = document.forms[2];
		var listitems = mylist.getElementsByTagName("table");
		for (i=0; i < listitems.length; i++) {
			if (listitems[i].getAttribute("id").length > 0) {
				var inputEl = listitems[i].getElementsByTagName("input");
				for (j=0; j < inputEl.length; j++) {
					if(inputEl[j].getAttribute("type") == "text"){
						inputEl[j].focus();
						inputEl[j].select();
						break;
					}
				}
			}
		}
	} catch(e) {}
}

function windowLoaded(){
	aonResizeBody();
	aonFocus();
	focusTableRow();
	try {
// Función que permite la ejecución de código JavaScript dentro de la ventana padre.
		loadInnerWindow();
	} catch(e) {}
}
