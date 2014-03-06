package com.code.aon.ui.purchase.event;

import com.code.aon.common.AonVersion;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.purchase.enumeration.ProposalDetailStatus;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ProposalDetailControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ProposalDetail detail = (ProposalDetail) this.getController().getTo();
		detail.setSkipProposalUpdating(true);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProposalDetail detail = (ProposalDetail) this.getController().getTo();
		detail.setStatus(ProposalDetailStatus.PENDING);
	}
	
	
}