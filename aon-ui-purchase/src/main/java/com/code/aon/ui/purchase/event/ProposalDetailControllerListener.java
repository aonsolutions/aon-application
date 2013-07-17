package com.code.aon.ui.purchase.event;

import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemSupplier;
import com.code.aon.purchase.Proposal;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.purchase.enumeration.ProposalDetailStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProposalDetailControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ProposalDetail detail = (ProposalDetail) this.getController().getTo();
		detail.setSkipProposalUpdating(true);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProposalDetail detail = (ProposalDetail) this.getController().getTo();
		detail.setStatus(ProposalDetailStatus.PENDING);
		try {
			detail.setSupplier(getMorePrioritySupplier(detail.getItem()));
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener el proveedor del  producto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private Supplier getMorePrioritySupplier(Item item) throws ManagerBeanException {
		Proposal proposal = (Proposal) ((LinesController) this.getController()).getMasterController().getTo();
		Supplier supplier = null;
		IManagerBean bean = BeanManager.getManagerBean(ItemSupplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_ITEM_ID), item.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_WORK_PLACE_ID), proposal.getWorkPlace().getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_PRIORITY), true);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			supplier = ((ItemSupplier) list.get(0)).getSupplier();
		} else {
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_ITEM_ID), item.getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_PRIORITY), true);
			list = bean.getList(criteria);
			if(!list.isEmpty()){
				supplier = ((ItemSupplier) list.get(0)).getSupplier();
			}
		}
		if(supplier==null){
			String msg = "El producto no tiene un proveedor asignado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return supplier;
	}
	
}