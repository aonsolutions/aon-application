package com.code.aon.ui.purchase.event;

import com.code.aon.purchase.Proposal;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.purchase.controller.IPurchaseConstants;

public class ProposalControllerListener extends ControllerAdapter implements IPurchaseConstants {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		setCurrentScope();
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		setCurrentScope();
	}
	
	private void setCurrentScope() {
		Proposal proposal = (Proposal) this.getController().getTo();
		proposal.setScope(proposal.getWorkPlace().getScope());
	}
	
}