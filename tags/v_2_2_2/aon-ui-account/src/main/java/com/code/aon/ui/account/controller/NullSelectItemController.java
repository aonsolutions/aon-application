package com.code.aon.ui.account.controller;

import javax.faces.model.SelectItem;

public class NullSelectItemController {

	public SelectItem getValue() {
		return new SelectItem("", " ", "", true);
	}
}