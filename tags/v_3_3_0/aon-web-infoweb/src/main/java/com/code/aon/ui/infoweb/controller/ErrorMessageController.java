package com.code.aon.ui.infoweb.controller;

import javax.faces.context.FacesContext;

public class ErrorMessageController {

	public boolean isVisible() {
		return FacesContext.getCurrentInstance().getMessages().hasNext();
	}
}
