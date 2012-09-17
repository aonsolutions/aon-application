package com.esferalia.aon.ui.payroll.controller.batch;

import org.slf4j.Logger;

import com.code.aon.faces.controller.AttachmentController;

public abstract class BatchAttachController extends AttachmentController {
	
	private boolean show;
	
	public BatchAttachController() {
		this.show = true;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}
	
	protected abstract Logger getLogger() ;
	
	protected abstract String getQuery() ;
	
}
