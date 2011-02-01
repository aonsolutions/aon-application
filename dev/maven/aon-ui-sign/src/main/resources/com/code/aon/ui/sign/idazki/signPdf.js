function signPdf() {
	try {
		var _applet = document.getElementById('izenpeApplet');
		if (! _applet ) {
			return false;
		}
		var pdf64Element = document.getElementById('aonContent:passwordForm:pdf64Data');
		if (! pdf64Element ) {
			return false;
		}		
		var pdf64SignedElement = document.getElementById('aonContent:passwordForm:pdf64SignedData');
		if (! pdf64SignedElement ) {
			return false;
		}		
		
		_applet.clearInputs();					
		_applet.setOption("pdf-signature-llx", "305" );
		_applet.setOption("pdf-signature-lly", "745" );
		_applet.setOption("pdf-signature-urx", "405" );
		_applet.setOption("pdf-signature-ury", "795" );
		if (! _applet.setCryptoStoreAuto() ) {
			return false;										
		}										
		var pdf64Text = pdf64Element.value;
		if (!_applet.addInput("inline-binary",pdf64Text,"inline",null)) {
			return false;
		}
		if (_applet.sign("pdf-timestamped")) {
			pdf64SignedElement.value = _applet.getOutputContent(0, true);
		} else {
			return false;
		}
		return true;
	} catch(e) {
		alert( "Se ha producido un error inexperado: " + e );
	}
	return false;									
}