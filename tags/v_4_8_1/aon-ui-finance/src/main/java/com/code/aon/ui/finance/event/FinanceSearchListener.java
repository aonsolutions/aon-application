package com.code.aon.ui.finance.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.RegistryBank;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.finance.controller.FinanceController;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class FinanceSearchListener extends ControllerSearchListener {

	private static final PayMethod EMPTY_PAYMETHOD = new PayMethod();
	
	private Customer customer;

	private Supplier supplier;

	private Creditor creditor;

	private RegistryBank registryBank;

	private FinanceStatus[] financeStatuses;
	
	private List<PayMethod> payMethods;
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
	public Creditor getCreditor() {
		return creditor;
	}

	public void setCreditor(Creditor creditor) {
		this.creditor = creditor;
	}
	
	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public FinanceStatus[] getFinanceStatuses() {
		return financeStatuses;
	}

	public void setFinanceStatuses(FinanceStatus[] financeStatuses) {
		this.financeStatuses = financeStatuses;
	}
	
	public List<PayMethod> getPayMethods() {
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
		for (PayMethod payMethod : getPayMethods()) {
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
		setCustomer(new Customer());
		setSupplier(new Supplier());
		setCreditor(new Creditor());
		setRegistryBank(new RegistryBank());
		FinanceStatus[] defaultFinanceStatus = {FinanceStatus.PENDING};
		setFinanceStatuses(defaultFinanceStatus);
		setPayMethods(new LinkedList<PayMethod>());
		getPayMethods().add(EMPTY_PAYMETHOD);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		criteria.addEqualExpression(getController().getFieldName(IFinanceAlias.FINANCE_PAYMENT), ((FinanceController)getController()).isPayment());
		if ((getCustomer() != null) && (getCustomer().getId() != null)) {
			criteria.addEqualExpression(getController().getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID), getCustomer().getId());			
		}
		if ((getSupplier() != null) && (getSupplier().getId() != null)) {
			criteria.addEqualExpression(getController().getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID), getSupplier().getId());			
		}			
		if ((getCreditor() != null) && (getCreditor().getId() != null)) {
			criteria.addEqualExpression(getController().getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID), getCreditor().getId());			
		}			
		if ((getRegistryBank() != null) && (getRegistryBank().getId() != null)) {
			criteria.addEqualExpression(getController().getFieldName(IFinanceAlias.FINANCE_BANK_ACCOUNT), getRegistryBank().getBankAccount());			
		}
		if (!ArrayUtils.isEmpty(getFinanceStatuses())) {
			String status = getController().resolveAlias(IFinanceAlias.FINANCE_FINANCE_STATUS);
			addEnumToCriteria(criteria, status, getFinanceStatuses());
		}
		if (getPayMethods() != null && getPayMethodsSize() > 0) {
			String payMethod = getController().resolveAlias(IFinanceAlias.FINANCE_PAY_METHOD_ID);
			addEnumToCriteria(criteria, payMethod, getPayMethodsIds().toArray());
		}
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