package com.esferalia.aon.ui.sepe.controller.batch;

import org.slf4j.Logger;

import com.code.aon.faces.controller.AttachmentController;
import com.esferalia.aon.payroll.enumeration.PayrollBatchAttachmentType;

public abstract class SEPEBatchAttachController extends AttachmentController {
	
	private boolean show;
	private PayrollBatchAttachmentType type;
	
	public SEPEBatchAttachController() {
		this.show = true;
	}

	public PayrollBatchAttachmentType getType() {
		return type;
	}
	
	public void setType(PayrollBatchAttachmentType type) {
		this.type = type;
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
