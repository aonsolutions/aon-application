package com.code.aon.ui.groupware.controller;

import javax.faces.context.FacesContext;

public class ErrorMessageController {

	public boolean isVisible() {
		return FacesContext.getCurrentInstance().getMessages().hasNext();
	}
}
