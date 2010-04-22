// Este metodo se lanza al cargar la ventana.
function loadInnerWindow(){
	try {	// event user interface
		if (document.getElementById('employeeForm:recurrence').style.display == '' 
				|| document.getElementById('selone').checked) 
			checkFrequency();
	} catch(e) {}
	try {	// incidence user interface
		enableData();
		incidenceType = document.getElementById('incidencesForm:Incidence_type');
		enableStartTime(incidenceType);
	} catch(e) {}
}

function checkFrequency() {
	var elements = disableAllFrequency();
    var i;
    for (i=0;i<elements.length;i++){ 
    	try {
			if (elements.item(i).checked) 
				break; 
    	} catch(e) {}
    } 
	document.getElementById('frequency_' + elements.item(i).value).style.display='block';
}

function disableAllFrequency() {
	var i;
   	var elements = document.getElementsByName("Event_frequency");
	for (i=0;i<elements.length;i++){ 
		try {
			document.getElementById('frequency_' + elements.item(i).value).style.display='none';
		} catch(e) {}
	} 
	return elements;
}

function enableIncidence(selone) {
	var incidence = document.getElementById('employeeForm:Event_category_incidence');
	if (selone.value == 'Incidencia') { 
		incidence.style.display = 'block';
		incidence.focus();
		document.getElementById('Event_title').value='';
	} else {
		incidence.style.display = 'none';
	}
}

function enableData() {
	length_label = document.getElementById('incidencesForm:Incidence_length_label');
	length_id = document.getElementById('incidencesForm:Incidence_length');
	startTime_label = document.getElementById('incidencesForm:Incidence_startTime_label');
	startTime = document.getElementById('incidencesForm:Incidence_startTime');
	frecuency_label = document.getElementById('incidencesForm:Incidence_frecuency_label');
	frecuency = document.getElementById('incidencesForm:Incidence_frecuency');
	endDate_label = document.getElementById('incidencesForm:Incidence_endDate_label');
	endDate_div = document.getElementById('incidencesForm:Incidence_endDate_div');
	allday = document.getElementById('incidencesForm:Incidence_allday');
	if (allday.checked) {
		startTime_label.style.display='none'; 
		startTime.style.display='none'; 
		length_label.style.display='none'; 
		length_id.style.display='none'; 
		endDate_label.style.display='none';
		endDate_div.style.display='none';
		frecuency_label.style.display='none';
		frecuency.style.display='none';
	} else {
		startTime_label.style.display='block';
		startTime.style.display='block';
		frecuency_label.style.display='block';
		frecuency.style.display='block';
		if (frecuency.checked) {
			length_label.style.display='none'; 
			length_id.style.display='none'; 
			endDate_label.style.display='block';
			endDate_div.style.display='block';
		} else {
			length_label.style.display='block';
			length_id.style.display='block';
			endDate_label.style.display='none';
			endDate_div.style.display='none';
		}
	}
}

function enableStartTime(incidenceType) {
	checks = document.getElementById('incidencesForm:Incidence_checks');
	startTime_label = document.getElementById('incidencesForm:Incidence_startTime_label');
	startTime = document.getElementById('incidencesForm:Incidence_startTime');
	allday = document.getElementById('incidencesForm:Incidence_allday');
	if (incidenceType.options[incidenceType.selectedIndex].value == '9') {
		document.getElementById('incidencesForm:Incidence_frecuency').checked = false;
		allday.checked = false;
		enableData();
		startTime_label.style.display='none'; 
		startTime.style.display='none'; 
		checks.style.display='none'; 
	} else {
		if (!allday.checked) {
			startTime_label.style.display='block'; 
			startTime.style.display='block'; 
		}
		checks.style.display='block'; 
	}
}
