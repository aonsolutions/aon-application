package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.CALCULATE_FINANCES_AMOUNT_ERROR_KEY;
import static com.code.aon.ui.common.ICommonMessages.CALCULATE_INVOICE_QUANTITY_ERROR_KEY;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_DUPLICATE_EXPENSE_INVOICE_WARNING;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_DUPLICATE_PURCHASE_INVOICE_WARNING;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_ALREADY_RECORDED_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_OPERATION_NOT_ALLOWED_PERIOD_EXCEEDED_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_UNRECORD_INVOICE_WARNING;
import static com.code.aon.ui.common.ICommonMessages.GENERATE_FINANCES_ERROR_KEY;
import static com.code.aon.ui.common.ICommonMessages.GENERATE_INCREASES_ERROR_KEY;
import static com.code.aon.ui.common.ICommonMessages.UNABLE_RECORD_INACCURACY_ERROR_KEY;
import static com.code.aon.ui.common.ICommonMessages.UNABLE_RECORD_NO_AMORTIZATION_ERROR_KEY;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationInvoice;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.InvestAsset;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.config.util.SeriesUtil;
import com.code.aon.customer.Customer;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.facturae.FACeUtil;
import com.code.aon.facturae.FacturaeWriter;
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
import com.code.aon.finance.util.FinanceUtil;
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
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.seller.Seller;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.HeaderObjectController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.util.FinanceEmailUtil;
import com.code.aon.ui.finance.util.InvoiceImportManager;
import com.code.aon.ui.finance.util.InvoiceOcrProcess;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.project.controller.IProjectConstants;
import com.code.aon.ui.project.controller.ProjectCollectionsController;
import com.code.aon.ui.registry.util.RegistryAddressFilter;
import com.code.aon.ui.sign.controller.ISignatureController;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistoryMapValue;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.project.ProjectTas;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.payroll.EnterpriseActivity;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.in.pdf.maker.PdfMaker;
import net.aonsolutions.aon.invoice.communication.InvoiceCommunicator;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.TBAIInformation;
import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.tbai.lroe.LROEInformation;

public class InvoiceController extends HeaderObjectController implements ISignatureController, IFinanceConstants, IAuditableController {

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
	private boolean showInvoiceAddressWindow;
	private boolean showActivityWindow;
	private InvestAsset savedInvestAsset;
	private boolean showInvestAssetWindow;
	private boolean showDetailInvestAssetWindow;
	private boolean showAddRectificationInvoiceWindow;
	private Project savedProject;
	private boolean showProjectLookup;
	private boolean showProjectWindow;
	private boolean showDetailProjectWindow;
	private Seller savedSeller;
	private boolean showSellerWindow;
	private boolean showDetailSellerWindow;
	private boolean showDocumentWindow;
	private boolean showCommentsWindow;
	private boolean showRemarksWindow;
	private boolean showAuditInfoWindow;
	private boolean showFiscalInformationWindow;
	private boolean showLroeWindow;
	private boolean showVerifactuWindow;
	private boolean showAmortizationWindow;
	private boolean showRectificationWindow;
	private String rectificationSeries;
	private int rectificationNumber;
	private boolean rectificationNumberEditable;
	private String rectificationReferenceCode;
	private Date rectificationDate;
	private String rectificationCause;
	private boolean rectificationSettleFinance;
	private boolean showDiscountsWindow;
	private String discountExpression;
	private boolean showDuplicationWindow;
	private String duplicationSeries;
	private int duplicationNumber;
	private boolean duplicationNumberEditable;
	private String duplicationReferenceCode;
	private Registry duplicationRegistry;
	private String duplicationRegistryName;
	private Date duplicationDate;
	private boolean showPaymentDataInListView;
	private boolean showTotalBreakdownInListView;
	private Double listTaxableBase;
	private Double listVatQuota;
	private Double listRetentionQuota;
	private Double listTotal;
	private FinanceEmailUtil emailController;
	private AonFile invoiceAttachFile;
	private RegistryAddressFilter addressesFilter;
	private InvoiceCommunicationConfiguration icc;
	
	private Boolean hasInvoiceDoc;
	private InvoiceDoc invoiceDoc;
	
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
	
	public boolean hasInvoiceDoc() {
		if (hasInvoiceDoc == null) {
			hasInvoiceDoc = getInvoiceDoc() != null;
		}
		return hasInvoiceDoc;
	}
	
	public InvoiceDoc getInvoiceDoc() {
		Invoice invoice = getInvoice();
		if(invoice != null && !isNevv() && invoice.getId() != null
				&& (invoiceDoc == null || AonNumberUtils.notEquals(invoiceDoc.getInvoice(), invoice.getId()))) {
			invoiceDoc = AON.getInvoiceDoc(getOccam(), invoice.getDomain(), invoice.getId()).orElse(null);
		}
		return invoiceDoc;
	}

	public void initializeInvoiceDoc() {
		this.hasInvoiceDoc = null;
		this.invoiceDoc = null;
	}
	
	public AonFile getInvoiceAttachFile() {
		return invoiceAttachFile;
	}
	public void setInvoiceAttachFile(AonFile invoiceAttachFile) {
		if ( this.invoiceAttachFile != null ) {
			this.invoiceAttachFile.clean();	
		}
		this.invoiceAttachFile = invoiceAttachFile;
	}
	public void invoiceAttachFileUploaded(UploadEvent event) {
		AonFile aonFile = AttachmentUtil.fileUploaded(event);
		setInvoiceAttachFile(aonFile);
	}
	public String clearInvoiceAttachUploadData() {
		if ( getInvoiceAttachFile() != null ) {
			getInvoiceAttachFile().clean();	
		}
		return null;
	}

	public RegistryAddressFilter getAddressesFilter() {
		if(addressesFilter==null)
			addressesFilter = new RegistryAddressFilter(this.getInvoice().getRegistry());
		return addressesFilter;
	}
	
	public void selectFilteredAddress(ActionEvent event) throws ManagerBeanException {
		if(addressesFilter.getModel().isRowAvailable())
			this.getInvoice().setRegistryAddress(addressesFilter.getSelectedAddress());
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
		this.addressesFilter = null;
		if (id != null) {
			List<ITransferObject> list = getAddresses(id);
			for (ITransferObject ito : list) {
				RegistryAddress address = (RegistryAddress)ito;
				String addressLabel = address.getFullAddress();
				addressLabel = ((addressLabel.length()>30)?addressLabel.substring(0,27)+"...":addressLabel) + " - " + address.getCity();
				addressLabel = ((addressLabel.length()>48)?addressLabel.substring(0,45)+"...":addressLabel);
				SelectItem item = new SelectItem(address, addressLabel);
				addresses.add(item);
			}
		}
	}
	
