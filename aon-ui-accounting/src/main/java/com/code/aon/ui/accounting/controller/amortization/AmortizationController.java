package com.code.aon.ui.accounting.controller.amortization;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.model.impl.ListDataModel;

import com.code.aon.account.Account;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationInvoice;
import com.code.aon.accounting.amortization.AmortizationManager;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * @author tyrion
 *
 */
public class AmortizationController extends BasicController {

	private DataModel investmentInvoices;
	private List<Invoice> checkedInvestmentInvoices;
	private List<ITransferObject> invoices;
	private Integer invoiceId;
	
	private boolean salePanelVisible;
	private String selectedTab;
	
	private boolean addInvoicePanelVisible;

	public boolean isSalePanelVisible() {
		return salePanelVisible;
	}
	public void setSalePanelVisible(boolean salePanelVisible) {
		this.salePanelVisible = salePanelVisible;
	}
	public boolean isAddInvoicePanelVisible() {
		return addInvoicePanelVisible;
	}
	public void setAddInvoicePanelVisible(boolean addInvoicePanelVisible) {
		this.addInvoicePanelVisible = addInvoicePanelVisible;
	}
	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public boolean isUpdatable() {
		try {
			if (isNew()) {
				return true;
			}
			AmortizationDetailController adc = (AmortizationDetailController) AonUtil.getRegisteredBean(IAccountingConstants.AMORTIZATION_DETAIL_CONTROLLER);
			return adc.hasScoredOrBlockedDetails();
		} catch (ManagerBeanException e) {
			return true;
		}
	}

