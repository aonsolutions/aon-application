package es.code.cdr.ui.controller;

import javax.faces.context.FacesContext;

public class ErrorMessageController {

	public boolean isVisible() {
		return FacesContext.getCurrentInstance().getMessages().hasNext();
	}
}
