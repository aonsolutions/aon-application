package com.code.aon.ui.finance.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
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
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceAttachment;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.RectificationInvoicingManager;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.project.Project;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.tas.ProjectTas;
import com.code.aon.tas.enumeration.ProjectStatus;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.util.FinanceEmailUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.sign.controller.ISignatureController;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.webmail.SecurityInfo;

public class InvoiceController extends BasicController implements ISignatureController, IFinanceConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class.getName());
	
	private String invoiceAddressControllerName;
	private String invoiceDetailControllerName;
	private String invoiceFinanceControllerName;
	private IPriceStrategy priceStrategy;
	private FinanceGenerator financeGenerator;
	private AccountEntryInvoiceWriter accountWriter;
	private List<SelectItem> addresses;
	private List<SelectItem> projects;
	private boolean showInvoiceAddressWindow;
	private boolean showProjectWindow;
	private boolean showDetailProjectWindow;
	private boolean showRectificationWindow;
	private String rectificationSeries;
	private int rectificationNumber;
	private Date rectificationDate;
	private String rectificationCause;
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
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
			Iterator<?> iterator = rAddressBean.getList(criteria).iterator();
			while(iterator.hasNext()) {
				RegistryAddress address = (RegistryAddress)iterator.next();
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
		return ((iAddress.getFullAddress().length()>30)?iAddress.getFullAddress().substring(0,27)+"...":iAddress.getFullAddress());
	}

	public String getCity() {
		IAddress iAddress = getInvoice().getRegistryAddress();
		BasicController addressController = (BasicController)FormUtil.getController(invoiceAddressControllerName);
		if (addressController.getTo() != null && ((InvoiceAddress)addressController.getTo()).getId() != null) {
			iAddress = (InvoiceAddress)addressController.getTo();
		}
		return iAddress.getCity();
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
	
	public void loadProjects(Integer id) throws ManagerBeanException {
		this.projects = new LinkedList<SelectItem>();
		if (id != null) {
			IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
			Criteria criteria = new Criteria();
			if (getInvoice().getType() == InvoiceType.SALES) {
				criteria.addEqualExpression(projectBean.getFieldName(IProjectAlias.PROJECT_REGISTRY_ID), id);
			}
			criteria.addEqualExpression(projectBean.getFieldName(IProjectAlias.PROJECT_ACTIVE), new Boolean(true));
			criteria.addOrder(projectBean.getFieldName(IProjectAlias.PROJECT_NAME));
			Iterator<?> iterator = projectBean.getList(criteria).iterator();
			while(iterator.hasNext()) {
				Project project = (Project)iterator.next();
				SelectItem item = new SelectItem(project, project.getName());
				projects.add(item);
			}
		}
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
			projectTas.setStatus(ProjectStatus.PENDING);
			projectTasBean.update(projectTas);
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
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), to.getId());
		if (link) {
			criteria.addNullExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_PROJECT));
		} else {
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_PROJECT_ID), project.getId());
		}
		for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			invoiceDetail.setProject((link) ? project : null);
			invoiceDetail.setUpdateEnabled(false);
			invoiceDetailBean.update(invoiceDetail);
		}
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

	public void onRectificationShow(ActionEvent event) throws ManagerBeanException {
		setRectificationSeries(obtainRectificationSeries(getInvoice().getSeries()));
		setRectificationNumber(obtainMaxRectificationNumber(getRectificationSeries()));
		setRectificationDate(new Date());
		setRectificationCause(null);
	}

	private String obtainRectificationSeries(String seriesId) throws ManagerBeanException {
		if (StringUtils.isNotEmpty(seriesId)) {
			Series series = (Series)BeanManager.getManagerBean(Series.class).get(seriesId);
			if (series != null && series.isRectification()) {
				return series.getId();
			}
		}
		return null;
	}

	public void onRectificationSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		setRectificationNumber(obtainMaxRectificationNumber((String)event.getNewValue()));
	}

	private int obtainMaxRectificationNumber(String seriesId) {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	public void onRectify(ActionEvent event) throws ManagerBeanException {
		RectificationInvoicingManager rectificationManager = new RectificationInvoicingManager();
		Invoice rectifier = rectificationManager.rectifyInvoice(getInvoice(), getRectificationSeries(), getRectificationNumber(), getRectificationDate(), getRectificationCause());

		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IFinanceAlias.INVOICE_ID), rectifier.getId());
		onSearch(event);
		getModel().setRowIndex(0);
		onSelect(event);
	}

	public String rectificationRedirect() {
		return SALE_INVOICE_FORM_NAME;
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

	public double getToInvoiceFinanceTotal() throws ManagerBeanException {
		double financeTotal = 0;
		IController feeFinanceController = FormUtil.getController(invoiceFinanceControllerName);
		Iterator<?> iterator = feeFinanceController.getManagerBean().getList(feeFinanceController.getCriteria()).iterator();
		while(iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			financeTotal += finance.getAmount();
		}
		return CommonUtil.round(financeTotal);
	}

	public String getPayMethod() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getModel().getRowData();
		String payMethodName = null;
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iterator = financeBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			if (payMethodName == null) {
				payMethodName = (finance.getPayMethod() != null) ? finance.getPayMethod().getName() : null;
			}
			if (finance.getPayMethod() != null && !finance.getPayMethod().getName().equals(payMethodName)) {
				return "MULTIPLE";
			}
		}
		return payMethodName;
	}

	public boolean isRemovable() {
		InvoiceDetailController invoiceDetailController = (InvoiceDetailController)FormUtil.getController(invoiceDetailControllerName);
		return (invoiceDetailController.getTo() == null && InvoiceStatus.PENDING == getInvoice().getStatus());
	}
	
	public boolean isAccountSource() throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.ACCOUNT);
		return (invoiceDetailBean.getCount(criteria) > 0);
	}

	public void generateFinances(ActionEvent event) throws ManagerBeanException{
		Invoice invoice = getInvoice();
		try {
			IController invoiceFinanceController = FormUtil.getController(invoiceFinanceControllerName);
			List<?> financeList = invoiceFinanceController.getManagerBean().getList(invoiceFinanceController.getCriteria());
			if (existFinanceTrackings(financeList)) {
				AonUtil.addInfoMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.VALIDATE_FINANCES_GENERATION_ERROR_KEY);
				throw new AbortProcessingException();
			}
			Iterator<?> iterator = financeList.iterator();
			while(iterator.hasNext()) {
				Finance finance = (Finance)iterator.next();
				invoiceFinanceController.getManagerBean().remove(finance);
			}
			getFinanceGenerator().generateFinances(invoice, getToInvoiceTotalPrice());
			invoiceFinanceController.onSearch(null);
		} catch (ManagerBeanException e) {
			String msg = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.GENERATE_FINANCES_ERROR_KEY) + ". " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	private boolean existFinanceTrackings(List<?> financeList) throws ManagerBeanException {
		IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Iterator<?> iterator = financeList.iterator();
		while(iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
			criteria.addNotEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TYPE), FinanceTrackingType.FRACTIONED);
			if (trackingBean.getCount(criteria) > 0) {
				return true;
			}
		}
		return false;
	}

	public void onRecordInvoice(ActionEvent event) throws ManagerBeanException{
		double invoiceTotal = getToInvoiceTotalPrice();
		double financeTotal = getToInvoiceFinanceTotal();
		if (financeTotal != 0 && invoiceTotal != financeTotal) {
			String message = AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.UNABLE_RECORD_INACCURACY_ERROR_KEY);
			throw new AbortProcessingException(message);
		}

		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			Invoice invoice = getInvoice();
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
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			Invoice invoice = getInvoice();
			getManagerBean().restoreNullSubPOJOs(invoice);
			getAccountWriter().unrecordAndUpdateInvoice(invoice);

			HibernateUtil.commitTransaction(sessionName);

			// En el caso de que se haya accedido al mantenimiento de facturas desde el mantenimiento de apuntes,
			// hay que tener en cuenta que al descontabilizar la factura, se está borrando el apunte del que 
			// provienes. De tal forma, se sobreescribe la funcionalidad del botón Volver, para que vaya a la 
			// pantalla de búsqueda de apuntes, ejecutando el actionListener correspondiente. 
			if (ObjectUtils.equals(IFinanceConstants.ACCOUNT_ENTRY_FORM_PAGE, this.backAction())) {
				setBackAction(IFinanceConstants.ACCOUNT_ENTRY_SEARCH_PAGE);
				setBackActionListener(IFinanceConstants.ACCOUNT_ENTRY_ON_EDIT_SEARCH_ACTION);
				AonUtil.addWarningMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_UNRECORD_INVOICE_WARNING);
			}
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

	public Integer getAccountEntryId() throws ManagerBeanException {
    	Invoice invoice = getInvoice();
		if (invoice != null && invoice.getId() != null) {
			IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryInvoiceBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID), invoice.getId());
			Iterator<?> iterator = accountEntryInvoiceBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				AccountEntryInvoice accountEntryInvoice = (AccountEntryInvoice)iterator.next();
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
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addNotEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DIRECT_INVOICE);
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
		return IFinanceAlias.INVOICE_ATTACHMENT_MIME_TYPE;
	}

	@Override
	public String getAttachmentParentAlias() {
		return IFinanceAlias.INVOICE_ATTACHMENT_INVOICE_ID;
	}

	@Override
	public boolean isSigned(ITransferObject to) {
		return ((Invoice) to).isSigned();
	}

	@Override
	public IAttachment newAttachment(ITransferObject parent) {
		InvoiceAttachment attachment = new InvoiceAttachment();
		attachment.setInvoice((Invoice) parent);
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
		return (SignerController) AonUtil.getRegisteredBean(IFinanceConstants.SALE_INVOICE_SIGNER_CONTROLLER_NAME);
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
	
	public void onSendInvoiceByEmail(ActionEvent event) throws ManagerBeanException, IOException {
		sendInvoiceByEmail(null, true);
	}

	private void sendInvoiceByEmail(SecurityInfo securyInfo, boolean facturae) throws ManagerBeanException, IOException {
		WebMailController webmailController = (WebMailController)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_WEBMAIL);
		if (webmailController.isLogged()) {
			Invoice invoice = getInvoice();
			MessageController messageController = (MessageController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
			messageController.initNewMessage();
			IAttachment attach = getInvoiceData(invoice);
			emailController.initMessageController(messageController, invoice, attach, facturae);
			messageController.setShowNewMessageWindow(true);
			messageController.setSecurityInfo(securyInfo);
		} else {
			AonUtil.addErrorMessageFromBundle(IWebMailConstants.BUNDLE_NAME, IWebMailConstants.NOT_SERVER_CONNECTED);
		}
	}

}
