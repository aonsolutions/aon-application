package com.code.aon.ui.purchase.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemSupplier;
import com.code.aon.purchase.Proposal;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

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
			ItemSupplier is = getMorePriorityItemSupplier((Item)event.getNewValue());
			if(is!=null && is.getId()!=null){
				proposalDetail.setPrice(is.getPrice());
				proposalDetail.setSupplier(is.getSupplier());
			}
		}
	}
	
	private ItemSupplier getMorePriorityItemSupplier(Item item) throws ManagerBeanException {
		Proposal proposal = (Proposal) getMasterController().getTo();
		ItemSupplier itemSupplier = null;
		IManagerBean bean = BeanManager.getManagerBean(ItemSupplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_ITEM_ID), item.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_WORK_PLACE_ID), proposal.getWorkPlace().getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_PRIORITY), true);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			itemSupplier = (ItemSupplier) list.get(0);
		} else {
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_ITEM_ID), item.getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_PRIORITY), true);
			list = bean.getList(criteria);
			if(!list.isEmpty()){
				itemSupplier = (ItemSupplier) list.get(0);
			}
		}
		if(itemSupplier==null){
			String msg = "El producto no tiene ningún proveedor asignado";
			AonUtil.addErrorMessage(msg);
		}
		return itemSupplier;
	}
	
}
