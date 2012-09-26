package com.code.aon.ui.purchase.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.purchase.Proposal;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.purchase.enumeration.ProposalTransferStatus;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.purchase.controller.ProposalController;
import com.code.aon.ui.purchase.controller.ProposalController.ProposalType;
import com.code.aon.ui.util.AonUtil;

public class ProposalControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		setCurrentScope();
		if(((ProposalController)getController()).getProposalType()==ProposalType.ITEM_RETURN){
			Proposal to = (Proposal) getController().getTo();
			to.setItemReturn(true);
			to.setTransferStatus(ProposalTransferStatus.NO_TRANSFER);
		}
		if(((ProposalController)getController()).getProposalType()==ProposalType.TRANSFER){
			Proposal to = (Proposal) getController().getTo();
			to.setItemReturn(true);
			to.setTransferStatus(ProposalTransferStatus.TRANSFER_PENDING);
		}
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
		ProposalController proposalController = (ProposalController)getController();
		((Proposal)proposalController.getTo()).setIssueDate(new Date());
		((Proposal)proposalController.getTo()).setStatus(ProposalStatus.PENDING);
		proposalController.init();
		CompanyCollectionsController c = (CompanyCollectionsController) AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		try {
			if(!c.getCurrentUserWorkPlaces().isEmpty()){
				((Proposal)getController().getTo()).setWorkPlace((WorkPlace) c.getCurrentUserWorkPlaces().get(0).getValue());
				((ProposalController)getController()).setDestinationWorkPlace((WorkPlace) c.getCurrentUserWorkPlaces().get(0).getValue());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		Proposal to = (Proposal) getController().getTo();
		ProposalController controller = (ProposalController) this.getController();
		if( to.getTransferStatus()==ProposalTransferStatus.TRANSFER_PROCESSED
				&& to.isItemReturn() ){
			controller.removeProposalDetail(to.getTransferProposal());
			controller.removeProposal(to.getTransferProposal());
		}
	}
	
	
	
}