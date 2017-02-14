package com.code.aon.ui.finance.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.IncomeInvoicingManager;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.supplier.util.SupplierValidationManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.bridge.IncomeTransferManager;
import com.code.aon.warehouse.enumeration.IncomeStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseInvoiceController extends InvoiceController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseInvoiceController.class.getName());
	
	private RegistryValidationManager vm;
	private IncomeTransferManager incomeTransferManager;
	private boolean showIncomeTransferWindow;
	private boolean showIncomeFilterWindow;

	public PurchaseInvoiceController() {
		setInvoiceAddressControllerName(PURCHASE_INVOICE_ADDRESS_CONTROLLER_NAME);
		setInvoiceDetailControllerName(PURCHASE_INVOICE_DETAIL_CONTROLLER_NAME);
		setInvoiceFinanceControllerName(PURCHASE_INVOICE_FINANCE_CONTROLLER_NAME);
	}

	private RegistryValidationManager getRegistryValidationManager() {
		if (vm == null) {
			vm = new SupplierValidationManager(); 
		}
		return vm;
	}

	public IncomeTransferManager getIncomeTransferManager() {
		if (incomeTransferManager == null) {
			incomeTransferManager = new IncomeTransferManager(); 
		}
		return incomeTransferManager;
	}

	public void setIncomeTransferManager(IncomeTransferManager incomeTransferManager) {
		this.incomeTransferManager = incomeTransferManager;
	}

	public boolean isShowIncomeTransferWindow() {
		return showIncomeTransferWindow;
	}

	public void setShowIncomeTransferWindow(boolean value) {
		this.showIncomeTransferWindow = value;
	}

	public boolean isShowIncomeFilterWindow() {
		return showIncomeFilterWindow;
	}

	public void setShowIncomeFilterWindow(boolean showIncomeFilterWindow) {
		this.showIncomeFilterWindow = showIncomeFilterWindow;
	}

	public void onSupplierChanged(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Supplier supplier = (Supplier)event.getNewValue();
			supplierChanged(supplier);
		} else {
			Invoice invoice = getInvoice();
			invoice.setRegistryAddress(null);

			setAddresses(null);	
		}
	}

	public void supplierChanged(Supplier supplier) throws ManagerBeanException {
		isBlocked(supplier);
		Invoice invoice = getInvoice();
		invoice.setRegistryName(supplier.getRegistry().getFullName());
		invoice.setRegistryDocument(supplier.getRegistry().getDocument());
		invoice.setRegistryDocumentType(supplier.getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(supplier.getRegistry().getDocumentCountry());
		invoice.setRegistry(supplier.getRegistry());
		invoice.setTransaction(supplier.getTransaction());
		invoice.setSurcharge(!invoice.isVatFree() && getCompanyController().isSurcharge());
		invoice.setWithholding(supplier.isWithholding());
		invoice.setWithholdingFarmer(supplier.isWithholdingFarmer());
		invoice.setScope(supplier.getScope());
		loadAddresses(supplier.getId());

		if (isNevv()) {
			InvoiceFinanceController financeController = (InvoiceFinanceController)FormUtil.getController(getInvoiceFinanceControllerName());
			Finance finance = (Finance)financeController.getTo();
			RegistryPayMethod rPayMethod = supplier.getRegistry().getPayMethod();
			finance.setPayMethod((rPayMethod==null) ? new PayMethod() : rPayMethod.getPayment());
			finance.setBankAccount((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? new BankAccount() : rPayMethod.getBankAccount());
			finance.setBankAlias((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? null : rPayMethod.getBankAlias());
			finance.setBic((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? null : rPayMethod.getBic());

			financeController.setRegistryBank((rPayMethod==null) ? null : rPayMethod.getRegistryBank());
			financeController.setShowBankManualInput(false);
		}
		validateInvoice();
	}

	private boolean isBlocked(Supplier supplier) {
		return getRegistryValidationManager().isBlocked(supplier);
	}

	public void onIncomeTransferShow(ActionEvent event) throws ManagerBeanException {
		getIncomeTransferManager().setFilterParams(null);
		loadIncomeTransferModel();
	}
	private void loadIncomeTransferModel() throws ManagerBeanException {
		List<ITransferObject> invoicedIncomeList = new LinkedList<ITransferObject>();
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.INCOME);
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		List<ITransferObject> list = invoiceDetailBean.getList(criteria);
		for (ITransferObject to : list ) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) to;
			IncomeDetail incomeDetail = (IncomeDetail)incomeDetailBean.get(invoiceDetail.getSourceId());
			if (!invoicedIncomeList.contains(incomeDetail.getIncome())) {
				invoicedIncomeList.add(incomeDetail.getIncome());
			}
		}
		getIncomeTransferManager().setInvoicedIncomeList(invoicedIncomeList);
		
		List<ITransferObject> incomeList = new LinkedList<ITransferObject>();
		if (!isReadOnly()) {
			IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
			criteria = new Criteria();
			criteria.addEqualExpression(incomeBean.getFieldName(IEntityAlias.INCOME_SUPPLIER_ID), getInvoice().getRegistry().getId());
			criteria.addEqualExpression(incomeBean.getFieldName(IEntityAlias.INCOME_STATUS), IncomeStatus.PENDING);
			criteria.addEqualExpression(incomeBean.getFieldName(IEntityAlias.INCOME_SECURITY_LEVEL), getInvoice().getSecurityLevel());
			if(getIncomeTransferManager().getFilterParams().getFromDate()!=null){
				criteria.addGreaterThanOrEqualExpression(incomeBean.getFieldName(IEntityAlias.INCOME_ISSUE_TIME), getIncomeTransferManager().getFilterParams().getFromDate());
			}
			if(getIncomeTransferManager().getFilterParams().getToDate()!=null){
				criteria.addLessThanOrEqualExpression(incomeBean.getFieldName(IEntityAlias.INCOME_ISSUE_TIME), getIncomeTransferManager().getFilterParams().getToDate());
			}
			if(StringUtils.isNotBlank(getIncomeTransferManager().getFilterParams().getReferenceCode())){
				criteria.addExpression(ExpressionUtilities.getLikeExpression(
						incomeBean.getFieldName(IEntityAlias.INCOME_REFERENCE_CODE), "%" + getIncomeTransferManager().getFilterParams().getReferenceCode() + "%"));
			}
			if(getIncomeTransferManager().getCheckedIncomeCount() > 0){
				for(Income income: getIncomeTransferManager().getCheckedIncome()){
					criteria.addNotEqualExpression(incomeBean.getFieldName(IEntityAlias.INCOME_ID), income.getId());
				}
			}
			if(getIncomeTransferManager().getInvoicedIncomeCount() > 0){
				for(ITransferObject to: getIncomeTransferManager().getInvoicedIncomeList()){
					criteria.addNotEqualExpression(incomeBean.getFieldName(IEntityAlias.INCOME_ID), ((Income)to).getId());
				}
			}
			criteria.addOrder(incomeBean.getFieldName(IEntityAlias.INCOME_ISSUE_TIME));
			criteria.addOrder(incomeBean.getFieldName(IEntityAlias.INCOME_REFERENCE_CODE));
			incomeList.addAll(incomeBean.getList(criteria));
		}
		getIncomeTransferManager().setIncomeList(incomeList);
	}
	
	public void onFilterTransferModel(ActionEvent event) throws ManagerBeanException {
		loadIncomeTransferModel();
	}

	public void onIncomeTransfer(ActionEvent event) throws ManagerBeanException {
		try {
			IncomeInvoicingManager invoicingManager = new IncomeInvoicingManager();
			List<Income> transferIncomeList = new LinkedList<>();
			getIncomeTransferManager().getInvoicedIncomeList().forEach(to -> 
			{if(!getIncomeTransferManager().getCheckedRestoreInvoicedIncome().contains(to))
				transferIncomeList.add((Income)to);
			});
			transferIncomeList.addAll(getIncomeTransferManager().getCheckedIncome());
			invoicingManager.transferIncomes(getInvoice(), transferIncomeList, getIncomeTransferManager().getInvoicedIncomeList());
			
			refresh(null);
			FormUtil.getController(PURCHASE_INVOICE_DETAIL_CONTROLLER_NAME).onSearch(null);

			autoGenerateIncreases();
			autoGenerateFinances();
			resetListTotals();
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public String rectificationRedirect() {
		return PURCHASE_INVOICE_FORM_NAME;
	}

	@Override
	public IAttachment generateReportAttachment( ITransferObject to ) {
		IManagerBean bean = getAttachmentBean();
		try {
			Criteria criteria = new Criteria();
			String invoiceAlias = bean.getFieldName(IEntityAlias.INVOICE_ATTACHMENT_INVOICE_ID);
			criteria.addEqualExpression(invoiceAlias, getInvoice().getId());
			String typeAlias = bean.getFieldName(IEntityAlias.INVOICE_ATTACHMENT_MIME_TYPE);
			criteria.addEqualExpression(typeAlias, MimeType.MIME_PDF);
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return (IAttachment) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error getting invoice pdf file " + to, e );
		}
		return null;
	}
	
	@Override
	public IAttachment getUnsignedAttachment(ITransferObject to, MimeType type) {
		if ( type == MimeType.MIME_PDF ) {
			IAttachment attachment = generateReportAttachment(to);
			if ( attachment == null ) {
				attachment = super.generateReportAttachment(to);
			}
			return attachment;
		}
		return super.getUnsignedAttachment(to, type);
	}		

	@Override
	public SignerController getSignerController() {
		return (SignerController) AonUtil.getRegisteredBean(PURCHASE_INVOICE_SIGNER_CONTROLLER_NAME);
	}

}
