package com.code.aon.ui.finance.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceSearchListener extends RegistrySearchListener {
	
	private static final PayMethod EMPTY_PAYMETHOD = new PayMethod();
	
	private String defaultType;
	private String defaultStatus;
	private Registry registry;
	private Project project;
    private Item item;
	private Bank bank;
	private FinanceStatus[] financeStatuses;
	private PayMethod[] payMethods;
	
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
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

	public boolean isSales() {
		if (getDefaultType() != null) {
			InvoiceType type = InvoiceType.valueOf(getDefaultType());
			return InvoiceType.SALES == type;
		}
		return false;
	}

	public boolean isPurchase() {
		if (getDefaultType() != null) {
			InvoiceType type = InvoiceType.valueOf(getDefaultType());
			return InvoiceType.PURCHASE == type;
		}
		return false;
	}

	public boolean isExpenses() {
		if (getDefaultType() != null) {
			InvoiceType type = InvoiceType.valueOf(getDefaultType());
			return InvoiceType.EXPENSES == type;
		}
		return false;
	}

	public boolean isUndeductible() {
		if (getDefaultType() != null) {
			InvoiceType type = InvoiceType.valueOf(getDefaultType());
			return InvoiceType.UNDEDUCTIBLE == type;
		}
		return false;
	}

	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setRegistry((Registry)BeanManager.getManagerBean(Registry.class).createNewTo());
		setProject((Project)BeanManager.getManagerBean(Project.class).createNewTo());
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
		setBank((Bank)BeanManager.getManagerBean(Bank.class).createNewTo());
		setFinanceStatuses(new FinanceStatus[0]);
		setPayMethods(new PayMethod[]{EMPTY_PAYMETHOD});
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		super.completeCriteria( criteria );
		if (getDefaultType() != null) {
			InvoiceType type = InvoiceType.valueOf(getDefaultType()); 
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_TYPE), type);	
		}
		if (getDefaultStatus() != null) {
			InvoiceStatus status = InvoiceStatus.valueOf(getDefaultStatus()); 
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_STATUS), status);	
		}
		if ((getRegistry() != null) && (getRegistry().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_REGISTRY_ID), getRegistry().getId());
		}		
		if ((getProject() != null) && (getProject().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_PROJECT_ID), getProject().getId());			
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