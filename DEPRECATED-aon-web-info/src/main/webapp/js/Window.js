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
	winId.close();
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
	
	var remain_height=0;	/* content height */	
	var sidebar_remain_height=0;	/* sidebar height */	
	
	//header container: contains the logo and recursive menu
	var headerContainer=document.getElementById("aon-header-container");	
	if(headerContainer!=null){
		// alert("headerContainer " + headerContainer.offsetHeight);
		remain_height+=headerContainer.offsetHeight;
		sidebar_remain_height+=headerContainer.offsetHeight;
	}

	var menuContainer=document.getElementById("aon-menu-container");	
	if(menuContainer!=null){
		// alert("headerContainer " + headerContainer.offsetHeight);
		remain_height+=menuContainer.offsetHeight;
		sidebar_remain_height+=menuContainer.offsetHeight;
	}
	
	//footerContainer: contains the copyright information
	var footerContainer=document.getElementById("aon-footer-container");
	if(footerContainer!=null){
		// alert("footerContainer " + footerContainer.offsetHeight);
		remain_height+=footerContainer.offsetHeight;	
		sidebar_remain_height+=footerContainer.offsetHeight;
	}
	
	//headerRegion: breadcrumb, title and errors 
	var headerRegion=document.getElementById("aon-content-region-header");	
	if(headerRegion!=null){
		// alert("headerRegion " + headerRegion.offsetHeight);
		remain_height+=headerRegion.offsetHeight;	
	}
	
	//generaltitle: general application title (used in hyperviews)
	var generalTitle=document.getElementById("aon-general-title");	
	if(generalTitle!=null){
		remain_height+=generalTitle.offsetHeight;	
		sidebar_remain_height+=generalTitle.offsetHeight;
	}
	
	//generaltoolbar: general toolbar (used in hyperviews)
	var generalToolbar=document.getElementById("aon-general-toolbar");	
	if(generalToolbar!=null){
		remain_height+=generalToolbar.offsetHeight;	
		sidebar_remain_height+=generalToolbar.offsetHeight;
	}
	
	//toolbar: content toolbar
	var toolbar=document.getElementById("aon-content-toolbar");	
	if(toolbar!=null){
		//alert("toolbar " + toolbar.offsetHeight);
		remain_height+=toolbar.offsetHeight;	
	}
	
	//subtitle: content subtitle
	var subtitle=document.getElementById("aon-content-subtitle");	
	if(subtitle!=null){
		//alert("subtitle " + subtitle.offsetHeight);
		remain_height+=subtitle.offsetHeight;
	}
	
	/*
	var errors=document.getElementById("aon-errors");	
	if(errors!=null){
		//alert("errors " + errors.offsetHeight);
		remain_height+=errors.offsetHeight;	
	}*/
	/*
	var windowPopUpHeader=document.getElementById("aon-window-popup-header");	
	if(windowPopUpHeader!=null){
		//alert("aon-window-popup-header " + windowPopUpHeader.offsetHeight);
		remain_height+=windowPopUpHeader.offsetHeight;			
	}
	var windowPopUpFooter=document.getElementById("aon-window-popup-footer");	
	if(windowPopUpFooter!=null){
		//alert("windowPopUpFooter " + windowPopUpFooter.offsetHeight);
		remain_height+=windowPopUpFooter.offsetHeight;			
	}
	var windowPopUpTitle=document.getElementById("aon-window-popup-title");	
	if(windowPopUpTitle!=null){
		//alert("windowPopUpTitle " + windowPopUpTitle.offsetHeight);
		remain_height+=windowPopUpTitle.offsetHeight;			
	}*/
	//contentMenu: special type of menu used to add a new information level
	var contentMenu=document.getElementById("aon-content-menu");
	if(contentMenu!=null){
		//alert("contentMenu " + contentMenu.offsetHeight);
		remain_height+=contentMenu.offsetHeight;			
	}

	var scrollArea = document.getElementById("aon-scroll-area");	
	if (scrollArea) {
		if((y-remain_height)>0){
			scrollArea.style.height= (y-remain_height) + "px";
		} else {
			scrollArea.style.height="0px";
		}
	}

	var sidebar = document.getElementById("aon-sidebar");
	if (sidebar) {
		if((y-remain_height)>0){
			sidebar.style.height= (y-sidebar_remain_height) + "px";
		} else {
			sidebar.style.height="0px";
		}
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
				        controls[j].focus();
				        return;
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
	try {
		aonResizeBody();
		aonFocus();
		focusTableRow();
	} catch(e) {
		alert( e );
	}	
}
