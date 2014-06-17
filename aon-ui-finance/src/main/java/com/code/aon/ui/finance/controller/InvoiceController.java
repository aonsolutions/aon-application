package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.CALCULATE_FINANCES_AMOUNT_ERROR_KEY;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_DUPLICATE_EXPENSE_INVOICE_WARNING;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_DUPLICATE_PURCHASE_INVOICE_WARNING;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_UNRECORD_INVOICE_WARNING;
import static com.code.aon.ui.common.ICommonMessages.GENERATE_FINANCES_ERROR_KEY;
import static com.code.aon.ui.common.ICommonMessages.UNABLE_RECORD_INACCURACY_ERROR_KEY;
import static com.code.aon.ui.common.ICommonMessages.UNABLE_RECORD_NO_AMORTIZATION_ERROR_KEY;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationInvoice;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.config.util.SeriesUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceAttachment;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.RectificationInvoicingManager;
import com.code.aon.finance.enumeration.InvoiceAttachmentType;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.tas.ProjectTas;
import com.code.aon.tas.enumeration.ProjectStatus;
import com.code.aon.ui.finance.util.FinanceEmailUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.ExtendedPageDataModel;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.sign.controller.ISignatureController;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceController extends BasicController implements ISignatureController, IFinanceConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class.getName());
	
	private String invoiceAddressControllerName;
	private String invoiceDetailControllerName;
	private String invoiceFinanceControllerName;
	private IPriceStrategy priceStrategy;
	private int financeGenerationMode;
	private FinanceGenerator financeGenerator;
	private AccountEntryInvoiceWriter accountWriter;
	private List<SelectItem> addresses;
	private List<SelectItem> projects;
	private boolean showRegistryDataWindow;
	private boolean showSellerDataWindow;
	private boolean showInvoiceAddressWindow;
	private boolean showProjectWindow;
	private boolean showNewProjectWindow;
	private boolean showDetailProjectWindow;
	private boolean showDocumentWindow;
	private boolean showCommentsWindow;
	private boolean showRemarksWindow;
	private boolean showAuditInfoWindow;
	private boolean showFiscalInformationWindow;
	private boolean showAmortizationWindow;
	private boolean showRectificationWindow;
	private boolean showPaymentDataInListView;
	private String rectificationSeries;
	private int rectificationNumber;
	private Date rectificationDate;
	private String rectificationCause;
	private boolean rectificationSettleFinance;
	private boolean showDiscountsWindow;
	private String discountExpression;
	private Double totalInvoiceAmount;
	private FinanceEmailUtil emailController;
	
	public InvoiceController() {
		this.emailController = new FinanceEmailUtil();
	}

	public Invoice getInvoice() {
		return (Invoice)getTo();
	}
	
	public String getInvoiceAddressControllerName() {
		return invoiceAddressControllerName;
	}

	public void setInvoiceAddressControllerName(String invoiceAddressControllerName) {
		this.invoiceAddressControllerName = invoiceAddressControllerName;
	}

	public String getInvoiceDetailControllerName() {
		return invoiceDetailControllerName;
	}

	public void setInvoiceDetailControllerName(String invoiceDetailControllerName) {
		this.invoiceDetailControllerName = invoiceDetailControllerName;
	}

	public String getInvoiceFinanceControllerName() {
		return invoiceFinanceControllerName;
	}

	public void setInvoiceFinanceControllerName(String invoiceFinanceControllerName) {
		this.invoiceFinanceControllerName = invoiceFinanceControllerName;
	}

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public int getFinanceGenerationMode() {
		return financeGenerationMode;
	}

	public void setFinanceGenerationMode(int financeGenerationMode) {
		this.financeGenerationMode = financeGenerationMode;
	}

	public void onFinanceGenerationModeChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			setFinanceGenerationMode((Integer)event.getNewValue());
		}
	}

	public FinanceGenerator getFinanceGenerator() {
		if (financeGenerator == null) {
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}

	public AccountEntryInvoiceWriter getAccountWriter() {
		if (accountWriter == null) {
			accountWriter = new AccountEntryInvoiceWriter();
		}
		return accountWriter;
	}
	
    public List<SelectItem> getAddresses() {
		return addresses;
	}
	
	public void setAddresses(List<SelectItem> addresses) {
		this.addresses = addresses;
	}
	
	public int getAddressCount() {
		if (addresses != null) {
			return addresses.size();
		}
		return 0;
	}
	
	public void loadAddresses(Integer id) throws ManagerBeanException {
		this.addresses = new LinkedList<SelectItem>();
		if (id != null) {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
			for (ITransferObject ito : rAddressBean.getList(criteria)) {
				RegistryAddress address = (RegistryAddress)ito;
				String addressLabel = address.getFullAddress();
				addressLabel = ((addressLabel.length()>30)?addressLabel.substring(0,27)+"...":addressLabel) + " - " + address.getCity();
				addressLabel = ((addressLabel.length()>48)?addressLabel.substring(0,45)+"...":addressLabel);
				SelectItem item = new SelectItem(address, addressLabel);
				addresses.add(item);
			}
		}
	}

	public String getAddress() {
		IAddress iAddress = getInvoice().getRegistryAddress();
		BasicController addressController = (BasicController)FormUtil.getController(invoiceAddressControllerName);
		if (addressController.getTo() != null && ((InvoiceAddress)addressController.getTo()).getId() != null) {
			iAddress = (InvoiceAddress)addressController.getTo();
		}
		return iAddress.getFullAddress();
	}

	public String getLocation() {
		IAddress iAddress = getInvoice().getRegistryAddress();
		BasicController addressController = (BasicController)FormUtil.getController(invoiceAddressControllerName);
		if (addressController.getTo() != null && ((InvoiceAddress)addressController.getTo()).getId() != null) {
			iAddress = (InvoiceAddress)addressController.getTo();
		}
		return iAddress.getLocation();
	}

    public List<SelectItem> getProjects() {
		return projects;
	}
	
	public void setProjects(List<SelectItem> projects) {
		this.projects = projects;
	}
	
	public int getProjectCount() {
		if (projects != null) {
			return projects.size();
		}
		return 0;
	}
	
	public void loadProjects(Integer registryId) throws ManagerBeanException {
		this.projects = new LinkedList<SelectItem>();
		if (registryId != null) {
			IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
			Criteria criteria = new Criteria();
			if (getInvoice().getType() == InvoiceType.SALES) {
				criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID), registryId);
			}
			criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_ACTIVE), Boolean.TRUE);
			criteria.addOrder(projectBean.getFieldName(IEntityAlias.PROJECT_NAME));
			for (ITransferObject ito : projectBean.getList(criteria)) {
				Project project = (Project)ito;
				SelectItem item = new SelectItem(project, project.getName());
				projects.add(item);
			}
		}
	}

	public void loadInvoiceProjects(ActionEvent event) throws ManagerBeanException {
		Invoice invoice = (Invoice) this.getTo();
		loadProjects(invoice.getRegistry().getId());
	}
	

	public boolean isShowPaymentDataInListView() {
		return showPaymentDataInListView;
	}

	public void setShowPaymentDataInListView(boolean showPaymentDataInListView) {
		this.showPaymentDataInListView = showPaymentDataInListView;
	}

	public boolean isShowRegistryDataWindow() {
		return showRegistryDataWindow;
	}

	public void setShowRegistryDataWindow(boolean value) {
		this.showRegistryDataWindow = value;
	}

	public boolean isShowSellerDataWindow() {
		return showSellerDataWindow;
	}

	public void setShowSellerDataWindow(boolean value) {
		this.showSellerDataWindow = value;
	}

	public boolean isShowInvoiceAddressWindow() {
		return showInvoiceAddressWindow;
	}

	public void setShowInvoiceAddressWindow(boolean value) {
		this.showInvoiceAddressWindow = value;
	}

	public void onInvoiceAddressShow(ActionEvent event) {
		BasicController addressController = (BasicController)FormUtil.getController(invoiceAddressControllerName);
		ITransferObject to = addressController.getTo();
		if (to == null) {
			addressController.onReset(event);
		}
	}

	public boolean isShowProjectWindow() {
		return showProjectWindow;
	}

	public void setShowProjectWindow(boolean value) {
		this.showProjectWindow = value;
	}

	public boolean isShowNewProjectWindow() {
		return showNewProjectWindow;
	}

	public void setShowNewProjectWindow(boolean showNewProjectWindow) {
		this.showNewProjectWindow = showNewProjectWindow;
	}

	public boolean isShowDetailProjectWindow() {
		return showDetailProjectWindow;
	}

	public void setShowDetailProjectWindow(boolean value) {
		this.showDetailProjectWindow = value;
	}

	public void addInvoiceProject(ActionEvent event) throws ManagerBeanException {
		linkProject(getInvoice().getProject(), true);

		IController invoiceDetailController = FormUtil.getController(invoiceDetailControllerName);
		invoiceDetailController.onSearch(null);
	}

	public void removeInvoiceProject(ActionEvent event) throws ManagerBeanException {
		Project project = getInvoice().getProject();
		if (project.isTas() && getInvoice().getType() == InvoiceType.SALES) {
			IManagerBean projectTasBean = BeanManager.getManagerBean(ProjectTas.class);
			ProjectTas projectTas = (ProjectTas)projectTasBean.get(project.getId());
			if (projectTas.getStatus() == ProjectStatus.CLOSED) {
				projectTas.setStatus(ProjectStatus.PENDING);
				projectTasBean.update(projectTas);
			}
		}
		linkProject(project, false);

		IController invoiceDetailController = FormUtil.getController(invoiceDetailControllerName);
		invoiceDetailController.onSearch(null);
	}

	private void linkProject(Project project, boolean link) throws ManagerBeanException {
		Invoice to = getInvoice();
		to.setProject((link) ? project : null);
		to.setUpdateEnabled(false);
		getManagerBean().restoreNullSubPOJOs(to);
		getManagerBean().update(to);

		to.setUpdateEnabled(true);
		getManagerBean().initializePOJO(to);
		loadProjects(to.getRegistry().getId());

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), to.getId());
		if (link) {
			criteria.addNullExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_PROJECT));
		} else {
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_PROJECT_ID), project.getId());
		}
		for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			invoiceDetail.setProject((link) ? project : null);
			invoiceDetail.setUpdateEnabled(false);
			invoiceDetailBean.update(invoiceDetail);
		}
	}
	
	public boolean isShowDocumentWindow() {
		return showDocumentWindow;
	}

	public void setShowDocumentWindow(boolean showDocumentWindow) {
		this.showDocumentWindow = showDocumentWindow;
	}

	public boolean isShowCommentsWindow() {
		return showCommentsWindow;
	}

	public void setShowCommentsWindow(boolean showCommentsWindow) {
		this.showCommentsWindow = showCommentsWindow;
	}

	public boolean isShowRemarksWindow() {
		return showRemarksWindow;
	}

	public void setShowRemarksWindow(boolean showRemarksWindow) {
		this.showRemarksWindow = showRemarksWindow;
	}

	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
	public boolean isShowFiscalInformationWindow() {
		return showFiscalInformationWindow;
	}

	public void setShowFiscalInformationWindow(boolean showFiscalInformationWindow) {
		this.showFiscalInformationWindow = showFiscalInformationWindow;
	}

	public boolean isShowAmortizationWindow() {
		return showAmortizationWindow;
	}

	public void setShowAmortizationWindow(boolean value) {
		this.showAmortizationWindow = value;
	}

	public boolean isShowRectificationWindow() {
		return showRectificationWindow;
	}

	public void setShowRectificationWindow(boolean value) {
		this.showRectificationWindow = value;
	}
	
	public String getRectificationSeries() {
		return rectificationSeries;
	}

	public void setRectificationSeries(String rectificationSeries) {
		this.rectificationSeries = rectificationSeries;
	}

	public int getRectificationNumber() {
		return rectificationNumber;
	}

	public void setRectificationNumber(int rectificationNumber) {
		this.rectificationNumber = rectificationNumber;
	}

	public Date getRectificationDate() {
		return rectificationDate;
	}

	public void setRectificationDate(Date rectificationDate) {
		this.rectificationDate = rectificationDate;
	}
	
	public String getRectificationCause() {
		return rectificationCause;
	}

	public void setRectificationCause(String rectificationCause) {
		this.rectificationCause = rectificationCause;
	}

	public boolean getRectificationSettleFinance() {
		return rectificationSettleFinance;
	}

	public void setRectificationSettleFinance(boolean rectificationSettleFinance) {
		this.rectificationSettleFinance = rectificationSettleFinance;
	}

	public void onRectificationShow(ActionEvent event) throws ManagerBeanException {
		setRectificationSeries(SeriesUtil.ensureRectificationSeries(getInvoice().getSeries()));
		setRectificationNumber(obtainMaxInvoiceNumber(getRectificationSeries()));
		setRectificationDate(new Date());
		setRectificationCause(null);
		setRectificationSettleFinance(true);
	}

	public void onRectificationSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		setRectificationNumber(obtainMaxInvoiceNumber((String)event.getNewValue()));
	}

	protected int obtainMaxInvoiceNumber(String seriesId) {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	public void onRectify(ActionEvent event) throws ManagerBeanException {
		RectificationInvoicingManager rectificationManager = new RectificationInvoicingManager();
		Invoice rectifier = rectificationManager.rectifyInvoice(getInvoice(), getRectificationSeries(), getRectificationNumber(), getRectificationDate(), 
																	getRectificationCause(), getRectificationSettleFinance());

		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.INVOICE_ID), rectifier.getId());
		onSearch(event);
		getModel().setRowIndex(0);
		onSelect(event);
	}

	public String rectificationRedirect() {
		return SALE_INVOICE_FORM_NAME;
	}

	public boolean isShowDiscountsWindow() {
		return showDiscountsWindow;
	}

	public void setShowDiscountsWindow(boolean showDiscountsWindow) {
		this.showDiscountsWindow = showDiscountsWindow;
	}

	public String getDiscountExpression() {
		return discountExpression;
	}

	public void setDiscountExpression(String discountExpression) {
		this.discountExpression = discountExpression;
	}

	public Double getTotalInvoiceAmount() {
		return totalInvoiceAmount;
	}

	public void setTotalInvoiceAmount(Double totalInvoiceAmount) {
		this.totalInvoiceAmount = totalInvoiceAmount;
	}
	
	public void onReferenceCodeChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			getInvoice().setReferenceCode((String)event.getNewValue());
			validateInvoice();
		}
	}

	public void onDateChanged(ActionEvent event) {
		getInvoice().setTaxDate(getInvoice().getIssueDate());
	}

	public boolean isTaxDateEquals() {
		Invoice invoice = getInvoice();
		if (invoice != null) {
			return ObjectUtils.equals(invoice.getIssueDate(), invoice.getTaxDate());
		}
		return true;
	}

	protected boolean validateInvoice() {
		Invoice invoice = getInvoice();
		if (invoice.getRegistry() != null && invoice.getRegistry().getId() != null && StringUtils.isNotEmpty(invoice.getReferenceCode())) {
			try {
				Criteria criteria = new Criteria();
				if (invoice.getId() != null) {
					criteria.addNotEqualExpression(getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
				}
				criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_REGISTRY_ID), invoice.getRegistry().getId());
				criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_REFERENCE_CODE), invoice.getReferenceCode());
				criteria.addEqualExpression(getFieldName(IEntityAlias.INVOICE_TYPE), invoice.getType());
				if (getManagerBean().getCount(criteria) > 0) {
					String msg = (getInvoice().isPurchase()) ? FINANCE_DUPLICATE_PURCHASE_INVOICE_WARNING : FINANCE_DUPLICATE_EXPENSE_INVOICE_WARNING;
					msg = AonUtil.getMessage(msg); 
					AonUtil.addWarningMessage(msg + " [" + getInvoice().getReferenceCode() + "]");
					return false;
				}
			} catch (ManagerBeanException ex) {
			}
		}
		return true;
	}

	public double getTaxableBase(){
		return getPriceStrategy().getTaxableBase((ICalculableContainer)getTo());
	}

	public double getVatQuota(){
		return getPriceStrategy().getTotalVatQuota((ICalculableContainer)getTo(), (ITaxInfo)getTo());
	}

	public double getRetentionQuota(){
		return getPriceStrategy().getTotalRetentionQuota((ICalculableContainer)getTo(), (ITaxInfo)getTo());
	}

	public double getToInvoiceTotalPrice() {
		return getInvoiceTotalPrice((Invoice)getInvoice());
	}
	
	public double getInvoiceTotalPrice() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return getInvoiceTotalPrice(invoice);
	}

	private double getInvoiceTotalPrice(Invoice invoice) {
		if (InvoiceType.UNDEDUCTIBLE == invoice.getType()) {
			return getPriceStrategy().getTaxableBase(invoice);
		}
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}

	public double getToInvoiceFinanceTotal() {
		double financeTotal = 0;
		try {
			IController invoiceFinanceController = FormUtil.getController(invoiceFinanceControllerName);
			for (ITransferObject ito : invoiceFinanceController.getManagerBean().getList(invoiceFinanceController.getCriteria())) {
				Finance finance = (Finance)ito;
				financeTotal += finance.getAmount();
			}
		} catch (ManagerBeanException ex) {
			String msg = AonUtil.getMessage(CALCULATE_FINANCES_AMOUNT_ERROR_KEY) + ". " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
		return CommonUtil.round(financeTotal);
	}
	
	public double getTotalDetailQuantity(){
		try {
			IManagerBean detailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), ((Invoice)this.getTo()).getId());
			Projection projection = Projection.sum(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_QUANTITY));
			Object value = detailBean.getUniqueResult(projection, criteria);
			return (value != null) ? ((Double)value) : 0;
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Se ha producido un error al obtener la cantidad total de unidades");
		}
		return 0;
	}

	public boolean isRemovable() {
		InvoiceDetailController invoiceDetailController = (InvoiceDetailController)FormUtil.getController(invoiceDetailControllerName);
		return (invoiceDetailController.getTo() == null && !isReadOnly());
	}
	
	public boolean isAccountSource() throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.ACCOUNT);
		return (invoiceDetailBean.getCount(criteria) > 0);
	}

	public void applyDiscounts(ActionEvent event) throws ManagerBeanException {
		IController invoiceDetailController = FormUtil.getController(invoiceDetailControllerName);
		List<ITransferObject> detailList = getInvoice().getDetailList();
		for (ITransferObject ito : detailList) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			invoiceDetail.getDiscountExpression().setDiscountExpr(getDiscountExpression());
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetail.setSkipServiceProcess(true);
			invoiceDetail.setUpdateEnabled(detailList.lastIndexOf(invoiceDetail) == detailList.size()-1);
			invoiceDetailController.getManagerBean().update(invoiceDetail);
		}
		refresh(null);
		invoiceDetailController.onSearch(null);
	}

	public void generateFinances(ActionEvent event) throws ManagerBeanException{
		Invoice invoice = getInvoice();
		try {
			IController invoiceFinanceController = FormUtil.getController(invoiceFinanceControllerName);
			List<ITransferObject> financeList = invoiceFinanceController.getManagerBean().getList(invoiceFinanceController.getCriteria());
			for (ITransferObject ito : financeList) {
				Finance finance = (Finance)ito;
				if (!finance.isAdvance() && finance.isPending()) {
					invoiceFinanceController.getManagerBean().remove(finance);
				}
			}

			double pendingAmount = getPendingAmount();
			if (pendingAmount != 0) {
				getFinanceGenerator().generateFinances(invoice, pendingAmount);
			}
			invoiceFinanceController.onSearch(null);
		} catch (ManagerBeanException e) {
			String msg = AonUtil.getMessage(GENERATE_FINANCES_ERROR_KEY) + ". " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public double getPendingAmount() {
		return CommonUtil.round(getToInvoiceTotalPrice() - getToInvoiceFinanceTotal());
	}

	public void onRecordInvoice(ActionEvent event) throws ManagerBeanException{
		double invoiceTotal = getToInvoiceTotalPrice();
		double financeTotal = getToInvoiceFinanceTotal();
		if (financeTotal != 0 && invoiceTotal != financeTotal) {
			String message = AonUtil.addErrorMessageFromBundle(UNABLE_RECORD_INACCURACY_ERROR_KEY);
			throw new AbortProcessingException(message);
		}
		Invoice invoice = getInvoice();
		if (invoice.isInvestment() && !isAmortizationForm()) {
			String message = AonUtil.addErrorMessageFromBundle(UNABLE_RECORD_NO_AMORTIZATION_ERROR_KEY);
			throw new AbortProcessingException(message);
		}
		
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			getManagerBean().restoreNullSubPOJOs(invoice);
			getAccountWriter().recordAndUpdateInvoice(invoice);
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			// --- Si se ha producido algún error, se carga de nuevo la factura de la BD.
			synchronizeErrorPojo(invoice);		
			// ---
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public void onUnrecordInvoice(ActionEvent event) throws ManagerBeanException{
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		Invoice invoice = getInvoice();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			getManagerBean().restoreNullSubPOJOs(invoice);
			getAccountWriter().unrecordAndUpdateInvoice(invoice);

			HibernateUtil.commitTransaction(sessionName);
			getManagerBean().initializePOJO(invoice);

			// En el caso de que se haya accedido al mantenimiento de facturas desde el mantenimiento de apuntes,
			// hay que tener en cuenta que al descontabilizar la factura, se está borrando el apunte del que 
			// provienes. De tal forma, se sobreescribe la funcionalidad del botón Volver, para que vaya a la 
			// pantalla de búsqueda de apuntes, ejecutando el actionListener correspondiente. 
			if (ObjectUtils.equals(ACCOUNT_ENTRY_FORM_PAGE, this.backAction())) {
				setBackAction(ACCOUNT_ENTRY_SEARCH_PAGE);
				setBackActionListener(ACCOUNT_ENTRY_ON_EDIT_SEARCH_ACTION);
				AonUtil.addWarningMessageFromBundle(FINANCE_UNRECORD_INVOICE_WARNING);
			}
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			// --- Si se ha producido algún error, se carga de nuevo la factura de la BD.
			synchronizeErrorPojo(invoice);		
			// ---
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	@SuppressWarnings("unchecked")
	private void synchronizeErrorPojo(Invoice errorInvoice) throws ManagerBeanException {
		Invoice restoredInvoice = (Invoice) getManagerBean().get(errorInvoice.getId()) ; 
		setTo(restoredInvoice);
		if (getModel() instanceof ExtendedPageDataModel) {
			List<ITransferObject> list = (List<ITransferObject>)getModel().getWrappedData();
			for (int i=0; i < list.size(); i++) {
				Invoice listInvoice = (Invoice) list.get(i);
				if (listInvoice.getId().equals(restoredInvoice.getId())) {
					list.set(i,restoredInvoice);
					break;
				}
			}
		}
	}
	

	public Integer getAccountEntryId() throws ManagerBeanException {
    	Invoice invoice = getInvoice();
		if (invoice != null && invoice.getId() != null) {
			IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryInvoiceBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID), invoice.getId());
			for (ITransferObject ito : accountEntryInvoiceBean.getList(criteria)) {
				AccountEntryInvoice accountEntryInvoice = (AccountEntryInvoice)ito;
				return accountEntryInvoice.getAccountEntry().getId();
			}
		}
    	return null;
	}

	public boolean isReadOnly() {
		return (isSigned() || isRecorded() || isRectified() || isSpecialRectifier());
	}
	
	public boolean isSigned() {
		return getInvoice().isSigned();
	}

	public boolean isRecorded() {
		return getInvoice().isRecorded();
	}

	public boolean isRectifier() {
		return getInvoice().isRectifier();
	}

	public boolean isNormalRectifier() {
		return getInvoice().isNormalRectifier();
	}

	public boolean isSpecialRectifier() {
		return getInvoice().isSpecialRectifier();
	}

	public boolean isRectified() {
		return getInvoice().isRectified();
	}

	public boolean isRegistryReadOnly() throws ManagerBeanException {
		return isReadOnly() || isRegistryReadOnly(getInvoice());
	}

	private boolean isRegistryReadOnly(Invoice invoice) throws ManagerBeanException {
		if (invoice.getType() == InvoiceType.SALES && invoice.getProject() != null && invoice.getProject().getId() != null) {
			return true;
		}

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addNotEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DIRECT_INVOICE);
		return invoiceDetailBean.getCount(criteria) > 0;
	}

	@Override
	public IManagerBean getAttachmentBean() {
		try {
			return BeanManager.getManagerBean(InvoiceAttachment.class);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String getAttachmentMimeTypeAlias() {
		return IEntityAlias.INVOICE_ATTACHMENT_MIME_TYPE;
	}

	@Override
	public String getAttachmentParentAlias() {
		return IEntityAlias.INVOICE_ATTACHMENT_INVOICE_ID;
	}

	@Override
	public boolean isSigned(ITransferObject to) {
		return getInvoice().isSigned();
	}

	@Override
	public IAttachment newAttachment(ITransferObject parent) {
		InvoiceAttachment attachment = new InvoiceAttachment();
		attachment.setInvoice((Invoice) parent);
		attachment.setType(InvoiceAttachmentType.INVOICE);
		return attachment;
	}

	@Override
	public void setSigned(ITransferObject to, boolean value) {
		((Invoice) to).setSigned(value);
	}
	
	@Override
	public IAttachment generateReportAttachment(ITransferObject to) {
		return getSignerController().getReport(to);
	}
	
	@Override
	public IAttachment getUnsignedAttachment(ITransferObject to) {
		return generateReportAttachment(to);
	}	
	
	@Override
	public String getDescription(ITransferObject parent) {
		Invoice invoice = (Invoice) parent;
		return "invoice_" + invoice.getReferenceCode().replace("/", "-");
	}
	
	public FinanceEmailUtil getEmailController() {
		return emailController;
	}
	
	public SignerController getSignerController() {
		return (SignerController) AonUtil.getRegisteredBean(SALE_INVOICE_SIGNER_CONTROLLER_NAME);
	}
	
	public IAttachment getInvoiceData(Invoice invoice) throws ManagerBeanException {
		SignerController signer = getSignerController();
		IAttachment attach = null;
		if (invoice.isSigned()) {
			attach = signer.getSignedAttachment(invoice.getId());
		} else {
			attach = getUnsignedAttachment(invoice);
		}
		return attach;		
	}
	
	public void onSendInvoiceByEmail(ActionEvent event) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {
			try {
				controller.onNewMessage(event);
				Invoice invoice = getInvoice();
				IAttachment attach = getInvoiceData(invoice);
				emailController.initMessageController(controller, invoice, attach, true);
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
				AonUtil.addErrorMessage(th.getMessage());
				throw new AbortProcessingException(th.getMessage(), th);
			}						
		}
	}
	
	public List<SelectItem> getInvoiceAttachmentTypes() {
		List<SelectItem> invoiceAttachmentTypes = new LinkedList<SelectItem>();
		if (! isAttachmentAvailable() ) {
			String name = InvoiceAttachmentType.INVOICE.getName(AonUtil.getCurrentLocale());
			invoiceAttachmentTypes.add( new SelectItem(InvoiceAttachmentType.INVOICE, name) );
		}
		String name = InvoiceAttachmentType.RECEIPT.getName(AonUtil.getCurrentLocale());
		invoiceAttachmentTypes.add( new SelectItem(InvoiceAttachmentType.RECEIPT, name) );
		return invoiceAttachmentTypes;
	}

	public boolean isAttachmentAvailable() {
		IManagerBean bean = getAttachmentBean();
		try {
			Criteria criteria = new Criteria();
			String invoiceAlias = bean.getFieldName(IEntityAlias.INVOICE_ATTACHMENT_INVOICE_ID);
			criteria.addEqualExpression(invoiceAlias, getInvoice().getId());
			String typeAlias = bean.getFieldName(IEntityAlias.INVOICE_ATTACHMENT_TYPE);
			criteria.addEqualExpression(typeAlias, InvoiceAttachmentType.INVOICE);
			return bean.getCount(criteria) > 0;
		} catch (ManagerBeanException e) {
			LOGGER.error("Error getting invoice pdf file " + getInvoice(), e );
		}
		return false;
	}
	
	public List<TaxBreakDown> getTaxBreakDowns() {
		if (getTo() != null) {
			Invoice invoice = getInvoice();
			List<TaxBreakDown> taxBreakDowns = getPriceStrategy().getTaxBreakDowns(invoice, invoice);
			Collections.sort(taxBreakDowns, new Comparator<TaxBreakDown>() {
				@Override
				public int compare(TaxBreakDown o1, TaxBreakDown o2) {
					int a = o1.getTaxType().ordinal();
					int b = o2.getTaxType().ordinal();
					if (a<b) {
						return -1;
					}
					if (a>b) {
						return 1;
					}
					return 0; 
				}
			});
			return taxBreakDowns;
		}
		return null;
	}
	
	public void onAcceptFiscalInformation(ActionEvent event) {
		Invoice invoice = getInvoice();
		
		// Solo se puede modificar service e investment, que no afectan a los totales
		// por lo tanto no es necesario recalcular.
		invoice.setUpdateEnabled(false);
		
		super.accept(event);
	}

	public boolean isAmortizationForm () {
		if (!isNew() && getInvoice().isInvestment()) { 
			try {
				IManagerBean bean = BeanManager.getManagerBean(AmortizationInvoice.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean.getFieldName(IEntityAlias.AMORTIZATION_INVOICE_INVOICE_ID), getInvoice().getId() );
				List<ITransferObject> list = bean.getList(c);
				return (list != null && list.size() > 0);
			} catch (ManagerBeanException e) {
				e.printStackTrace();
				LOGGER.error("Imposible averiguar si la factura tiene ficha de amortización");
			}
		}
		return false;
	}
	
	public void onShowAmortizationPanel(ActionEvent event) {
		setShowAmortizationWindow(true);
		
		IController controller = (IController) AonUtil.getRegisteredBean(AMORTIZATION_CONTROLLER_NAME);
		controller.onReset(event);
		Invoice invoice = getInvoice();
		Amortization amortization = (Amortization) controller.getTo();
		amortization.setAmount(invoice.getTaxableBase());
		amortization.setConfidential(invoice.isConfidential());
		amortization.setInitialDate(invoice.getIssueDate());
	}
	
	public void acceptAmortization(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			IController controller = (IController) AonUtil.getRegisteredBean(AMORTIZATION_CONTROLLER_NAME);
			((BasicController) controller).accept(event);
			Amortization amortization = (Amortization) controller.getTo();
			Invoice invoice = getInvoice();
			IManagerBean bean = BeanManager.getManagerBean(AmortizationInvoice.class);
			AmortizationInvoice ai = new AmortizationInvoice();
			ai.setAmortization(amortization);
			ai.setInvoice(invoice);
			bean.insert(ai);
			
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	public String showAmortization() {
		try {
			IController controller = (IController) AonUtil.getRegisteredBean(AMORTIZATION_CONTROLLER_NAME);
			controller.onEditSearch(null);
			Criteria criteria = controller.getCriteria();
		
			IManagerBean bean = BeanManager.getManagerBean(AmortizationInvoice.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.AMORTIZATION_INVOICE_INVOICE_ID), getInvoice().getId() );
			List<ITransferObject> list = bean.getList(c);
			List<Integer> ids = new ArrayList<Integer>();
			for (ITransferObject to : list) {
				AmortizationInvoice ai = (AmortizationInvoice) to;
				ids.add(ai.getAmortization().getId());
			}
			String alias = controller.getFieldName(IEntityAlias.AMORTIZATION_ID);
			criteria.addInExpression(alias, ids);
			controller.onSearch(null);
			String backAction = AMORTIZATION_LIST_VIEW; 
			if (controller.getModel().getRowCount() == 1) {
				controller.getModel().setRowIndex(0);
				controller.onSelect(null);
				backAction = AMORTIZATION_FORM_VIEW;
			} 
			((BasicController) controller).setBackAction(getBeanName() + "_form");
			return backAction;
		} catch (ManagerBeanException e) {
			String message = "Imposible navegar a la ficha de amortización";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
		
	}
	
	public boolean isAmoritizationNavigationDisabled() {
		return ( getBackAction() != null);
	}
	
	public void getSelectionTotalAmount(ActionEvent event) {
		try {	
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);			
			Criteria criteria = new Criteria();
			String idAlias = invoiceBean.getFieldName(IEntityAlias.INVOICE_ID);
			ProjectionList pl = new ProjectionList( Projection.property(idAlias) );
			Expression exp = ExpressionUtilities.getSubQueryExpression(Invoice.class, getCriteria(), pl);
			criteria.addInExpression(idAlias, exp);
			Projection amountProjection = Projection.sum(invoiceBean.getFieldName(IEntityAlias.INVOICE_TOTAL));
			Double amount = (Double)invoiceBean.getUniqueResult(amountProjection, criteria);
			setTotalInvoiceAmount(CommonUtil.round(amount==null?0:amount));
		} catch (ManagerBeanException e) {
			String message = "Imposible obtener el total";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}		
	}
	
	public void onViewAccountEntry(ActionEvent event) {
		try {
			BasicController entryController = (BasicController) FormUtil.getController(IFinanceConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), getAccountEntryId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
			entryController.setBackAction(getBeanName() + "_form");
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar el apunte.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
}
