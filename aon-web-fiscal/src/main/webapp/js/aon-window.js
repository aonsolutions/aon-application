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

window.onload=aonFocus;