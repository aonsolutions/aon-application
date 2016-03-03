package com.code.aon.ui.product.controller;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.product.Item;
import com.code.aon.product.ItemComposition;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class ItemCompositionController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean longDescription;

	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);

		ItemComposition itemComposition = (ItemComposition)getTo();
		if (StringUtils.equals(itemComposition.getCompositionItem().getFullName().trim(), itemComposition.getDescription().trim())) {
			String longDescription = itemComposition.getCompositionItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				itemComposition.setDescription(itemComposition.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public void onCompositionItemChanged(LookupChangeEvent event) {
		ItemComposition itemComposition = (ItemComposition)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			itemComposition.setCompositionItem(item);
			itemComposition.setDescription(item.getFullName());
			if (itemComposition.getQuantity() == 0) {
				itemComposition.setQuantity(1);
			}
		}
	}	

}