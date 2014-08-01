package com.code.aon.ui.finance.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.config.Catalogue;
import com.code.aon.finance.PosCatalogue;
import com.code.aon.ui.form.LinesController;

public class PosCatalogueController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onCatalogueChanged(ValueChangeEvent event) {
		PosCatalogue posCatalogue = (PosCatalogue)this.getTo();
		posCatalogue.setCatalogue((Catalogue)event.getNewValue());
	}

}
