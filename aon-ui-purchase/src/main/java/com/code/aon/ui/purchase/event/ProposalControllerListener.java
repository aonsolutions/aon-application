package com.code.aon.ui.purchase.event;

import java.util.Date;

import com.code.aon.purchase.Proposal;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ProposalControllerListener extends ControllerAdapter {

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
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		((Proposal)getController().getTo()).setIssueDate(new Date());
		((Proposal)getController().getTo()).setStatus(ProposalStatus.PENDING);
	}
	
}