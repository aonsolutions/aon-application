package com.code.aon.ui.config.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.config.Catalogue;
import com.code.aon.config.TariffCatalogue;
import com.code.aon.ui.form.LinesController;

public class TariffCatalogueController extends LinesController {

	public void onCatalogueChanged(ValueChangeEvent event) {
		TariffCatalogue tariffCatalogue = (TariffCatalogue)this.getTo();
		tariffCatalogue.setCatalogue((Catalogue)event.getNewValue());
	}

}