	private List<ITransferObject> getAddresses(Integer registryId) throws ManagerBeanException {
		if (registryId != null) {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), registryId);
			criteria.addOrder(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE));
			return rAddressBean.getList(criteria);
		}
		return null;
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

	public boolean isProjectListEnabled() {
		return getProjectCount() > 0 && getProjectCount() <= 20;
	}

	public void loadProjects(Integer registryId) throws ManagerBeanException {
		this.projects = new LinkedList<SelectItem>();
		if (registryId != null) {
			ProjectCollectionsController projectColls = (ProjectCollectionsController)AonUtil.getRegisteredBean(IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME);
			if (projectColls.getProjectsCount(registryId) <= 20) {
				projects.addAll(projectColls.getProjects(registryId));
			}
		}
	}

	public boolean isShowRegistryDataWindow() {
		return showRegistryDataWindow;
	}

	public void setShowRegistryDataWindow(boolean value) {
		this.showRegistryDataWindow = value;
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

	public boolean isShowActivityWindow() {
		return showActivityWindow;
	}

	public void setShowActivityWindow(boolean value) {
		this.showActivityWindow = value;
	}

	public void addInvoiceActivity(ActionEvent event) throws ManagerBeanException {
		acceptInvoice(event);
		linkInvestAsset(getInvoice(), false);
	}

	public InvestAsset getSavedInvestAsset() {
		return savedInvestAsset;
	}

	public void setSavedInvestAsset(InvestAsset savedInvestAsset) {
		this.savedInvestAsset = null;
		if (savedInvestAsset != null && savedInvestAsset.getId() != null) {
			this.savedInvestAsset = new InvestAsset();
			this.savedInvestAsset.setId(savedInvestAsset.getId());
		}
	}

	public boolean isShowInvestAssetWindow() {
		return showInvestAssetWindow;
	}

	public void setShowInvestAssetWindow(boolean value) {
		this.showInvestAssetWindow = value;
	}

	public boolean isShowDetailInvestAssetWindow() {
		return showDetailInvestAssetWindow;
	}

	public void setShowDetailInvestAssetWindow(boolean value) {
		this.showDetailInvestAssetWindow = value;
	}

	public void addInvoiceInvestAsset(ActionEvent event) throws ManagerBeanException {
		linkInvestAsset(getInvoice(), false);
	}

	public void linkInvestAsset(Invoice invoice, boolean onlyDetails) throws ManagerBeanException {
		boolean detailsChanged = false;
		InvestAsset investAsset = (invoice.getInvestAsset() != null && invoice.getInvestAsset().getId() != null) ? invoice.getInvestAsset() : null;
		setSavedInvestAsset((getSavedInvestAsset() != null && getSavedInvestAsset().getId() != null) ? getSavedInvestAsset() : null);
		if ((investAsset == null && getSavedInvestAsset() != null) || (investAsset != null && !investAsset.equals(getSavedInvestAsset()))) {
			if (!onlyDetails) {
				invoice.setUpdateEnabled(!invoice.isRecorded());
				getManagerBean().restoreNullSubPOJOs(invoice);
				getManagerBean().update(invoice);
				getManagerBean().initializePOJO(invoice);
			}

			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			if (investAsset == null) {
				criteria.addNotNullExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVEST_ASSET));
			} else {
				if (getSavedInvestAsset() == null) {
					criteria.addNullExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVEST_ASSET));
				} else {
					criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVEST_ASSET_ID), getSavedInvestAsset().getId());
				}
			}
			for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
				invoiceDetail.setInvestAsset(investAsset);
				invoiceDetail.setUpdateEnabled(true);
				invoiceDetailBean.update(invoiceDetail);
				detailsChanged = true;
			}
		}
		setSavedInvestAsset(invoice.getInvestAsset());

		if (!onlyDetails && detailsChanged) {
			IController invoiceDetailController = FormUtil.getController(invoiceDetailControllerName);
			invoiceDetailController.onSearch(null);
		}
	}

	public Project getSavedProject() {
		return savedProject;
	}

	public void setSavedProject(Project savedProject) {
		this.savedProject = null;
		if (savedProject != null && savedProject.getId() != null) {
			this.savedProject = new Project();
			this.savedProject.setId(savedProject.getId());
		}
	}

	public boolean isShowProjectLookup() {
		return showProjectLookup;
	}

	public void setShowProjectLookup(boolean value) {
		this.showProjectLookup = value;
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
		linkProject(getInvoice(), false);
	}

	public void linkProject(Invoice invoice, boolean onlyDetails) throws ManagerBeanException {
		boolean detailsChanged = false;
		Project project = (invoice.getProject() != null && invoice.getProject().getId() != null) ? invoice.getProject() : null;
		setSavedProject((getSavedProject() != null && getSavedProject().getId() != null) ? getSavedProject() : null);
		if ((project == null && getSavedProject() != null) || (project != null && !project.equals(getSavedProject()))) {
			if (!onlyDetails) {
				invoice.setUpdateEnabled(!invoice.isRecorded());
				getManagerBean().restoreNullSubPOJOs(invoice);
				getManagerBean().update(invoice);
				getManagerBean().initializePOJO(invoice);
			}

			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			if (project == null) {
				criteria.addNotNullExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_PROJECT));
			} else {
				if (getSavedProject() == null) {
					criteria.addNullExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_PROJECT));
				} else {
					criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_PROJECT_ID), getSavedProject().getId());
				}
			}
			for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
				invoiceDetail.setProject(project);
				invoiceDetail.setUpdateEnabled(false);
				invoiceDetailBean.update(invoiceDetail);
				detailsChanged = true;
			}
		}
		setSavedProject(invoice.getProject());

		if (!onlyDetails && detailsChanged) {
			IController invoiceDetailController = FormUtil.getController(invoiceDetailControllerName);
			invoiceDetailController.onSearch(null);
		}
	}

	public Seller getSavedSeller() {
		return savedSeller;
	}

	public void setSavedSeller(Seller savedSeller) {
		this.savedSeller = null;
		if (savedSeller != null && savedSeller.getId() != null) {
			this.savedSeller = new Seller();
			this.savedSeller.setId(savedSeller.getId());
		}
	}

	public boolean isShowSellerWindow() {
		return showSellerWindow;
	}

	public void setShowSellerWindow(boolean value) {
		this.showSellerWindow = value;
	}

	public boolean isShowDetailSellerWindow() {
		return showDetailSellerWindow;
	}

	public void setShowDetailSellerWindow(boolean value) {
		this.showDetailSellerWindow = value;
	}

	public void addInvoiceSeller(ActionEvent event) throws ManagerBeanException {
		linkSeller(getInvoice(), false);
	}

	public void linkSeller(Invoice invoice, boolean onlyDetails) throws ManagerBeanException {
		boolean detailsChanged = false;
		Seller seller = (invoice.getSeller() != null && invoice.getSeller().getId() != null) ? invoice.getSeller() : null;
		setSavedSeller((getSavedSeller() != null && getSavedSeller().getId() != null) ? getSavedSeller() : null);
		if ((seller == null && getSavedSeller() != null) || (seller != null && !seller.equals(getSavedSeller()))) {
			if (!onlyDetails) {
				invoice.setUpdateEnabled(!invoice.isRecorded());
				getManagerBean().restoreNullSubPOJOs(invoice);
				getManagerBean().update(invoice);
				getManagerBean().initializePOJO(invoice);
			}

			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			if (seller == null) {
				criteria.addNotNullExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SELLER));
			} else {
				if (getSavedSeller() == null) {
					criteria.addNullExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SELLER));
				} else {
					criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SELLER_ID), getSavedSeller().getId());
				}
			}
			for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
				invoiceDetail.setSeller(seller);
				invoiceDetail.setUpdateEnabled(false);
				invoiceDetailBean.update(invoiceDetail);
				detailsChanged = true;
			}
		}
		setSavedSeller(invoice.getSeller());

		if (!onlyDetails && detailsChanged) {
			IController invoiceDetailController = FormUtil.getController(invoiceDetailControllerName);
			invoiceDetailController.onSearch(null);
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
	
	public boolean isShowLroeWindow() {
		return showLroeWindow;
	}

	public void setShowLroeWindow(boolean showLroeWindow) {
		this.showLroeWindow = showLroeWindow;
	}
	
	public boolean isShowVerifactuWindow() {
		return showVerifactuWindow;
	}

	public void setShowVerifactuWindow(boolean showVerifactuWindow) {
		this.showVerifactuWindow = showVerifactuWindow;
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
	
	public boolean isRectificationNumberEditable() {
		return rectificationNumberEditable;
	}

	public void setRectificationNumberEditable(boolean rectificationNumberEditable) {
		this.rectificationNumberEditable = rectificationNumberEditable;
	}

	public String getRectificationReferenceCode() {
		return rectificationReferenceCode;
	}

	public void setRectificationReferenceCode(String rectificationReferenceCode) {
		this.rectificationReferenceCode = rectificationReferenceCode;
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
		Invoice invoice = getInvoice();
		if (!FinanceUtil.isValidLimitRectificationDate(invoice)) {
			String message = AonUtil.addErrorMessageFromBundle(FINANCE_OPERATION_NOT_ALLOWED_PERIOD_EXCEEDED_ERROR);
			throw new AbortProcessingException(message);
		}

		if (invoice.isSales()) {
			setRectificationSeries(SeriesUtil.ensureRectificationSeries(invoice.getSeries()));
			if (SeriesUtil.getFirstRectificationSeries() == null) {
				String message = "Es obligatorio indicar una serie para rectificaciones de ventas. Defina una en Configuraci\u00f3n >> Series.";
				AonUtil.addErrorMessage(message);
				throw new AbortProcessingException(message);
			}
		}
		setRectificationNumber(0);
		setRectificationNumberEditable(false);
		setRectificationReferenceCode(null);
		setRectificationDate(new Date());
		setRectificationCause(null);
		setRectificationSettleFinance(true);
	}

	public void onRectificationSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (isRectificationNumberEditable()) {
			updateRectificationNumber((String)event.getNewValue());
		}
	}
	
	public void onRectificationNumberEditable(ActionEvent event) throws ManagerBeanException {
		updateRectificationNumber(getRectificationSeries());		
	}			
	
	private void updateRectificationNumber(String seriesId) {
		setRectificationNumber(obtainMaxNumber(seriesId));
	}	

	public void onRectify(ActionEvent event) throws ManagerBeanException {
		RectificationInvoicingManager manager = new RectificationInvoicingManager();
		Invoice rectifier = null;
		if ( AonDateUtils.isBefore( getRectificationDate(), getInvoice().getIssueDate()) ) {
			String message = AonError.INVOICE_INVALID_RECTIFICATION_DATE.getMessage();
			AonUtil.addErrorMessage( message );
			throw new AbortProcessingException(message);
		}
		if (getInvoice().isSales()) {
			if (getRectificationNumber() == 0) {
				updateRectificationNumber(getRectificationSeries());
			}
			if(hasCommunication()) {
				String domainName = AonUtil.getDomainName();
				Integer domainId = DomainManager.getCurrentDomain();
				Integer number = AON.getInvoiceMinNumber(domainName, domainId, "", com.esferalia.aon.occam.api.model.type.InvoiceType.SALES, getRectificationSeries());
				setRectificationNumber(number < 0 ? number : -1);
			}
			rectifier = manager.rectifyInvoice(getInvoice()
				, getRectificationSeries()
				, getRectificationNumber()
				, getRectificationDate()
				, getRectificationCause()
				, getRectificationSettleFinance()
				, hasCommunication());
		} else {
			rectifier = manager.rectifyReceivedInvoice(getInvoice()
				, getRectificationReferenceCode()
				, getRectificationDate()
				, getRectificationCause()
				, getRectificationSettleFinance()
				, hasCommunication());
		}
		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.INVOICE_ID), rectifier.getId());
		onSearch(event);
		getModel().setRowIndex(0);
		onSelect(event);
	}
	
	public Occam getOccam() {
		return new Occam()
				.setDomain(DomainManager.getCurrentDomain())
				.setDomainName(AonUtil.getDomainName())
				.setUser("");
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
		this.discountExpression = StringUtils.trimToNull(discountExpression);
	}

	public boolean isShowDuplicationWindow() {
		return showDuplicationWindow;
	}

	public void setShowDuplicationWindow(boolean value) {
		this.showDuplicationWindow = value;
	}
	
	public String getDuplicationSeries() {
		return duplicationSeries;
	}

	public void setDuplicationSeries(String duplicationSeries) {
		this.duplicationSeries = duplicationSeries;
	}

	public int getDuplicationNumber() {
		return duplicationNumber;
	}

	public void setDuplicationNumber(int duplicationNumber) {
		this.duplicationNumber = duplicationNumber;
	}
	
	public boolean isDuplicationNumberEditable() {
		return duplicationNumberEditable;
	}

	public void setDuplicationNumberEditable(boolean duplicationNumberEditable) {
		this.duplicationNumberEditable = duplicationNumberEditable;
	}

	public String getDuplicationReferenceCode() {
		return duplicationReferenceCode;
	}

	public void setDuplicationReferenceCode(String duplicationReferenceCode) {
		this.duplicationReferenceCode = duplicationReferenceCode;
	}
	
	public Registry getDuplicationRegistry() {
		return duplicationRegistry;
	}

	public void setDuplicationRegistry(Registry duplicationRegistry) {
		this.duplicationRegistry = duplicationRegistry;
	}
	
	public String getDuplicationRegistryName() {
		return duplicationRegistryName;
	}

	public void setDuplicationRegistryName(String duplicationRegistryName) {
		this.duplicationRegistryName = duplicationRegistryName;
	}
	
	public Date getDuplicationDate() {
		return duplicationDate;
	}

	public void setDuplicationDate(Date duplicationDate) {
		this.duplicationDate = duplicationDate;
	}
	
	public void onDuplicationShow(ActionEvent event) throws ManagerBeanException {
		Invoice invoice = getInvoice();
		if (!FinanceUtil.isValidLimitDate(invoice)) {
			String message = AonUtil.addErrorMessageFromBundle(FINANCE_OPERATION_NOT_ALLOWED_PERIOD_EXCEEDED_ERROR);
			throw new AbortProcessingException(message);
		}

		if (invoice.isSales()) {
			setDuplicationSeries(SeriesUtil.ensureInvoiceSeries(invoice.getSeries()));
		}
		setDuplicationNumber(0);
		setDuplicationNumberEditable(false);
		setDuplicationReferenceCode(null);
		setDuplicationRegistry(invoice.getRegistry());
		setDuplicationRegistryName(invoice.getRegistryName());
		setDuplicationDate(new Date());
	}

	public void onDuplicationSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (isDuplicationNumberEditable()) {
			updateDuplicationNumber((String)event.getNewValue());
		}
	}
	
	public void onDuplicationNumberEditable(ActionEvent event) throws ManagerBeanException {
		updateDuplicationNumber(getDuplicationSeries());		
	}			
	
	private void updateDuplicationNumber(String seriesId) {
		setDuplicationNumber(obtainMaxNumber(seriesId));
	}	

	public void onDuplicationRegistryChanged(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			IRegistry iRegistry = (IRegistry)event.getNewValue();
			setDuplicationRegistry(iRegistry.getRegistry());
			setDuplicationRegistryName(iRegistry.getRegistry().getName());
		}
	}

	public void onDuplicate(ActionEvent event) throws ManagerBeanException {
		Invoice to = getInvoice();
		if (to.isSales()) {
			if (StringUtils.isBlank(getDuplicationSeries())) {
	        	setDuplicationSeries(null);
	        }		
	        if (getDuplicationNumber() == 0) {
	        	updateDuplicationNumber(getDuplicationSeries());
			}		
	        if(hasCommunication()) {
				String domainName = AonUtil.getDomainName();
				Integer domainId = DomainManager.getCurrentDomain();
				Integer number = AON.getInvoiceMinNumber(domainName, domainId, "", com.esferalia.aon.occam.api.model.type.InvoiceType.SALES, getDuplicationSeries());
				setDuplicationNumber(number < 0 ? number : -1);
	        }
		}
		this.getManagerBean().restoreNullSubPOJOs(to);
		InvoiceImportManager manager = new InvoiceImportManager();
		Invoice invoice = manager.copyInvoice(to, getDuplicationSeries(), getDuplicationNumber(), getDuplicationReferenceCode(), getDuplicationRegistry(), 
												getDuplicationRegistryName(), getDuplicationDate(), !to.getRegistry().equals(getDuplicationRegistry()));

		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
		onSearch(event);
		getModel().setRowIndex(0);
		onSelect(event);
	}

	public boolean isShowPaymentDataInListView() {
		return showPaymentDataInListView;
	}

	public void setShowPaymentDataInListView(boolean showPaymentDataInListView) {
		this.showPaymentDataInListView = showPaymentDataInListView;
		if (showPaymentDataInListView) {
			setShowTotalBreakdownInListView(false);
		}
	}

	public boolean isShowTotalBreakdownInListView() {
		return showTotalBreakdownInListView;
	}

	public void setShowTotalBreakdownInListView(boolean showTotalBreakdownInListView) {
		this.showTotalBreakdownInListView = showTotalBreakdownInListView;
		if (showTotalBreakdownInListView) {
			setShowPaymentDataInListView(false);
		}
	}

	public Double getListTaxableBase() {
		return listTaxableBase;
	}

	public void setListTaxableBase(Double listTaxableBase) {
		this.listTaxableBase = listTaxableBase;
	}
	
	public Double getListVatQuota() {
		return listVatQuota;
	}

	public void setListVatQuota(Double listVatQuota) {
		this.listVatQuota = listVatQuota;
	}
	
	public Double getListRetentionQuota() {
		return listRetentionQuota;
	}

	public void setListRetentionQuota(Double listRetentionQuota) {
		this.listRetentionQuota = listRetentionQuota;
	}
	
	public Double getListTotal() {
		return listTotal;
	}

	public void setListTotal(Double listTotal) {
		this.listTotal = listTotal;
	}
	
	public void acceptInvoice(ActionEvent event) {
		Invoice invoice = getInvoice();
		invoice.setUpdateEnabled(!invoice.isRecorded());
		super.accept(event);
	}
	
	public void updateRemarks(ActionEvent event) {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		Invoice invoice = getInvoice();
		if (invoice != null && invoice.getId() != null) {
			try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
				int i = ctx.getDslContext().update(INVOICE)
					.set(INVOICE.REMARKS, invoice.getRemarks())
				.where(INVOICE.ID.equal( invoice.getId() ))
				.execute();
				LOGGER.info("Observaciones guardadas (" + i + ")");
			}
		}
	}

	public void refreshEntireInvoice() {
		try {
			refresh(null);
			IController invoiceDetailController = FormUtil.getController(invoiceDetailControllerName);
			invoiceDetailController.onSearch(null);
			IController invoiceFinanceController = FormUtil.getController(invoiceFinanceControllerName);
			invoiceFinanceController.onSearch(null);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	@Override
	protected Criteria getSeriesCriteria() {
    	Criteria criteria = new Criteria();
    	InvoiceType type = (getInvoice() != null) ? getInvoice().getType() : InvoiceType.SALES;
    	criteria.addEqualExpression(getTableName().toLowerCase() + ".type", type.ordinal());    	
    	return criteria;
	}

	@Override
	public List<SelectItem> getSeriesCodes() throws ManagerBeanException {
		ConfigCollectionsController configCollections = (ConfigCollectionsController)AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		if (getInvoice() != null && isRectifier()) {
			return configCollections.getRectificationSeriesIds();
		} else {
			return configCollections.getInvoiceSeriesIds();
		}
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

	public void onActivityChanged(ActionEvent event) {
		EnterpriseActivity activity = getInvoice().getActivity();
		InvestAsset asset = getInvoice().getInvestAsset();
		
		if (activity != null && activity.getId() != null && asset != null && asset.getActivity() != null && !activity.equals(asset.getActivity())) {
			getInvoice().setInvestAsset(null);
		}
	}

	public List<SelectItem> getInvestAssets() throws ManagerBeanException {
		List<SelectItem> investAssets = new LinkedList<SelectItem>();
		if (getInvoice().getActivity() == null || getInvoice().getActivity().getId() == null) {
			CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			investAssets.addAll(companyCollections.getActiveCompanyInvestAssets());
		} else {
			IManagerBean investAssetBean = BeanManager.getManagerBean(InvestAsset.class);
			Criteria criteria = new Criteria();
			String alias = investAssetBean.getFieldName(IEntityAlias.INVEST_ASSET_ACTIVITY_ID);
			Expression activityValueExpr = ExpressionUtilities.getEqualExpression(alias, getInvoice().getActivity().getId());
			alias = investAssetBean.getFieldName(IEntityAlias.INVEST_ASSET_ACTIVITY);
			Expression activityNullExpr = ExpressionUtilities.getNullExpression(alias);
			criteria.addExpression(ExpressionUtilities.getOrExpression(activityValueExpr, activityNullExpr));
			criteria.addNullExpression(investAssetBean.getFieldName(IEntityAlias.INVEST_ASSET_END_DATE));
			criteria.addOrder(investAssetBean.getFieldName(IEntityAlias.INVEST_ASSET_DESCRIPTION));
			for (ITransferObject ito : investAssetBean.getList(criteria)) {
				InvestAsset investAsset = (InvestAsset)ito;
				SelectItem item = new SelectItem(investAsset, investAsset.getDescription());
				investAssets.add(item);
			}
		}
		return investAssets;
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

	public boolean isTaxFree() {
		Invoice invoice = getInvoice();
		return invoice.isVatFree() && (invoice.isRetentionFree() || !invoice.isWithholding());
	}

	public double getToInvoiceTotalQuantity() {
		return getInvoiceTotalQuantity(getInvoice());
	}

	public double getInvoiceTotalQuantity() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return getInvoiceTotalQuantity(invoice);
	}

	private double getInvoiceTotalQuantity(Invoice invoice) {
		double totalQuantity = 0;
		try {
			IManagerBean detailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			Projection projection = Projection.sum(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_QUANTITY));
			Object value = detailBean.getUniqueResult(projection, criteria);
			totalQuantity = (value != null) ? ((Double)value) : 0;
		} catch (ManagerBeanException ex) {
			String msg = AonUtil.getMessage(CALCULATE_INVOICE_QUANTITY_ERROR_KEY) + ". " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
		return totalQuantity;
	}

	public double getTaxableBase() {
		return getPriceStrategy().getTaxableBase((ICalculableContainer)getTo());
	}

	public double getVatQuota() {
		return getPriceStrategy().getTotalVatQuota((ICalculableContainer)getTo(), (ITaxInfo)getTo());
	}

	public double getRetentionQuota() {
		return getPriceStrategy().getTotalRetentionQuota((ICalculableContainer)getTo(), (ITaxInfo)getTo());
	}

	public double getToInvoiceTotalPrice() {
		return getInvoiceTotalPrice(getInvoice());
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
	
	public boolean isTediSource() throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.TEDI);
		return (invoiceDetailBean.getCount(criteria) > 0);
	}

	public void onApplyDiscountsShow(ActionEvent event) throws ManagerBeanException {
		Invoice invoice = getInvoice();
		if (!invoice.isRecorded() && checkRecorded(invoice)) {
			refreshEntireInvoice();
			String message = AonUtil.addErrorMessageFromBundle(FINANCE_INVOICE_ALREADY_RECORDED_ERROR);
			throw new AbortProcessingException(message);
		}
	}

	public void onApplyDiscounts(ActionEvent event) throws ManagerBeanException {
		Invoice invoice = getInvoice();
		if (!invoice.isRecorded() && checkRecorded(invoice)) {
			refreshEntireInvoice();
			String message = AonUtil.addErrorMessageFromBundle(FINANCE_INVOICE_ALREADY_RECORDED_ERROR);
			throw new AbortProcessingException(message);
		}

		applyDiscounts(invoice);
	}

	private void applyDiscounts(Invoice invoice) throws ManagerBeanException {
		List<ITransferObject> detailList = invoice.getDetailList();
		if (detailList.size() > 0) {
			IController invoiceDetailController = FormUtil.getController(invoiceDetailControllerName);
			for (ITransferObject ito : detailList) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
				invoiceDetail.getDiscountExpression().setDiscountExpr(getDiscountExpression());
				invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
				invoiceDetail.setSkipServiceProcess(true);
				invoiceDetail.getInvoice().setUpdateEnabled(detailList.lastIndexOf(invoiceDetail) == detailList.size()-1);
				invoiceDetailController.getManagerBean().update(invoiceDetail);
			}
			refresh(null);
			invoiceDetailController.onSearch(null);

			autoGenerateIncreases();
			autoGenerateFinances();
			resetListTotals();
		}
	}

	public void autoGenerateIncreases() {
		if (getInvoice().isSales()) {
			try {
				List<ITransferObject> increaseDetails = getInvoice().getIncreaseDetails();
				if (increaseDetails.size() > 0) {
					IController invoiceDetailController = FormUtil.getController(invoiceDetailControllerName);
					double taxableBase = getInvoice().getTaxableBase();
					for (ITransferObject ito : increaseDetails) {
						InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
						taxableBase = CommonUtil.round(taxableBase - invoiceDetail.getTaxableBase());
					}
					for (ITransferObject ito : increaseDetails) {
						InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
						invoiceDetail.setPrice(taxableBase);
						invoiceDetail.setTaxableBase(CommonUtil.round(taxableBase - getPriceStrategy().getBasePrice(invoiceDetail)));
						invoiceDetail.getInvoice().setUpdateEnabled(increaseDetails.lastIndexOf(invoiceDetail) == increaseDetails.size()-1);
						invoiceDetailController.getManagerBean().update(invoiceDetail);
					}
					refresh(null);
					invoiceDetailController.setModel(null);
				}
			} catch (ManagerBeanException e) {
				String msg = AonUtil.getMessage(GENERATE_INCREASES_ERROR_KEY) + ". " + e.getMessage();
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg,e);
			}
		}
	}

	public void onGenerateFinances(ActionEvent event) {
		Invoice invoice = getInvoice();
		if (!invoice.isRecorded() && checkRecorded(invoice)) {
			refreshEntireInvoice();
			String message = AonUtil.addErrorMessageFromBundle(FINANCE_INVOICE_ALREADY_RECORDED_ERROR);
			throw new AbortProcessingException(message);
		}

		generateFinances(true);
		FormUtil.getController(invoiceFinanceControllerName).onSearch(null);
	}

	public void autoGenerateFinances() {
		if (getFinanceGenerationMode() == 0) {
			try {
				if (getInvoice().getRegistry().getPayMethod() != null) {
					generateFinances(false);
				} else {
					synchronizeFinances(false, false);
				}
				FormUtil.getController(invoiceFinanceControllerName).onSearch(null);
			} catch (ManagerBeanException e) {
				String msg = AonUtil.getMessage(GENERATE_FINANCES_ERROR_KEY) + ". " + e.getMessage();
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg,e);
			}
		}
	}

	private void generateFinances(boolean forceRemove) {
		try {
			synchronizeFinances(forceRemove, true);

			double pendingAmount = getPendingAmount();
			if (pendingAmount != 0) {
				getFinanceGenerator().generateFinances(getInvoice(), pendingAmount);
			}
		} catch (ManagerBeanException e) {
			String msg = AonUtil.getMessage(GENERATE_FINANCES_ERROR_KEY) + ". " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	private void synchronizeFinances(boolean forceRemove, boolean removeAutoFinance) throws ManagerBeanException {
		InvoiceFinanceController invoiceFinanceController = (InvoiceFinanceController)FormUtil.getController(invoiceFinanceControllerName);
		List<ITransferObject> financeList = invoiceFinanceController.getManagerBean().getList(invoiceFinanceController.getCriteria());
		for (ITransferObject ito : financeList) {
			Finance finance = (Finance)ito;
			if (finance.isPending()) {
				Invoice invoice = getInvoice();
				if (!finance.isAdvance()) {
					if (forceRemove || (removeAutoFinance && !finance.isManual()) || !getInvoice().getRegistry().equals(finance.getRegistry())) {
						invoiceFinanceController.getManagerBean().remove(finance);
					} else {
						finance.setRegistry(invoice.getRegistry());
						finance.setRegistryName(invoice.getRegistryName());
						finance.setRegistryDocument(invoice.getRegistryDocument());
						finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
						finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
						finance.setConcept(invoice.getDocumentNumber());
						finance.setSecurityLevel(invoice.getSecurityLevel());
						invoiceFinanceController.getManagerBean().update(finance);
					}
				} else if (!invoice.getRegistry().equals(finance.getRegistry())) {
					invoiceFinanceController.excludeAdvance(finance);
				}
			}
		}
	}

	public double getPendingAmount() {
		return CommonUtil.round(getToInvoiceTotalPrice() - getToInvoiceFinanceTotal());
	}

	public void onRecordInvoice(ActionEvent event) throws ManagerBeanException {
		double invoiceTotal = AonMathUtils.round(getToInvoiceTotalPrice());
		double financeTotal = AonMathUtils.round(getToInvoiceFinanceTotal());
		if (financeTotal != 0 && invoiceTotal != financeTotal) {
			String message = AonUtil.addErrorMessageFromBundle(UNABLE_RECORD_INACCURACY_ERROR_KEY);
			throw new AbortProcessingException(message);
		}
		Invoice invoice = (Invoice)BeanManager.getManagerBean(Invoice.class).get(getInvoice().getId());
		if (checkRecorded(invoice)) {
			refreshEntireInvoice();
			String message = AonUtil.addErrorMessageFromBundle(FINANCE_INVOICE_ALREADY_RECORDED_ERROR);
			throw new AbortProcessingException(message);
		}
		if (invoice.isInvestment() && !isAmortizationForm()) {
			String message = AonUtil.addErrorMessageFromBundle(UNABLE_RECORD_NO_AMORTIZATION_ERROR_KEY);
			throw new AbortProcessingException(message);
		}
		if (!FinanceUtil.isValidLimitDate(invoice)) {
			String message = AonUtil.addErrorMessageFromBundle(FINANCE_OPERATION_NOT_ALLOWED_PERIOD_EXCEEDED_ERROR);
			throw new AbortProcessingException(message);
		}

		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			getAccountWriter().recordAndUpdateInvoice(invoice);

			HibernateUtil.commitTransaction(sessionName);

			setTo(invoice);
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

			refresh(event);
		}
	}

	public void onUnrecordInvoice(ActionEvent event) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		Invoice invoice = (Invoice)BeanManager.getManagerBean(Invoice.class).get(getInvoice().getId());
		if (!FinanceUtil.isValidLimitDate(invoice)) {
			String message = AonUtil.addErrorMessageFromBundle(FINANCE_OPERATION_NOT_ALLOWED_PERIOD_EXCEEDED_ERROR);
			throw new AbortProcessingException(message);
		}
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			getAccountWriter().unrecordAndUpdateInvoice(invoice);

			HibernateUtil.commitTransaction(sessionName);

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
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);

			refresh(event);
		}
	}

	public boolean checkRecorded(Invoice invoice) {
		String select = "SELECT invoice.id id " +
    					"FROM invoice as invoice " +
    					"WHERE " + DomainManager.getSQLWhereClause("invoice.domain") + " " +
    					"AND invoice.id = " + invoice.getId() + " " +
    					"AND status = 1";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(select);
        return !query.addScalar("id", Hibernate.INTEGER).list().isEmpty();
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
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addNotEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DIRECT_INVOICE);
		return invoiceDetailBean.getCount(criteria) > 0;
	}
	
	public boolean isTediReadOnly() throws ManagerBeanException {
		return isReadOnly() || isTediReadOnly(getInvoice());
	}
	
	private boolean isTediReadOnly(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.TEDI);
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
	public IAttachment newAttachment(ITransferObject parent, MimeType type) {
		InvoiceAttachment attachment = new InvoiceAttachment();
		attachment.setInvoice((Invoice) parent);
		attachment.setType(InvoiceAttachmentType.INVOICE);
		attachment.setMimeType(type);
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
	
	
	public IAttachment generateInvoiceAttachment(Domain domain, Invoice inv, ITransferObject to) {
		IAttachment attachment = null;
		try {
			String login = "";
			Occam occam = new Occam().setDomain(domain.getId()).setDomainName(domain.getName()).setUser(login);
			PrintInvoiceConfiguration config = AON_SOLUTIONS.getPrintInvoiceConfiguration(domain.getName(), domain.getId(), login, true);
			com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domain.getName(), domain.getId(), login, inv.getId());
		
			CompanyFull company = AON.getCompanyFull(domain.getName(), domain.getId(), login);
			Attach logo = new Attach();
		
			if(config.isLogo()) {
				Integer logoId = company.getRegistry().getId();
				logo = AON.getAttach(domain.getName(), domain.getId(), login, f-> f.getAttachModuleProperty().eq(logoId)
						.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
			}
			String qrUrl = domain.getName() + "/dip?source=invoice&id=" + inv.getId() ;  
			String tbaiId = "";
			Date expDate = invoice.getExpDate() != null ? invoice.getExpDate() : invoice.getIssueDate();
			if(isTbai(expDate) || isLroe(expDate)) {
				try(CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)) {
					TbaiData tbaiData = TbaiData.getInstance(ctx, getInvoiceCommunicationConfiguration());
					String tbaiUrl = tbaiData.getTbaiUrl( domain.getId(), invoice.getId());
					qrUrl = AonStringUtils.isBlank(tbaiUrl) ? qrUrl : tbaiUrl;
					tbaiId = tbaiData.getTbaiId(domain.getId(), invoice.getId());
				}
			} else if(icc.isVerifactu(expDate) || icc.isNoVerifactu(expDate)) {
				if (invoice.getCommunicationInfo() != null) {
					InvoiceInfo info = invoice.getCommunicationInfo().get(
						icc.isVerifactu(expDate) 
							? InvoiceCommunicationType.VERIFACTU 
							: InvoiceCommunicationType.NO_VERIFACTU);
					if (info != null) {
						qrUrl = info.getCheckUrl();
					}
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			if(company.getRegistry().getDomain().isGarage()) {	
				invoice.detailStream().forEach(d -> {
					ProjectTas pt = AON.getProjectTas(occam, f -> f.getDomainProperty().eq(invoice.getDomain())
							.and(f.getIdProperty().eq(d.getProject()))).orElse(null);
					if(pt != null)	d.setProjectName(d.getProjectName() + " - KMS. " + d.getProject());	
				});
			}
			
			PdfMaker.printInvoice(out, company, icc, invoice, config, qrUrl, logo.getData(), tbaiId);
			byte[] data = out.toByteArray();
			attachment = newAttachment(to, MimeType.MIME_PDF);
			attachment.setData(data);
			attachment.setMimeType(MimeType.MIME_PDF);
			attachment.setDescription("Factura");
		} catch (Throwable e) {
			LOGGER.error(">>>> onReport " + e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}			
		return attachment;
	}
	
	@Override
	public IAttachment getUnsignedAttachment(ITransferObject to, MimeType type) {
		Invoice invoice = (Invoice) to;
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		Domain domain = AON.getDomain(domainName, domainId, "");

		if(isSaleInvoiceDefault() && MimeType.MIME_PDF == type){
			return generateInvoiceAttachment(domain, invoice, to);
		} else if ( type == MimeType.MIME_PDF ) {
			return generateReportAttachment(to);	
		} else if ( type == MimeType.MIME_XML && isIncludeFacturae(invoice) ) {
			return getUnsignedFacturae(to);	
		}
		return null;
	}	
	
	public boolean isSaleInvoiceDefault() {
		Integer domainId = DomainManager.getCurrentDomain();
		String domainName = AonUtil.getDomainName();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		com.esferalia.aon.occam.api.model.ApplicationParameter appParam = AON.getApplicationParameter(domainName, domainId, login, com.esferalia.aon.occam.api.model.type.AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM);

		com.esferalia.aon.occam.api.model.ApplicationParameter personalized = AON.getApplicationParameter(domainName, domainId, login, com.esferalia.aon.occam.api.model.type.AppParam.REPORT_saleInvoice);
		
		return personalized.isEmpty() && ( appParam.getValue() == null || "default".equalsIgnoreCase(appParam.getValue()));
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
		}
		if ( attach == null ) {
			attach = getUnsignedAttachment(invoice, MimeType.MIME_PDF);
		}
		return attach;		
	}

	public static byte[] getFacturaeData( FacturaeWriter fw, Invoice invoice ) {
		byte[] data = null;
		File invoiceFile = null;
		try {
			invoiceFile = File.createTempFile("facturae ("+invoice.getId() + ")", FacturaeWriter.FACTURAE_EXTENSION );
			String filePath = invoiceFile.getAbsolutePath();
			String fileName = FilenameUtils.getFullPath(filePath) + FilenameUtils.getBaseName(filePath);
			fw.serialize(invoice, fileName);
			data = FileUtils.readFileToByteArray(invoiceFile);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			FileUtils.deleteQuietly(invoiceFile);	
		}
		return data;
	}
	
	private IAttachment getUnsignedFacturae( ITransferObject to ) {
		Invoice invoice = (Invoice) to;
		FacturaeWriter fw = new FacturaeWriter(AonUtil.getCurrentLocale());
		byte[] data = getFacturaeData(fw, invoice);
		MimeType type = FACeUtil.isDefined(invoice) ? MimeType.MIME_XSIG : MimeType.MIME_XML;
		IAttachment attachment = newAttachment(invoice, type);
		attachment.setData(data);
		attachment.setDescription(getDescription(invoice));
		return attachment;		
	}		
	
	public IAttachment getInvoiceFacturae(Invoice invoice) throws ManagerBeanException {
		SignerController signer = getSignerController();
		IAttachment attach = null;
		if (invoice.isSigned()) {
			attach = signer.getSignedFacturae(invoice.getId());
		}
		if ( attach == null ) {
			attach = getUnsignedAttachment(invoice, MimeType.MIME_XML);
		}
		return attach;		
	}
	
	public static boolean isIncludeFacturae( Invoice invoice ) {
		boolean include = false;
		if ( invoice.getType() == InvoiceType.SALES ) {
			if ( getCompanyController().isEInvoice() ) {
				try {
					IManagerBean bean = BeanManager.getManagerBean(Customer.class);
					if ( (invoice.getRegistry()!= null) && (invoice.getRegistry().getId()!=null) ) {
						Customer customer = (Customer) bean.get(invoice.getRegistry().getId());
						include = (customer != null) && customer.isEInvoice();						
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}			
			}			
		}
		return include;
	}
	
	public boolean isDownloadFacturae() {
		return isIncludeFacturae(getInvoice());
	}
	
	public void onSendInvoiceByEmail(ActionEvent event) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {
			try {
				controller.onNewMessage(event);
				Invoice invoice = getInvoice();
				IAttachment attach = getInvoiceData(invoice);
				emailController.initMessageController(controller, invoice, attach, isIncludeFacturae(invoice));
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
	
	public boolean isAmortizationForm () {
		if (!isNevv() && getInvoice().isInvestment()) { 
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
			
			BasicController controller = (BasicController)AonUtil.getRegisteredBean(AMORTIZATION_CONTROLLER_NAME);
			controller.accept(event);
			controller.getManagerBean().restoreNullSubPOJOs(controller.getTo());

			AmortizationInvoice amortizationInvoice = new AmortizationInvoice();
			amortizationInvoice.setAmortization((Amortization)controller.getTo());
			amortizationInvoice.setInvoice(getInvoice());
			BeanManager.getManagerBean(AmortizationInvoice.class).insert(amortizationInvoice);
			
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
			IController controller = (IController)AonUtil.getRegisteredBean(AMORTIZATION_CONTROLLER_NAME);
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
		return (getBackAction() != null);
	}

	public void resetListTotals() {
		setListTaxableBase(null);
		setListVatQuota(null);
		setListRetentionQuota(null);
		setListTotal(null);
	}

	public void obtainListTotals(ActionEvent event) {
		try {	
			Criteria criteria = new Criteria();
			ProjectionList idPrjnList = new ProjectionList(Projection.property(getFieldName(IEntityAlias.INVOICE_ID)));
			criteria.addInExpression(getFieldName(IEntityAlias.INVOICE_ID), ExpressionUtilities.getSubQueryExpression(Invoice.class, getCriteria(), idPrjnList));

			Projection basePrjn = Projection.sum(getFieldName(IEntityAlias.INVOICE_TAXABLE_BASE));
			Projection vatPrjn = Projection.sum(getFieldName(IEntityAlias.INVOICE_VAT_QUOTA));
			Projection retPrjn = Projection.sum(getFieldName(IEntityAlias.INVOICE_RETENTION_QUOTA));
			Projection totalPrjn = Projection.sum(getFieldName(IEntityAlias.INVOICE_TOTAL));
			ProjectionList totalsPrjnList = new ProjectionList(basePrjn, vatPrjn, retPrjn, totalPrjn);
			Object[] result = (Object[])getManagerBean().getUniqueResult(totalsPrjnList, criteria);

			setListTaxableBase((result[0] == null) ? 0 : CommonUtil.round((Double)result[0]));
			setListVatQuota((result[1] == null) ? 0 : CommonUtil.round((Double)result[1]));
			setListRetentionQuota((result[2] == null) ? 0 : CommonUtil.round((Double)result[2]));
			setListTotal((result[3] == null) ? 0 : CommonUtil.round((Double)result[3]));
		} catch (ManagerBeanException e) {
			String message = "Imposible obtener el Total";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}		
	}

	public void onViewAccountEntry(ActionEvent event) {
		try {
			BasicController entryController = (BasicController)FormUtil.getController(IFinanceConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
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

	@Override
	protected String getTableName() {
		return super.getPojoShortName();
	}

	public boolean isPrintWord() {
		return AppParamUtil.getValueAsBoolean(AppParam.APP_PRINT_INVOICE_WORD);
	}	
	
	public static CompanyController getCompanyController() {
		return (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);		
	}
	
	public boolean isDefinedOcrUrl() {
		String url = obtainOcrUrl();
		return url!=null && StringUtils.isNotBlank(url);
	}
	
	private String obtainOcrUrl() {
		ApplicationParameter urlParam = AppParamUtil.getParameter("OCR_URL");
		return urlParam!=null?urlParam.getValue():null;
	}
	
	private InvoiceOcrProcess ocrProcess;
	
	public InvoiceOcrProcess getOcrProcess(){
		if(ocrProcess==null){
			ocrProcess = new InvoiceOcrProcess();
		}
		return ocrProcess;
	}
	
	public void processOcr(ActionEvent event) {
		String url = obtainOcrUrl();
		if(url!=null && StringUtils.isNotBlank(url)){
			String domainName = AonUtil.getDomainName();
			Integer domainId = DomainManager.getCurrentDomain();
			try {
				Invoice invoice = (Invoice) this.getTo();
				InvoiceOcrProcess ocr = new InvoiceOcrProcess();
				ocr.process(url, domainName, domainId, invoice, getInvoiceAttachFile().getData());
				
				loadAddresses(invoice.getRegistry().getId());
				invoice.setRegistry(invoice.getRegistry().getRegistry());
				invoice.setRegistryAddress(invoice.getRegistry().getDefaultAddress());
				invoice.setRegistryDocumentCountry(invoice.getRegistry().getDocumentCountry());
				invoice.setRegistryDocumentType(invoice.getRegistry().getDocumentType());
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		} else {
			AonUtil.addInfoMessage("No se ha definido la URL de procesamiento OCR.");
		}
	}
	
	public String onExcel() {
		try {
			InvoiceExcelReport report = new InvoiceExcelReport();
			report.run(getCriteria());
		} catch (ManagerBeanException th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
		}
		return null;
	}
	
	public boolean isFirstNumberOfSeries() {
		if(AonStringUtils.isBlank(getInvoice().getSeries())) return false; 
	    Integer domainId = DomainManager.getCurrentDomain();
        String domainName = AonUtil.getDomainName();
        String login = UserUtils.getInstance().getLoggedUser().getLogin();
        Byte[] types = new Byte[]{com.esferalia.aon.occam.api.model.type.InvoiceType.SALES.value()};
        int number = AON.getInvoiceNextNumber(domainName, domainId, login, types, getInvoice().getSeries());
        return number <= 1;
	}
	
	Boolean uniqueNumberOfSeries;
	public boolean isUniqueNumberOfSeries() {
	    if(uniqueNumberOfSeries == null) {
	        Integer domainId = DomainManager.getCurrentDomain();
	        String domainName = AonUtil.getDomainName();
	        String login = UserUtils.getInstance().getLoggedUser().getLogin();
	        long l = AON.getInvoiceStream(domainName, domainId, login, f -> f.getDomainProperty().eq(domainId)
	            .and(f.getTypeProperty().eq(com.esferalia.aon.occam.api.model.type.InvoiceType.SALES.value()))
	            .and(f.getSeriesProperty().eq(getInvoice().getSeries()))).count();
	        uniqueNumberOfSeries = l <= 1;
	    }
	    return uniqueNumberOfSeries;
	}
	    
	public boolean isTbaiInvoice() {
		return (isTbai() || isLroe() ) && !AonStringUtils.isBlank(getTbaiUrl());
	}
	
	public boolean isCommunicableInvoice() {
		return getInvoice().getNumber() > 0
			&& ( (isVerifactu() && !AonStringUtils.isBlank(getVerifactuUrl() ))
				|| isNoVerifactu() 
				|| isSif())
		;
	}

	public boolean isVerifactuInvoice() {
		return isVerifactu() && getInvoice().getNumber() > 0 && getInvoice().isSales(); // && !AonStringUtils.isBlank(getVerifactuUrl());
	}
	
	public boolean isNoVerifactuInvoice() {
		return isNoVerifactu() && getInvoice().getNumber() > 0 && getInvoice().isSales(); // && !AonStringUtils.isBlank(getVerifactuUrl());
	}
	
	public boolean isSifInvoice() {
		return isSif() && getInvoice().getNumber() > 0 && getInvoice().isSales(); // && !AonStringUtils.isBlank(getVerifactuUrl());
	}
	
	public boolean isSiiInvoice() {
		return isSii() && getInvoice().getNumber() > 0 && getInvoice().isSales(); // && !AonStringUtils.isBlank(getVerifactuUrl());
	}
	
	String tbaiUrl;
	String verifactuUrl;
	Integer invoiceId;

	public String getTbaiUrl() {
		Invoice invoice = (Invoice) this.getTo();
		if(invoice == null || invoice.getId() == null) tbaiUrl = null;
		if(tbaiUrl == null || (invoice != null && invoice.getId() != null 
				&& !invoice.getId().equals(invoiceId))) {
			if(invoice == null || invoice.getId() == null) return null;
			invoiceId = invoice.getId();
			Integer domainId = DomainManager.getCurrentDomain();
			String domainName = AonUtil.getDomainName();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();

			try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
				tbaiUrl = TbaiData.getInstance(ctx, getInvoiceCommunicationConfiguration()).getTbaiUrl(domainId, invoice.getId());
			}
			if(tbaiUrl == null) tbaiUrl = ""; 
		} 
		return tbaiUrl;
	}
	
	public void setTbaiUrl(String tbaiUrl) {
		this.tbaiUrl = tbaiUrl;
	}
	
	public boolean isIssueable() {
		return !isNevv()
			&& (isVerifactu() || isNoVerifactu() ||  isSif() ||  isSii())
			&& (!isVerifactuInvoice() || !isNoVerifactuInvoice() || !isSifInvoice() || !isSiiInvoice())
			&& (getInvoice().getNumber() <= 0 || isUniqueNumberOfSeries());
	}
	public String getVerifactuUrl() {
		Invoice invoice = (Invoice) this.getTo();
		if(invoice == null || invoice.getId() == null) verifactuUrl = null;
		if(AonStringUtils.isBlank(verifactuUrl) || (invoice != null && invoice.getId() != null 
				&& !invoice.getId().equals(invoiceId))) {
			if(invoice == null || invoice.getId() == null) return null;
			invoiceId = invoice.getId();
			Integer domainId = DomainManager.getCurrentDomain();
			String domainName = AonUtil.getDomainName();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(login);
			verifactuUrl = AON.getInvoiceInfo(occam, invoice.getId(), InvoiceCommunicationType.VERIFACTU)
				.map( InvoiceInfo::getCheckUrl )
				.orElse( "" )
			;
			
////			InvoiceData data = AON.getInvoiceData(domain, user, f -> f.getDomainProperty().eq(domainId)
////				.and(f.getInvoiceProperty().eq(invoiceId))
////				.and(f.getNameProperty().eq("VERIFACTU_QR")));
//			if(data != null) verifactuUrl = data.getValue();					
//			if(verifactuUrl == null) verifactuUrl = ""; 
		} 
		return verifactuUrl;
	}
	
	public void setVerifactuUrl(String verifactuUrl) {
		this.verifactuUrl = verifactuUrl;
	}

	public boolean hasCommunication() { return getInvoiceCommunicationConfiguration().hasCommunication(); }
	
	public boolean isLroe() 		{return getInvoiceCommunicationConfiguration().isLroe();}
	public boolean isTbai() 		{return getInvoiceCommunicationConfiguration().isTbai();}
	public boolean isVerifactu() 	{return getInvoiceCommunicationConfiguration().isVerifactu();}
	public boolean isNoVerifactu() 	{return getInvoiceCommunicationConfiguration().isNoVerifactu();}
	public boolean isSif() 			{return getInvoiceCommunicationConfiguration().isSif();}
	public boolean isSii() 			{return getInvoiceCommunicationConfiguration().isSii();}

	public boolean isLroe(Date date) 		{return getInvoiceCommunicationConfiguration().isLroe(date);}
	public boolean isTbai(Date date) 		{return getInvoiceCommunicationConfiguration().isTbai(date);}
	public boolean isVerifactu(Date date) 	{return getInvoiceCommunicationConfiguration().isVerifactu(date);}
	public boolean isNoVerifactu(Date date) {return getInvoiceCommunicationConfiguration().isNoVerifactu(date);}
	public boolean isSif(Date date) 		{return getInvoiceCommunicationConfiguration().isSif(date);}
	public boolean isSii(Date date) 		{return getInvoiceCommunicationConfiguration().isSii(date);}

	
	public boolean isAraba() 	{return getInvoiceCommunicationConfiguration().isAraba();}
	public boolean isBizkaia() 	{return getInvoiceCommunicationConfiguration().isBizkaia();}
	public boolean isGipuzkoa() {return getInvoiceCommunicationConfiguration().isGipuzkoa();}
	
	public InvoiceCommunicationConfiguration getInvoiceCommunicationConfiguration() {
		if(icc == null) {
			String domainName = AonUtil.getDomainName();
			Integer domainId = DomainManager.getCurrentDomain();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(login);
			icc = AON.getInvoiceCommunicationConfiguration(occam);
		}
		return icc;
	}
	
	public void setInvoiceCommunicationConfiguration(InvoiceCommunicationConfiguration icc) {
		this.icc = icc;
	}
	
	public LROEInformation getLroeInfo() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		Domain domain = new Domain().setName(domainName).setId(domainId);
		User user = new User().setLogin(login);
		LROEInformation a = LroeData.get(domain, user, getInvoice().getId(), com.esferalia.aon.occam.api.model.type.InvoiceType.safeValueOf(getInvoice().getType().ordinal()));
		return a; 		
	}
	
	public TBAIInformation getTbaiInfo() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		Domain domain = new Domain().setName(domainName).setId(domainId);
		User user = new User().setLogin(login);
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return TbaiData.getInstance(ctx, getInvoiceCommunicationConfiguration()).get(domain, user, getInvoice().getId());		
		}
	}
	
	public List<InvoiceCommunicationHistory> getInvoiceCommunicationHistory() {
		try {
			String domainName = AonUtil.getDomainName();
			Integer domainId = DomainManager.getCurrentDomain();
			String login = UserUtils.getInstance().getLoggedUser().getLogin();
			Occam occam = new Occam().setDomain(domainId).setDomainName(domainName).setUser(login);
			Map<InvoiceCommunicationType, InvoiceCommunicationHistoryMapValue> map = InvoiceCommunicator.history(occam, getInvoice().getId());
			return AonCollectionUtils.stream(map)
				.filter(e -> e.getKey() == InvoiceCommunicationType.VERIFACTU 
						|| e.getKey() == InvoiceCommunicationType.NO_VERIFACTU
						|| e.getKey() == InvoiceCommunicationType.SIF)
				.map(Entry::getValue)
				.filter( v -> v != null)
				.map(InvoiceCommunicationHistoryMapValue::getHistory)
				.filter( h -> h != null)
				.findFirst()
				.orElse(new LinkedList<>())
			;
		} catch (Exception e) {
			throw new AbortProcessingException(e);
		}
	}
	
	public String getExpDate() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		Date date = AON_SOLUTIONS.getInvoiceExpDate(domainName, domainId, login, getInvoice().getId());
		if(date == null && isTbaiInvoice()) date = getInvoice().getDate();
		if(date == null) return "Pendiente de emisión";
		else return AonDateUtils.format(date, "dd/MM/yyyy");
	}
	
	public boolean isRectificationInvoiceNull() {
		Invoice invoice = (Invoice) getTo();
		return invoice.getRectificationInvoice() == null
				|| invoice.getRectificationInvoice().getId() == null;
		
	}
	
	public boolean isShowAddRectificationInvoiceWindow() {
		return showAddRectificationInvoiceWindow;
	}

	public void setShowAddRectificationInvoiceWindow(boolean value) {
		this.showAddRectificationInvoiceWindow = value;
	}
	
	public void onRectificationInvoiceChanged(ActionEvent event) {
		Invoice invoice = getInvoice();
		Invoice rectificationInvoice = invoice.getRectificationInvoice();
		
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		
		AON.rectifyInvoice(domainName, domainId, login, invoice.getId(), rectificationInvoice.getId());	
	}
	
	public List<SelectItem> getRectificationInvoices() throws ManagerBeanException {
		List<SelectItem> rectificationInvoices = new LinkedList<>();

		Invoice inv = getInvoice();
		
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_REGISTRY_ID), inv.getRegistry().getId());
		criteria.addNotEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ID), inv.getId());
		Invoice.addNotAnnulledExpression(invoiceBean, criteria);
		criteria.addOrder(invoiceBean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), false);
		for (ITransferObject ito : invoiceBean.getList(criteria)) {
			Invoice invoice = (Invoice)ito;
			SelectItem item = new SelectItem(invoice, invoice.getDate() + " - "  + invoice.getReferenceCode() + " - " + invoice.getTotal());
			rectificationInvoices.add(item);
		}
		return rectificationInvoices;
	}
}