	public List<SelectItem> getFixedAssetAccounts() {
		try {
			Amortization am = (Amortization) getTo();
			Account a = am.getAmortizationType().getFixedAssetAccount();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), a.getCode() + IAccountingConstants.ASTERISK);
			criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED), true);
			return getAccounts(criteria);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar la lista de cuentas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ExpressionException e) {
			String msg = "Imposible cargar la lista de cuentas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public List<SelectItem> getAccumulatedAccounts() {
		try {
			Amortization am = (Amortization) getTo();
			Account a = am.getAmortizationType().getAccumulatedAccount();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), a.getCode() + IAccountingConstants.ASTERISK);
			criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED), true);
			return getAccounts(criteria);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar la lista de cuentas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ExpressionException e) {
			String msg = "Imposible cargar la lista de cuentas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public List<SelectItem> getAllocationAccounts() {
		try {
			Amortization am = (Amortization) getTo();
			Account a = am.getAmortizationType().getAllocationAccount();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), a.getCode() + IAccountingConstants.ASTERISK);
			criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED), true);
			return getAccounts(criteria);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar la lista de cuentas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ExpressionException e) {
			String msg = "Imposible cargar la lista de cuentas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	private List<SelectItem> getAccounts(Criteria criteria) throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			list.add(item);
		}
		return list;
	}

	public void onSale(ActionEvent event) {
		try {
			Amortization a = (Amortization) getTo();
			if (a.getDeadline() == null && a.getSaleAmount() != null) {
				String msg = "Debe indicar una fecha de baja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (a.getDeadline() != null && a.getSaleAmount() == null) {
				String msg = "Debe indicar una importe de venta.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			accept(event);
			if (a.getDeadline() != null) {
				AmortizationManager am = new AmortizationManager();
				am.checkSale(a);
				am.sale(a);
				AmortizationDetailController ad = (AmortizationDetailController) AonUtil.getRegisteredBean(IAccountingConstants.AMORTIZATION_DETAIL_CONTROLLER);
				ad.initModel();
				ad.onSearch(event);
			} else {
				onCalculate(event);			
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible realizar la cancelación de la ficha. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void onCalculate(ActionEvent event) {
		try {
			accept(event);
			Amortization a = (Amortization) getTo();
			AmortizationManager am = new AmortizationManager();
			am.generateDetails(a);
			AmortizationDetailController ad = (AmortizationDetailController) AonUtil.getRegisteredBean(IAccountingConstants.AMORTIZATION_DETAIL_CONTROLLER);
			ad.initModel();
			ad.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible realizar el cálculo de la ficha. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}

	}
	
	public boolean isFixedAssetAccountSynchronizable() {
		Amortization to = (Amortization) getTo();
		return isAccountSynchronizable(to.getFixedAssetAccount(), IAccountingConstants.EMPTY);
	}
	public boolean isAccumulatedAccountSynchronizable() {
		Amortization to = (Amortization) getTo();
		return isAccountSynchronizable(to.getAccumulatedAccount(), IAccountingConstants.ACCUMULATED_ACCOUNT_PREFIX);
	}
	public boolean isAllocationAccountSynchronizable() {
		Amortization to = (Amortization) getTo();
		return isAccountSynchronizable(to.getAllocationAccount(), IAccountingConstants.ALLOCATION_ACCOUNT_PREFIX);
	}
	public boolean isAccountSynchronizable(Account account, String prefix) {
		Amortization to = (Amortization) getTo();
		if (account == null || account.getId() == null ||
			StringUtils.equals(prefix + to.getDescription(), account.getDescription())) {
			return false;
		}
		return true;
	}
		
	public void onFixedAssetAccountSynchronize(ActionEvent event) {
		Amortization to = (Amortization) getTo();
		to.setFixedAssetAccount( onAccountSynchronize(to.getFixedAssetAccount(), IAccountingConstants.EMPTY));
	}
	public void onAccumulatedAccountSynchronize(ActionEvent event) {
		Amortization to = (Amortization) getTo();
		to.setAccumulatedAccount( onAccountSynchronize(to.getAccumulatedAccount(),IAccountingConstants.ACCUMULATED_ACCOUNT_PREFIX));
	}
	public void onAllocationAccountSynchronize(ActionEvent event) {
		Amortization to = (Amortization) getTo();
		to.setAllocationAccount( onAccountSynchronize(to.getAllocationAccount(),IAccountingConstants.ALLOCATION_ACCOUNT_PREFIX));
	}
	public Account onAccountSynchronize(Account account,String prefix) {
		try {
			Amortization to = (Amortization) getTo();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			account.setDescription(prefix + to.getDescription());
			return (Account) accountBean.update(account);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo sincronizar una cuenta contable. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}		
	}
	
	public List<ITransferObject> getInvoices() {
		if (invoices == null) {
			resetInvoices();
		}
		return invoices;
	}
	public void setInvoices(List<ITransferObject> invoices) {
		this.invoices = invoices;
	}
	
	public Integer getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}
	
	public void resetInvoices() {
		setInvestmentInvoices(null);
		setInvoices(null);
		try {
			Amortization am = (Amortization) getTo();
			IManagerBean bean = BeanManager.getManagerBean(AmortizationInvoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AMORTIZATION_INVOICE_AMORTIZATION_ID), am.getId());
			List<ITransferObject> list = bean.getList(criteria);
			setInvoices(list);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo determinar las facturas vinculadas. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			setInvoices(new LinkedList<ITransferObject>() );
			throw new AbortProcessingException(msg, e);
		}		
	}
	public boolean isInvoiceNavigationDisabled() {
		return ( getBackAction() != null);
	}
	
	private AmortizationInvoice getAmortizationInvoice(Integer id) {
		for (ITransferObject to :  getInvoices() ) {
			AmortizationInvoice inv = (AmortizationInvoice) to;
			if (ObjectUtils.equals(getInvoiceId(), inv.getId())) {
				return inv;
			}
		}
		return null;
	}
	
	public String showInvoice() {
		try {
			String invoiceViewer = null;
			AmortizationInvoice ai = getAmortizationInvoice(getInvoiceId());
			if (ai == null) {
				String message = "Imposible encontrar laa factura entre las listadas.";
				AonUtil.addErrorMessage(message);
				throw new AbortProcessingException(message);
			}
			Invoice invoice = ai.getInvoice();
			String invoiceControllerName = "";
			if (invoice.getType() == InvoiceType.SALES) {
				invoiceControllerName = IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME;
				invoiceViewer = IFinanceConstants.SALE_INVOICE_FORM_NAME;
			} else if (invoice.getType() == InvoiceType.PURCHASE) {
				invoiceControllerName = IFinanceConstants.PURCHASE_INVOICE_CONTROLLER_NAME;
				invoiceViewer = IFinanceConstants.PURCHASE_INVOICE_FORM_NAME;
			} else if (invoice.getType() == InvoiceType.EXPENSES) {
				invoiceControllerName = IFinanceConstants.EXPENSE_INVOICE_CONTROLLER_NAME;
				invoiceViewer = IFinanceConstants.EXPENSE_INVOICE_FORM_NAME;
			} else if (invoice.getType() == InvoiceType.UNDEDUCTIBLE) {
				invoiceControllerName = IFinanceConstants.UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME;
				invoiceViewer = IFinanceConstants.UNDEDUCTIBLE_INVOICE_FORM_NAME;
			}
			InvoiceController invoiceController = (InvoiceController) AonUtil.getRegisteredBean(invoiceControllerName);
			invoiceController.onLoad(null, invoice.getId(), IAccountingConstants.AMORTIZATION_FORM_NAVKEY, null);
			return invoiceViewer;
		} catch (ManagerBeanException e) {
			String message = "Imposible navegar a la factura";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}
	
	public void onRemoveInvoice(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(AmortizationInvoice.class);
			ITransferObject to = bean.get(getInvoiceId());
			bean.remove(to);
			resetInvoices();
		} catch (ManagerBeanException e) {
			String message = "No se pudo borrar el vínculo entre la ficha de amortización y la factura.";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}

	public void setInvestmentInvoices(DataModel model) {
		investmentInvoices = model;
		setCheckedInvestmentInvoices(null);
	}
	
	public DataModel getInvestmentInvoices() {
		try {
			if (investmentInvoices == null) {
				IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
				Criteria criteria = new Criteria();
				Amortization am = (Amortization) getTo();
				Date startDate = am.getInitialDate();
				Calendar c = Calendar.getInstance();
				c.setTime(startDate);
				c.set(Calendar.DAY_OF_MONTH, 1);
				c.set(Calendar.MONTH, 0);
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), c.getTime());
				List<InvoiceType> types = new LinkedList<InvoiceType>();
				types.add(InvoiceType.EXPENSES);
				types.add(InvoiceType.PURCHASE);
				criteria.addInExpression(bean.getFieldName(IEntityAlias.INVOICE_TYPE), types);
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_INVESTMENT), true);
				List<ITransferObject> list = bean.getList(criteria);
				setInvestmentInvoices(new ListDataModel(list));
			}
			return investmentInvoices;
		} catch (ManagerBeanException e) {
			String message = "No se pudo mostrar las facturas de inversión.";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}
	public List<Invoice> getCheckedInvestmentInvoices() {
		return checkedInvestmentInvoices;
	}
	public void setCheckedInvestmentInvoices(List<Invoice> checkedInvestmentInvoices) {
		this.checkedInvestmentInvoices = checkedInvestmentInvoices;
	}
	public boolean isCheckedInvestmentInvoice() {
		Invoice invoice = (Invoice) getInvestmentInvoices().getRowData();
		return (getCheckedInvestmentInvoices().contains(invoice));
	}
	public void setCheckedInvestmentInvoice(boolean checked) {
		Invoice invoice = (Invoice) getInvestmentInvoices().getRowData();
		if (checked) {
			getCheckedInvestmentInvoices().remove(invoice);	
		} else {
			getCheckedInvestmentInvoices().add(invoice);
		}
	}
	public void onAddInvoice(ActionEvent event) {
		//TODO add
		setInvestmentInvoices(null);
		setCheckedInvestmentInvoices(null);		
	}
	
}
