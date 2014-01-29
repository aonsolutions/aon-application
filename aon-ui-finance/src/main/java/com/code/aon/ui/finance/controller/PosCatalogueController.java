package com.code.aon.ui.finance.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.config.Catalogue;
import com.code.aon.finance.PosCatalogue;
import com.code.aon.ui.form.LinesController;

public class PosCatalogueController extends LinesController {

	public void onCatalogueChanged(ValueChangeEvent event) {
		PosCatalogue posCatalogue = (PosCatalogue)this.getTo();
		posCatalogue.setCatalogue((Catalogue)event.getNewValue());
	}

}
