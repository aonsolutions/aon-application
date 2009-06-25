package com.code.aon.ui.finance.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;

public class InvoiceSearchListener extends RegistrySearchListener {
	
	private static final PayMethod EMPTY_PAYMETHOD = new PayMethod();
	
	private String defaultType;
	
	private String defaultStatus;

	private Registry registry;
	
    private Item item;

	private Bank bank;
    
	private FinanceStatus[] financeStatuses;
	
	private List<PayMethod> payMethods;
	
	public String getDefaultType() {
		return defaultType;
	}

	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}
	
	public String getDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(String defaultStatus) {
		this.defaultStatus = defaultStatus;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
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
		super.init();
		setRegistry(new Registry());
		setItem(new Item());
		getItem().setProduct(new Product());
		setBank(new Bank());
		setFinanceStatuses(new FinanceStatus[0]);
		setPayMethods(new LinkedList<PayMethod>());
		getPayMethods().add(EMPTY_PAYMETHOD);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		super.completeCriteria();
		Criteria criteria = getController().getCriteria();
		if (getDefaultType() != null) {
			InvoiceType type = InvoiceType.valueOf(getDefaultType()); 
			criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_TYPE), type);	
		}
		if (getDefaultStatus() != null) {
			InvoiceStatus status = InvoiceStatus.valueOf(getDefaultStatus()); 
			criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_STATUS), status);	
		}
		if ((getRegistry() != null) && (getRegistry().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), getRegistry().getId());
		}		
		if ((getItem() != null) && (getItem().getId() != null)) {
			criteria.addEqualExpression("Invoice.lines.item.id", getItem().getId());
		}		
		if ((getBank() != null) && (!StringUtils.isEmpty(getBank().getCode()))) {
			criteria.addEqualExpression("Invoice.finances.bank.code", getBank().getCode());			
		}		
		if (!ArrayUtils.isEmpty(getFinanceStatuses())) {
			String status = getController().resolveAlias("Invoice.finances.financeStatus");
			addEnumToCriteria(criteria, status, getFinanceStatuses());
		}
		if (getPayMethods() != null && getPayMethodsSize() > 0) {
			String payMethod = getController().resolveAlias("Invoice.finances.payMethod.id");
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