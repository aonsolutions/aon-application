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
import com.code.aon.purchase.Proposal;
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
			IManagerBean bean = BeanManager.getManagerBean(CatalogueItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID), ((Proposal)getTo()).getWorkplaceDepartment().getCatalogue().getId());
			for (ITransferObject ito : bean.getList(criteria)) {
				CatalogueItem ci = (CatalogueItem)ito;
				SelectItem item = new SelectItem(ci.getItem(), ci.getItem().getProduct().getCode() + " - " + ci.getItem().getProduct().getName());
				list.add(item);
			}
		}
		return list;
	}
	

}
