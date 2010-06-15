package com.code.aon.ui.finance.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AccountHelper;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;

public class InvoiceRecorder implements ITransferObject {

	private static final long serialVersionUID = -6961138379394750580L;

	private static final String FINANCE_BUNDLE = "financeBundle";
	private static final String FINANCE_INACCURACY_MSG = "finance_unable_record_inaccuracy_error";
	
	private Invoice invoice;
	private boolean checked;
	private boolean recordable;
	private boolean refresh;
	private Account account;
	private boolean showTaxBreakDowns;
	private boolean showAccountEntry;
	private List<String> messages;

	private List<TaxBreakDown> taxBreakDowns;
	private List<AccountEntryDetail> details;
	private Double invoiceTotal;
	
	private IPriceStrategy priceStrategy;
	private AccountBridgeUtil accountBridgeUtil;

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public AccountBridgeUtil getAccountBridgeUtil() {
		if(accountBridgeUtil == null){
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}


	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
		refreshFlags();
	}

	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isRecordable() {
		if (isRefresh()) {
			refreshFlags();
		}
		return recordable;
	}
	public void setRecordable(boolean recordable) {
		this.recordable = recordable;
	}

	public boolean isRefresh() {
		return refresh;
	}
	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}

	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	public boolean isDateEquals() {
		boolean b = DateUtils.isSameDay(getInvoice().getIssueDate(), getInvoice().getTaxDate());
		return b;
	}

	public boolean isMessagesPresent() {
		return getMessages() != null && getMessages().size() > 0;
	}
	
	public boolean isShowTaxBreakDowns() {
		return showTaxBreakDowns;
	}
	public void setShowTaxBreakDowns(boolean showTaxBreakDowns) {
		this.showTaxBreakDowns = showTaxBreakDowns;
	}

	
	public boolean isShowAccountEntry() {
		return showAccountEntry;
	}
	public void setShowAccountEntry(boolean showAccountEntry) {
		this.showAccountEntry = showAccountEntry;
	}

	public List<AccountEntryDetail> getDetails() {
		return details;
	}
	public void setDetails(List<AccountEntryDetail> details) {
		this.details = details;
	}

	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
	public List<TaxBreakDown> getTaxBreakDowns() {
		if (isShowTaxBreakDowns() && (taxBreakDowns == null || isRefresh())) {
			taxBreakDowns = getPriceStrategy().getTaxBreakDowns(getInvoice(), getInvoice());	
		}
		return taxBreakDowns;
	}

	public void setTaxBreakDowns(List<TaxBreakDown> taxBreakDowns) {
		this.taxBreakDowns = taxBreakDowns;
	}

	public void addMessage(String msg) {
		if (this.messages == null) {
			setMessages( new LinkedList<String>()); 
		}
		getMessages().add(msg);
	}
	
	public void addFinanceInaccuracyMessage() {
		String msg = AonUtil.getMessage(FINANCE_BUNDLE, FINANCE_INACCURACY_MSG);
		addMessage(msg);
	}

	private void refreshFlags() {
		try {
			setMessages(null);
			checkFinanceInaccuracyPresent();
			InvoiceType type = getInvoice().getType();

			if ( getInvoice().isWithholding()) {
				addMessage("Factura con retenciones I.R.P.F.");
			}
			if ( getInvoice().isSurcharge()) {
				addMessage("Factura con Recargo de Equivalencia.");
			}
			if ( getInvoice().getTransaction() != InvoiceTransactionType.NATIONAL) {
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				addMessage("Factura de tipo " + getInvoice().getTransaction().getName(locale));
			}
			if ( type != InvoiceType.SALES && getInvoice().isInvestment()) {
				addMessage("Factura marcada como inversión.");
			}
			if (!isDateEquals()) {
				addMessage("Fecha de IVA diferente a fecha de factura.");
			}
			
			if (type == InvoiceType.SALES) {
				setAccount( getAccountBridgeUtil().getCustomerAccount(getInvoice().getRegistry()));	
			} else if (type == InvoiceType.PURCHASE) {
				setAccount( getAccountBridgeUtil().getSupplierAccount(getInvoice().getRegistry()));	
			} else if (type == InvoiceType.EXPENSES) {
				setAccount( getAccountBridgeUtil().getCreditorAccount(getInvoice().getRegistry()));	
			}
			if (invoice.getType() == InvoiceType.EXPENSES || invoice.getType() == InvoiceType.UNDEDUCTIBLE) {
				checkExpenseAccount();
			}
		} catch (ManagerBeanException ex) {
			addMessage("Error en el chequeo. " +  ex.getMessage());
		}
		setRefresh(false);
	}

	private void checkExpenseAccount() throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
		IManagerBean accountHelperBean = BeanManager.getManagerBean(AccountHelper.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		List<ITransferObject> list = invoiceDetailBean.getList(criteria);
		boolean wrong = false;
		for (ITransferObject to: list) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) to;
			if (invoiceDetail.getItem() != null) {
				Item item = invoiceDetail.getItem();
				if (item.getProduct().getType() == ProductType.EXPENSE) {
					Integer productId = item.getProduct().getId();
					criteria = new Criteria();
					criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_ID), productId);
					criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_TYPE), ProductAccountType.PURCHASE);
					List<ITransferObject> accounts = productAccountBean.getList(criteria);
					if (accounts == null || accounts.size() == 0) {
						wrong = true;
						addMessage("El gasto: \"" + invoiceDetail.getDescription() + "\" no tiene cuenta contable asociada.");			
					} else {
						ProductAccount acc = (ProductAccount) accounts.get(0);
						Criteria c = new Criteria();
						c.addEqualExpression(accountHelperBean.getFieldName(IAccountingAlias.ACCOUNT_HELPER_ACCOUNT_ID), acc.getAccount().getId());
						c.addOrder(accountHelperBean.getFieldName(IAccountingAlias.ACCOUNT_HELPER_COUNTER), false);
						List<ITransferObject> ahs = accountHelperBean.getList(c);
						if (ahs != null && ahs.size() > 0) {
							AccountHelper ah = (AccountHelper) ahs.get(0);
							Account balancingAccount = ah.getBalancingAccount();
							if (!getAccount().equals(balancingAccount) ) {
								addMessage("La contrapartida más usada para el gasto: \"" + invoiceDetail.getDescription() + "\" es \"" + balancingAccount.getFullDescription() +"\".");		
							}
						}
					}
					
				}
			}
		}
		if (wrong) {
			setRecordable(false);
		}
	}

	private void checkFinanceInaccuracyPresent() throws ManagerBeanException {
		setRecordable(true);
		double invoiceTotal = getInvoiceTotal();
		double financeTotal = getFinanceTotal(getInvoice());
		boolean ok = InvoiceStatus.PENDING.equals(getInvoice().getStatus()) && (financeTotal == 0 || invoiceTotal == financeTotal);
		if (!ok) {
			setRecordable(false);
			addFinanceInaccuracyMessage();
		}
		if (financeTotal == 0) {
			addMessage("Factura sin Vencimientos.");			
		}
	}

	private double getInvoiceTotal(Invoice invoice) {
		return getPriceStrategy().getTotalPrice(invoice,invoice);
	}
	public double getInvoiceTotal() {
		if (invoiceTotal == null || isRefresh()) {
			setInvoiceTotal( getInvoiceTotal(getInvoice()));
		}
		return invoiceTotal;
	}
	public void setInvoiceTotal(Double invoiceTotal) {
		this.invoiceTotal = invoiceTotal;
	}

	private double getFinanceTotal(Invoice invoice) throws ManagerBeanException {
		double financeTotal = 0;
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		List<ITransferObject> finances = financeBean.getList(criteria);
		for (ITransferObject to: finances) {
			Finance finance = (Finance) to;
			financeTotal += finance.getAmount();
		}
		return CommonUtil.round(financeTotal);
	}

	public boolean isWarned() {
		return isRecordable() && isMessagesPresent();
	}
	public boolean isCommentPresent() {
		return !StringUtils.isBlank( getInvoice().getComments());
	}
	
}
