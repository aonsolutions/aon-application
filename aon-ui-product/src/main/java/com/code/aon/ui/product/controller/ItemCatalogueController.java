package com.code.aon.ui.product.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.config.Catalogue;
import com.code.aon.product.CatalogueItem;
import com.code.aon.ui.form.LinesController;

public class ItemCatalogueController extends LinesController {

	public void onCatalogueChanged(ValueChangeEvent event) {
		CatalogueItem catalogueItem = (CatalogueItem)this.getTo();
		catalogueItem.setCatalogue((Catalogue)event.getNewValue());
	}

}