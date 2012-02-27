package com.code.aon.ui.purchase.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.purchase.Proposal;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

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
		CompanyCollectionsController c = (CompanyCollectionsController) AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		try {
			if(!c.getCurrentUserWorkPlaces().isEmpty()){
				((Proposal)getController().getTo()).setWorkPlace((WorkPlace) c.getCurrentUserWorkPlaces().get(0).getValue());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
}