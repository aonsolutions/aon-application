package com.code.aon.ui.finance.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.util.BankUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceFinanceController extends LinesController implements IFinanceConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private RegistryBank registryBank;
	private boolean showBankManualInput;

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public boolean isShowBankManualInput() throws ManagerBeanException {
		return showBankManualInput;
	}

	public void setShowBankManualInput(boolean showBankManualInput) {
		this.showBankManualInput = showBankManualInput;
	}

	public boolean isModelToEditable() throws ManagerBeanException{
		if (getModel().isRowAvailable()) {
			Finance finance = (Finance)getModel().getRowData(); 
			return (finance.isPending() || finance.isReturned());
		}
		return false;
	}

	public boolean isModelToPending() throws ManagerBeanException{
		if (getModel().isRowAvailable()) {
			Finance finance = (Finance)getModel().getRowData(); 
			return (finance.isPending());
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean isAllPending() throws ManagerBeanException{
		for (ITransferObject ito : (List<ITransferObject>)getModel().getWrappedData()) {
			Finance finance = (Finance)ito;
			if (!finance.isPending()) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean isOnePending() throws ManagerBeanException{
		for (ITransferObject ito : (List<ITransferObject>)getModel().getWrappedData()) {
			Finance finance = (Finance)ito;
			if (finance.isPending()) {
				return true;
			}
		}
		return false;
	}

	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPayMethod = (PayMethod) event.getOldValue();
		PayMethod newPayMethod = (PayMethod) event.getNewValue();
		if (oldPayMethod == null || newPayMethod == null || oldPayMethod.getType() != newPayMethod.getType()) {
			Finance finance = (Finance) getTo();
			finance.setPayMethod(newPayMethod);
			finance.setBankAccount(new BankAccount());
			finance.setBankAlias(null);
			finance.setBic(null);

			setRegistryBank(null);
			setShowBankManualInput(false);
		}
	}

	public void onRBankChanged(ValueChangeEvent event) {
		Finance finance = (Finance) getTo();
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			RegistryBank rbank = (RegistryBank) event.getNewValue();
			finance.setBankAccount(rbank.getBankAccount());
			finance.setBankAlias(rbank.getBankAlias());
			finance.setBic(rbank.getBic());

			setRegistryBank(rbank);
		} else {
			finance.setBankAccount(new BankAccount());
			finance.setBankAlias(null);
			finance.setBic(null);

			setRegistryBank(null);
		}
	}

	public List<SelectItem> getAllBanks() throws ManagerBeanException {
		Finance finance = (Finance) getTo();
		if (finance != null && finance.getPayMethod() != null) {
			if (useRegistryBanks(finance.getInvoice().getType() == InvoiceType.SALES, finance.getPayMethod().getType())) {
				RegistryCollectionsController registryColls = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return registryColls.getAllRegistryBanks(finance.getInvoice().getRegistry());
			} 
			CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return companyColls.getAllCompanyBanks();
		}
		return new LinkedList<SelectItem>();
	}
	
	public int getAllBanksCount() throws ManagerBeanException {
		return getAllBanks().size();
	}

	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		Finance finance = (Finance) getTo();
		if (finance != null && finance.getPayMethod() != null) {
			if (useRegistryBanks(finance.getInvoice().getType() == InvoiceType.SALES, finance.getPayMethod().getType())) {
				RegistryCollectionsController registryColls = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return registryColls.getActiveRegistryBanks(finance.getInvoice().getRegistry());
			} 
			CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return companyColls.getActiveCompanyBanks();
		}
		return new LinkedList<SelectItem>();
	}
	
	public int getActiveBanksCount() throws ManagerBeanException {
		return getActiveBanks().size();
	}

	private boolean useRegistryBanks(boolean sales, PayMethodType payMethodType) {
		return ((sales && payMethodType == PayMethodType.NEGOTIABLE_DOCUMENT) || (!sales && payMethodType == PayMethodType.BANK_TRANSFER));	
	}

	public void onBankManualInput(ActionEvent event) throws ManagerBeanException {
		Finance finance = (Finance) getTo();
		finance.setBankAccount(new BankAccount());
		finance.setBankAlias(null);
		finance.setBic(null);

		setRegistryBank(null);
	}

	public void onBankAccountData(ActionEvent event) {
		BankUtil.fillBankAccountData((Finance)getTo());
	}

	public boolean isBankCreationEnabled() throws ManagerBeanException {
		Finance finance = (Finance) getTo();
		if (finance == null || finance.getPayMethod() == null || finance.getBankAccount() == null)
			return false;
		if (!useRegistryBanks(finance.getInvoice().getType() == InvoiceType.SALES, finance.getPayMethod().getType()))
			return false;
		if (!finance.getBankAccount().isValidBankAccount())
			return false;

		for (SelectItem item : getAllBanks()) {
			RegistryBank rBank = (RegistryBank)item.getValue();
			BankAccount bankAccount = rBank.getBankAccount();
			if (bankAccount != null && StringUtils.equals(finance.getBankAccount().getIban(), bankAccount.getIban())) {
				return false;
			}
		}
		return true;
	}

	public void onAddRegistryBank(ActionEvent event) throws ManagerBeanException {
		Finance finance = (Finance) getTo();

		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		RegistryBank rBank = new RegistryBank();
		rBank.setRegistry(finance.getInvoice().getRegistry());
		rBank.setBankAccount(finance.getBankAccount());
		rBank.setBankAlias(finance.getBankAlias());
		rBank.setBic(finance.getBic());
		rBank.setActive(true);
		rBank = (RegistryBank)rBankBean.insert(rBank);
		setRegistryBank(rBank);
	}

	public boolean isAdvancedFinancesAvailable() throws ManagerBeanException {
		Invoice invoice = (Invoice)getMasterController().getTo();

		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_REGISTRY_ID), invoice.getRegistry().getId());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAYMENT), !invoice.isSales());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_SECURITY_LEVEL), invoice.getSecurityLevel());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_ADVANCE), true);
		criteria.addNullExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID));
		return (financeBean.getCount(criteria) > 0);
	}

	public void onImportAdvances(ActionEvent event) throws ManagerBeanException {
		Invoice invoice = (Invoice)getMasterController().getTo();

		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_REGISTRY_ID), invoice.getRegistry().getId());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAYMENT), !invoice.isSales());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_SECURITY_LEVEL), invoice.getSecurityLevel());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_ADVANCE), true);
		criteria.addNullExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID));
		for (ITransferObject ito : financeBean.getList(criteria)) {
			Finance finance = (Finance)ito;
			finance.setInvoice(invoice);
			finance.setRemarks("[" + finance.getConcept() + "]" + "\n" + ((finance.getRemarks() == null) ? "" : finance.getRemarks()));
			financeBean.update(finance);
		}
		onSearch(event);
	}

	public void onExcludeAdvance(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {  
			Finance finance = (Finance)getModel().getRowData();
			finance.setInvoice(null);
			if (finance.getRemarks() != null && finance.getRemarks().indexOf("[") >= 0 && finance.getRemarks().indexOf("]") >= 0) {
				finance.setConcept(finance.getRemarks().substring(finance.getRemarks().indexOf("[")+1, finance.getRemarks().indexOf("]")));
				finance.setRemarks(finance.getRemarks().substring(finance.getRemarks().indexOf("]")+1));
			}
			getManagerBean().update(finance);

			onSearch(event);
		}
	}

	public void onLoadFinance(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {  
			Finance finance = (Finance)getModel().getRowData();
			String backAction = "";
			String backActionListener = "";
			if (finance.getInvoice().getType() == InvoiceType.SALES) {
				backAction = SALE_INVOICE_FORM_NAME;
				backActionListener = SALE_INVOICE_FINANCE_CONTROLLER_NAME;
			} else if (finance.getInvoice().getType() == InvoiceType.PURCHASE) {
				backAction = PURCHASE_INVOICE_FORM_NAME;
				backActionListener = PURCHASE_INVOICE_FINANCE_CONTROLLER_NAME;
			} else if (finance.getInvoice().getType() == InvoiceType.EXPENSES) {
				backAction = EXPENSE_INVOICE_FORM_NAME;
				backActionListener = EXPENSE_INVOICE_FINANCE_CONTROLLER_NAME;
			} else if (finance.getInvoice().getType() == InvoiceType.UNDEDUCTIBLE) {
				backAction = UNDEDUCTIBLE_INVOICE_FORM_NAME;
				backActionListener = UNDEDUCTIBLE_INVOICE_FINANCE_CONTROLLER_NAME;
			}

			BasicController financeController = (BasicController)AonUtil.getRegisteredBean(FINANCE_CONTROLLER_NAME);
			financeController.onLoad(event, finance.getId(), backAction, backActionListener + ".onSearch");
		}
	}

}
