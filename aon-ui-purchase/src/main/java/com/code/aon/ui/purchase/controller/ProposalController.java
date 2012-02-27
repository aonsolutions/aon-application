package com.code.aon.ui.purchase.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkplaceDepartment;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.ItemSupplier;
import com.code.aon.purchase.Proposal;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class ProposalController extends BasicController {
	
	public List<SelectItem> getWorkplaceDepartments() throws ManagerBeanException{
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(WorkplaceDepartment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WORK_PLACE_ID), ((Proposal)getTo()).getWorkPlace().getId());
		for (ITransferObject ito : bean.getList(criteria)) {
			WorkplaceDepartment wd = (WorkplaceDepartment)ito;
			SelectItem item = new SelectItem(wd, wd.getDepartment().getName());
			list.add(item);
		}
		return list;
	}
	
	public List<SelectItem> getDepartmentsItems() throws ManagerBeanException{
		List<SelectItem> list = new LinkedList<SelectItem>();
		if(((Proposal)getTo()).getWorkplaceDepartment()!=null){
			IManagerBean catalogueItemBean = BeanManager.getManagerBean(CatalogueItem.class);
			Criteria criteria1 = new Criteria();
			criteria1.addEqualExpression(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID), ((Proposal)getTo()).getWorkplaceDepartment().getCatalogue().getId());
			List<Integer> itemIds = new LinkedList<Integer>();
			for (ITransferObject ito : catalogueItemBean.getList(criteria1)) {
				itemIds.add(((CatalogueItem)ito).getItem().getId());
			}
			IManagerBean itemSupplierBean = BeanManager.getManagerBean(ItemSupplier.class);
			Criteria criteria2 = new Criteria();
			criteria2.addNotNullExpression(itemSupplierBean.getFieldName(IEntityAlias.ITEM_SUPPLIER_SUPPLIER_ID));
			criteria2.addInExpression(itemSupplierBean.getFieldName(IEntityAlias.ITEM_SUPPLIER_ITEM_ID), itemIds);
			for (ITransferObject ito : itemSupplierBean.getList(criteria2)) {
				ItemSupplier is = (ItemSupplier) ito;
				SelectItem item = new SelectItem(is.getItem(), is.getItem().getProduct().getCode() + " - " + is.getItem().getProduct().getName());
				list.add(item);
			}
		}
		return list;
	}
	
	public boolean isPending() {
		Proposal proposal = (Proposal) getTo();
		return proposal.getStatus()==ProposalStatus.PENDING;
	}
	
	public boolean isPartialProcessed() {
		Proposal proposal = (Proposal) getTo();
		return proposal.getStatus()==ProposalStatus.PARTIAL_PROCESSED;
	}
	
	public boolean isProcessed() {
		Proposal proposal = (Proposal) getTo();
		return proposal.getStatus()==ProposalStatus.PROCESSED;
	}

}
