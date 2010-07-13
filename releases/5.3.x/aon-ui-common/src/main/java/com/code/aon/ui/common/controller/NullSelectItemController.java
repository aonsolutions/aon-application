package com.code.aon.ui.common.controller;

import javax.faces.model.SelectItem;

/**
 * @author ecastellano
 *
 */
public class NullSelectItemController {

	/**
	 * @return SelectItem
	 */
	public SelectItem getValue() {
		return new SelectItem("", " ", "", true);
	}
}