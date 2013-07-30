package com.code.aon.ui.purchase.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class ProposalDetailController extends LinesController implements IPurchaseConstants {
	
	public void onLoadPurchase(ActionEvent event) throws ManagerBeanException {
		ProposalDetail proposalDetail = (ProposalDetail)this.getModel().getRowData();
		BasicController purchaseController = (BasicController)AonUtil.getRegisteredBean(IPurchaseConstants.PURCHASE_CONTROLLER_NAME);
		purchaseController.onLoad(event, proposalDetail.getPurchaseDetail().getPurchase().getId(), "proposal_form", PROPOSAL_DETAIL_CONTROLLER_NAME + ".onBackProposal");
	}
	
	public void onBackProposal(ActionEvent event) throws ManagerBeanException {
		ProposalController proposalController = (ProposalController) AonUtil.getRegisteredBean(PROPOSAL_CONTROLLER_NAME);
		proposalController.refresh(event);
		onSearch(event);
	}
	
	public void onItemChanged(LookupChangeEvent event) throws ManagerBeanException {
		ProposalDetail proposalDetail = (ProposalDetail) getTo();
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Item item = (Item)event.getNewValue();
			proposalDetail.setPrice(item.getPrice());
		}
	}
	
}
