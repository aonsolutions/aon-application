package com.esferalia.aon.ui.payroll.controller.contract;

import com.code.aon.faces.controller.AttachmentController;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;

public class ContractAttachController extends AttachmentController {
	
	private ContractAttachmentType type;
		
	private boolean show;
	
	public ContractAttachController() {
		this.show = true;
	}

	public ContractAttachmentType getType() {
		return type;
	}
	
	public void setType(ContractAttachmentType type) {
		this.type = type;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}
	
}
