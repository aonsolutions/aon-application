package com.code.aon.ui.product.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class CatalogueItemController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void productData(LookupChangeEvent event) throws ManagerBeanException {
		CatalogueItem catalogueItem = (CatalogueItem)getTo();
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Product product = (Product)event.getNewValue();
			catalogueItem.setProduct(product);
			catalogueItem.setItem(product.getUniqueItem());
		} else {
			catalogueItem.setItem(null);
		}
	}

	public List<SelectItem> getItemList() throws ManagerBeanException {
		CatalogueItem catalogueItem = (CatalogueItem)getTo();
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		if (catalogueItem != null && catalogueItem.getProduct() != null && catalogueItem.getProduct().getId() != null) {
			for (ITransferObject ito : catalogueItem.getProduct().getItemList()) {
				Item item = (Item)ito;
				SelectItem selectItem = new SelectItem(item, item.getFullDetails());
				itemList.add(selectItem);
			}
		}
		return itemList;
	}

}