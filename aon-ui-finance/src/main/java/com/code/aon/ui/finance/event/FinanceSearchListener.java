package com.code.aon.ui.finance.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.finance.InvoiceExportType;
import com.code.aon.ui.finance.controller.ExporterController;
import com.code.aon.ui.finance.controller.FinanceCollectionsController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.IFinanceController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceSearchListener extends FinanceListSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final PayMethod EMPTY_PAYMETHOD = new PayMethod();
	
	private Registry registry;
	private RegistryBank registryBank;
	private PayMethod[] payMethods;
	private boolean skipPayrollFilter;
	private boolean nullInvoice;
	private boolean exportMode;
	
	public Registry getRegistry() {
		return registry;
	}
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
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
	
	public boolean isSkipPayrollFilter() {
		return skipPayrollFilter;
	}
	public void setSkipPayrollFilter(boolean skipPayrollFilter) {
		this.skipPayrollFilter = skipPayrollFilter;
	}

	public boolean isNullInvoice() {
		return nullInvoice;
	}
	public void setNullInvoice(boolean nullInvoice) {
		this.nullInvoice = nullInvoice;
	}

	public boolean isExportMode() {
		return exportMode;
	}
	public void setExportMode(boolean exportMode) {
		this.exportMode = exportMode;
	}

	private IFinanceController getFinanceController() {
		return (IFinanceController)getController();
	}

	@Override
	protected void init() throws ManagerBeanException {
		initData();

		FinanceStatus[] defaultFinanceStatus = {FinanceStatus.PENDING, FinanceStatus.RETURNED};
		setFinanceStatuses(defaultFinanceStatus);
		setExportMode(false);
	}

	public void initData() throws ManagerBeanException {
		super.init();
		setRegistry((Registry)BeanManager.getManagerBean(Registry.class).createNewTo());
		setRegistryBank((RegistryBank)BeanManager.getManagerBean(RegistryBank.class).createNewTo());
		setPayMethods(new PayMethod[]{EMPTY_PAYMETHOD});
		setSkipPayrollFilter(getFinanceController().isPayment() && !getFinanceController().isPayroll() && AonUtil.getRoleManager().isPayroll());
		setNullInvoice(false);
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_PAYMENT), getFinanceController().isPayment());
		if ((getRegistry() != null) && (getRegistry().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_REGISTRY_ID), getRegistry().getId());			
		}		
		if ((getRegistryBank() != null) && (getRegistryBank().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_BANK_ACCOUNT), getRegistryBank().getBankAccount());			
		}
		if (getPayMethods() != null && getPayMethodsSize() > 0) {
			String payMethod = getController().resolveAlias(IEntityAlias.FINANCE_PAY_METHOD_ID);
			addEnumToCriteria(criteria, payMethod, getPayMethodsIds().toArray());
		}
		if (!isSkipPayrollFilter()) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_PAYROLL), getFinanceController().isPayroll());
		}
		if (isNullInvoice()) {
			criteria.addNullExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_ID));
		}
		if (isExportMode()) {
			criteria.addNullExpression(getController().resolveAlias("Finance.batchDetails<id"));

			ExporterController exporter = (ExporterController)AonUtil.getRegisteredBean(IFinanceConstants.EXPORTER_CONTROLLER_NAME);
			if (exporter.getConfiguration().getType() == InvoiceExportType.A3) {
				criteria.addNotNullExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_ID));
			}
		}
		super.completeCriteria(criteria);
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

	public List<SelectItem> getFinanceStatusList() {
		FinanceCollectionsController collections = (FinanceCollectionsController) AonUtil.getRegisteredBean(IFinanceConstants.COLLECTIONS_CONTROLLER_NAME);
		List<SelectItem> statusList = collections.getFinanceStatuses();
		if (isExportMode()) {
			List<SelectItem> newStatusList = new LinkedList<SelectItem>();
			for (SelectItem item : statusList) {
				if (ObjectUtils.equals(item.getValue(), FinanceStatus.PAID) || ObjectUtils.equals(item.getValue(), FinanceStatus.RETURNED)) {
					newStatusList.add(item);
				}
			}
			return newStatusList;
		}
		return statusList;
	}	

}