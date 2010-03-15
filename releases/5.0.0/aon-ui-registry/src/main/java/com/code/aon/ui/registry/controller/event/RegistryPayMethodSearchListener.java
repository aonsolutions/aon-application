package com.code.aon.ui.registry.controller.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;

public class RegistryPayMethodSearchListener extends RegistrySearchListener {

	private static final PayMethod EMPTY_PAYMETHOD = new PayMethod();
	
	private List<PayMethod> payMethods;
	
	public List<PayMethod> getPayMethods() {
		if (payMethods == null) {
			payMethods = new LinkedList<PayMethod>();
			payMethods.add(EMPTY_PAYMETHOD);
		}
		return payMethods;
	}

	public void setPayMethods(List<PayMethod> payMethods) {
		this.payMethods = payMethods;
	}
	
	public int getPayMethodsSize() {
		return this.payMethods.size();
	}	

	public List<Integer> getPayMethodsIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for(PayMethod payMethod : getPayMethods()) {
			if ((payMethod != null) && (payMethod.getId() != null)) {
				ids.add(payMethod.getId());
			}
		}
		return ids;
	}
	
	public PayMethod getEmptyPayMethod() {
		return EMPTY_PAYMETHOD;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setPayMethods(new LinkedList<PayMethod>());
		getPayMethods().add(EMPTY_PAYMETHOD);
		super.init();
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		String payMethod = resolveAlias("payMethods_payment_id");
		addEnumToCriteria(criteria, payMethod, getPayMethodsIds().toArray());
		super.completeCriteria();
	}
	
	public void onAddPayMethod(ActionEvent event) {
		getPayMethods().add(EMPTY_PAYMETHOD);
	}
	
	public void onRemovePayMethod(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		getPayMethods().remove(index);
		if (getPayMethods().isEmpty()) {
			getPayMethods().add(EMPTY_PAYMETHOD);
		}
	}
	
}