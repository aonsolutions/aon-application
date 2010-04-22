function openWindow(form, action) {
	hform=document.forms[form];
	hform.action=action;
    var win3 = centerWindow(700, 550, hform.target);
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
			 ',toolbar=0,location=0,directories=0,status=1,menubar=0,scrollbars=1,resizable=1';
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
	var footerHeight=0;
	var errorsHeight=0;
	var windowPopUpHeaderHeight=0;
	var windowPopUpFooterHeight=0;
	var windowPopUpTitleHeight=0;
	var hpHypercubeHeight=0;
	var titleHeight=0;
	var hpHypercube=0;
	
	var headerRegion=document.getElementById("aon-header-container");	
	var toolbarRegion=document.getElementById("aon-general-toolbar");
	var subtitleRegion=document.getElementById("aon-content-subtitle");
	
	var footerRegion=document.getElementById("aon-footer-container");
	var windowPopUpTitle=document.getElementById("aon-window-popup-title");
	var hpTitle=document.getElementById("aon-general-title");
	var hpHypercube=document.getElementById("aon-hypercube-item");
	
	
	if(headerRegion!=null){
		var headerHeight=headerRegion.offsetHeight;
	}
	if(toolbarRegion!=null){
		var toolbarHeight=toolbarRegion.offsetHeight;	
		}
	if(hpTitle!=null){
		var titleHeight=hpTitle.offsetHeight;	
	
	}
	if(subtitleRegion!=null){
		var subtitleHeight=subtitleRegion.offsetHeight;	
	
	}
	if(footerRegion!=null){
		var footerHeight=footerRegion.offsetHeight;	
	
	}
	if(windowPopUpTitle!=null){
		var windowPopUpTitleHeight=windowPopUpTitle.offsetHeight;			
	
	}

	var remain_height = headerHeight + toolbarHeight + titleHeight + subtitleHeight + footerHeight + windowPopUpTitleHeight ;
	var el =  document.getElementById("aon-scroll-area");
	var sidebar=document.getElementById("aon-sidebar-region");

	if(el){	
		if((y-remain_height)>0){
			el.style.height= (y-remain_height) + "px";						
		}
		else{
			el.style.height="0px";
		}
	}
	if(sidebar){	
		if((y-remain_height)>0){
			sidebar.style.height= (y-remain_height) + "px";			
		}
		else{
			sidebar.style.height="0px";
		}
	}
	
	elem = document.getElementById("actual_tree_node");
	if (elem) {
		document.location = "#actual_tree_node";
	}

	return false;
}
