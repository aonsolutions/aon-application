package com.code.aon.ui.product.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Catalogue;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ui.form.LinesController;

public class ItemCatalogueController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onCatalogueChanged(ValueChangeEvent event) {
		CatalogueItem catalogueItem = (CatalogueItem)this.getTo();
		catalogueItem.setCatalogue((Catalogue)event.getNewValue());
	}

	public List<SelectItem> getItemList() throws ManagerBeanException {
		Product product = (Product)getMasterController().getTo();
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		for (ITransferObject ito : product.getItemList()) {
			Item item = (Item)ito;
			SelectItem selectItem = new SelectItem(item, item.getFullDetails());
			itemList.add(selectItem);
		}
		return itemList;
	}

}