package com.code.aon.ui.purchase.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Department;
import com.code.aon.company.WorkplaceDepartment;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.ItemSupplier;
import com.code.aon.purchase.Proposal;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProposalController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProposalController.class);
	
	private IControllerListener departmentItemFilter;
	
	public List<SelectItem> getDepartments() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		try {
			List<Integer> deptartmentIds = new LinkedList<Integer>();
			if(getTo()!=null){
				for (ITransferObject ito : getWorkplaceDepartments()) {
					WorkplaceDepartment wd = (WorkplaceDepartment)ito;
					deptartmentIds.add(wd.getDepartment().getId());
				}
				if(!deptartmentIds.isEmpty()){
					IManagerBean dBean = BeanManager.getManagerBean(Department.class);
					Criteria dCriteria = new Criteria();
					dCriteria.addInExpression(dBean.getFieldName(IEntityAlias.DEPARTMENT_ID), deptartmentIds);
					dCriteria.addOrder(dBean.getFieldName(IEntityAlias.DEPARTMENT_NAME));
					for (ITransferObject ito : dBean.getList(dCriteria)) {
						Department d = (Department)ito;
						SelectItem item = new SelectItem(d, d.getName());
						list.add(item);
					}
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los departamentos";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return list;
	}
	
	private List<ITransferObject> getWorkplaceDepartments() throws ManagerBeanException {
		IManagerBean wdBean = BeanManager.getManagerBean(WorkplaceDepartment.class);
		Criteria wdCriteria = new Criteria();
		wdCriteria.addEqualExpression(wdBean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WORK_PLACE_ID), ((Proposal)getTo()).getWorkPlace().getId());
		wdCriteria.addEqualExpression(wdBean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_ACTIVE), Boolean.TRUE);
		return wdBean.getList(wdCriteria);
	}
	
	public List<Integer> getDepartmentsItemIds() throws ManagerBeanException{
		List<Integer> list = new LinkedList<Integer>();
		Proposal proposal = ((Proposal)getTo()); 
		if(proposal.getDepartment()!=null){
			List<Integer> catalogueIds = new LinkedList<Integer>();
			for(ITransferObject to: getWorkplaceDepartments(proposal)){
				WorkplaceDepartment wd = (WorkplaceDepartment) to;
				catalogueIds.add(wd.getCatalogue().getId());
			}
			IManagerBean catalogueItemBean = BeanManager.getManagerBean(CatalogueItem.class);
			Criteria deptCriteria = new Criteria();
			deptCriteria.addInExpression(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID), catalogueIds);
			List<Integer> itemIds = new LinkedList<Integer>();
			for (ITransferObject ito : catalogueItemBean.getList(deptCriteria)) {
				itemIds.add(((CatalogueItem)ito).getItem().getId());
			}
			IManagerBean itemSupplierBean = BeanManager.getManagerBean(ItemSupplier.class);
			Criteria itemSupCriteria = new Criteria();
			itemSupCriteria.addInExpression(itemSupplierBean.getFieldName(IEntityAlias.ITEM_SUPPLIER_ITEM_ID), itemIds);
			itemSupCriteria.addNotNullExpression(itemSupplierBean.getFieldName(IEntityAlias.ITEM_SUPPLIER_SUPPLIER_ID));

			for (ITransferObject ito : itemSupplierBean.getList(itemSupCriteria)) {
				ItemSupplier is = (ItemSupplier) ito;
				if( is.getWorkPlace() == null || is.getWorkPlace().getId() == null || is.getWorkPlace().getId().equals(proposal.getWorkPlace().getId()) ){
					list.add(is.getItem().getId());
				}
			}
		}
		if(list.isEmpty()){
			list.add(-1);
		}
		return list;
	}
	
	public IControllerListener getDepartmentItemFilter() {
		if ( this.departmentItemFilter == null ) {
			this.departmentItemFilter = new ControllerAdapter() {
				@Override
				public void beforeModelSearched(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						controller.getCriteria().addInExpression(controller.getFieldName(IEntityAlias.ITEM_ID), getDepartmentsItemIds());
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering items", e);
					}
				}
			};
		}
		return this.departmentItemFilter;
	}
	
	private List<ITransferObject> getWorkplaceDepartments(Proposal proposal){
		try {
			IManagerBean bean = BeanManager.getManagerBean(WorkplaceDepartment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WORK_PLACE_ID), proposal.getWorkPlace().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_DEPARTMENT_ID), proposal.getDepartment().getId());
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los productos";
			AonUtil.addErrorMessage(msg);
		}
		return null;
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
