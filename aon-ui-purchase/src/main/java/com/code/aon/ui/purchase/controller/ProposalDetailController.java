package com.code.aon.ui.purchase.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import org.ajax4jsf.component.html.HtmlAjaxCommandButton;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.purchase.Proposal;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryItemStatus;
import com.code.aon.registry.enumeration.RegistryMode;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProposalDetailController extends LinesController implements IPurchaseConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void onAccept(ActionEvent event) {
		boolean reset = isNevv();
		super.onAccept(event);
		if(reset){
			super.onReset(event);
		}
		try  {
			((HtmlAjaxCommandButton) event.getSource()).setFocus("Item_id-New");
		} catch(Exception e){
			// continue without focus...
		}
	}
	
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
			RegistryItem rItem = getHighestPriorityRegistryItem((Item)event.getNewValue());
			if (rItem!=null && rItem.getId()!=null) {
				proposalDetail.setPrice(rItem.getPrice());
				proposalDetail.setSupplier((Supplier)BeanManager.getManagerBean(Supplier.class).get(rItem.getRegistry().getId()));
			}
		}
	}
	
	private RegistryItem getHighestPriorityRegistryItem(Item item) throws ManagerBeanException {
		Proposal proposal = (Proposal) getMasterController().getTo();
		RegistryItem rItem = null;
		IManagerBean bean = BeanManager.getManagerBean(RegistryItem.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), item.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_STATUS), RegistryItemStatus.ACTIVE);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
		Expression wpExpr = ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_WORK_PLACE_ID), proposal.getWorkPlace().getId());
		Expression wpNullExpr = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_WORK_PLACE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(wpExpr, wpNullExpr));
		criteria.addOrder(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_WORK_PLACE), false);
		criteria.addOrder(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY));
		List<ITransferObject> list = bean.getList(criteria);
		if (!list.isEmpty()) {
			rItem = (RegistryItem)list.get(0);
		}
		if(rItem==null){
			String msg = "El producto no tiene ningún proveedor asignado";
			AonUtil.addErrorMessage(msg);
		}
		return rItem;
	}
	
	
	public void onRefresh(ActionEvent event) {
		initializeModel();
	}
}
