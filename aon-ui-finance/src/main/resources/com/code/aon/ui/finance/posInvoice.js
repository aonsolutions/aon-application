function jzebraReady() {
	changePrintTicketPrinting();
	printTicket();
}

function printTicket() {
	try {
		var _applet = document.getElementById('jzebra');
		if (! _applet ) {
			showError( "El Applet no se cargado" );
			return false;
		}
		var ticketData = document.getElementById('aonContent:printTicketForm:ticketData');
		if (! ticketData ) {
			showError( "No se ha encontrado el texto de la factura simplificada" );
			return false;
		}		

		var ticketText = ticketData.value;

		_applet.append64(ticketText);
		_applet.print();
		
		monitorPrinting();
	} catch(e) {
		showError( e.message );
	}
}

function monitorPrinting() {
	try {
		var _applet = document.getElementById('jzebra');
		if (_applet != null) {
			if (!_applet.isDonePrinting()) {
				window.setTimeout('monitorPrinting()', 100);
			} else {
				var e = _applet.getException();
				if ( e == null ) {
					var _closeButton = document.getElementById('printTicketCloseButton');
					if ( _closeButton ) {
						_closeButton.click();	
					}
				} else {
					showError( e.getLocalizedMessage() );
				}
			}
		}
	} catch(e) {
		showError( e.message );
	}
}

function showError( text ) {
	var ticketStatus = document.getElementById('aonContent:printTicketForm:printTicketStatus');
	if ( ticketStatus ) {
		changePrintTicketEror();
		ticketStatus.innerHTML= ticketStatus.innerHTML + ": " + text;
	}
}