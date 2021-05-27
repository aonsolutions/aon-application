package com.code.aon.ui.registry.controller.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;

public class RegistryPayMethodSearchListener extends RegistrySearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final PayMethod EMPTY_PAYMETHOD = new PayMethod();
	
	private PayMethod[] payMethods;
	
	public PayMethod[] getPayMethods() {
		if (payMethods == null) {
			payMethods = new PayMethod[]{EMPTY_PAYMETHOD};
		}
		return payMethods;		
	}

	public void setPayMethods(PayMethod[] payMethods) {
		this.payMethods = payMethods;
	}
	
	public int getPayMethodsSize() {
		return ArrayUtils.getLength(payMethods);
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
		setPayMethods(new PayMethod[]{EMPTY_PAYMETHOD});
		super.init();
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		String payMethod = resolveAlias("payMethods_payment_id");
		addEnumToCriteria(criteria, payMethod, getPayMethodsIds().toArray());
		super.completeCriteria( criteria );
	}
	
	public void onAddPayMethod(ActionEvent event) {
		this.payMethods = (PayMethod[]) ArrayUtils.add(this.payMethods, EMPTY_PAYMETHOD);
	}
	
	public void onRemovePayMethod(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.payMethods = (PayMethod[]) ArrayUtils.remove(this.payMethods, index);
		if ( ArrayUtils.isEmpty(this.payMethods) ) {
			setPayMethods(new PayMethod[]{EMPTY_PAYMETHOD});
		}		
	}
	
}