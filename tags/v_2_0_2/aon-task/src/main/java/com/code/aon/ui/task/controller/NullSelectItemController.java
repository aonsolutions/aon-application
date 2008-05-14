package com.code.aon.ui.task.controller;

import javax.faces.model.SelectItem;

public class NullSelectItemController {

	private static final SelectItem NULL_SELECT_ITEM = new SelectItem(null, " ");
	
	public SelectItem getValue() {
		return NULL_SELECT_ITEM;
	}

}
