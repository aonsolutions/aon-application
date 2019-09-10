package com.code.aon.ui.commercial.controller;

import static com.code.aon.ui.common.ICommonMessages.ITEM_SERIALIZABLE_REQUIRED_ERROR;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferAttachment;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.config.util.SeriesUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.OfferInvoicingManager;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.RegistrySeller;
import com.code.aon.registry.RegistrySupplier;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistrySellerStatus;
import com.code.aon.sales.Sales;
import com.code.aon.sales.bridge.ProjectTasManager;
import com.code.aon.sales.bridge.SalesManager;
import com.code.aon.seller.Seller;
import com.code.aon.supplier.Supplier;
import com.code.aon.tas.ProjectTas;
import com.code.aon.tas.TasItem;
import com.code.aon.ui.commercial.util.CommercialEmailUtil;
import com.code.aon.ui.commercial.util.DocumentOnlineSigner;
import com.code.aon.ui.commercial.util.OfferImportManager;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.config.BankAccountHelper;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.HeaderObjectController;
import com.code.aon.ui.finance.SddMandateObject;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.sales.controller.SalesController;
import com.code.aon.ui.sign.controller.ISignatureController;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.tas.controller.ITasConstants;
import com.code.aon.ui.tas.controller.ProjectTasController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.entity.IEntityAlias;

public class OfferController extends HeaderObjectController implements ISignatureController, ICommercialConstants, IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(OfferController.class.getName());
	
	private List<SelectItem> addresses;
	private List<SelectItem> projects;
	private Boolean defaultPayMethod;
	private IPriceStrategy priceStrategy;
	private boolean showOfferCopyWindow;
	private String offerSeries;
	private int offerNumber;
	private Target offerTarget;
	private Date offerDate;
	private boolean showSalesWindow;
	private String salesSeries;
	private int salesNumber;
	private Date salesDate;
	private boolean showInvoiceWindow;
	private String invoiceSeries;
	private int invoiceNumber;
	private Date invoiceDate;
	private boolean showProjectTasWindow;
	private String projectTasSeries;
	private int projectTasNumber;
	private Date projectTasDate;
	private TasItem projectTasItem;
	private String selectedTab;
	private boolean showAuditInfoWindow;
	private Double listTotal;
	private CommercialEmailUtil emailUtil;
	private boolean showEmailOptionWindow;
	private boolean includeEmailOfferAttach;
	private boolean includeEmailOfferReport;
	private boolean includeEmailSddMandateReport;
	private boolean documentOnlineSign;
	private SddMandateObject sddMandate;
	private BankAccountHelper accountHelper;
	private DocumentOnlineSigner documentOnlineSigner;
	
	public OfferController() {
		this.emailUtil = new CommercialEmailUtil();
		this.accountHelper = new BankAccountHelper(this);
	}
	
	public List<SelectItem> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<SelectItem> addresses) {
		this.addresses = addresses;
	}

	public List<SelectItem> getProjects() {
		return projects;
	}

	public void setProjects(List<SelectItem> projects) {
		this.projects = projects;
	}

	public Boolean getDefaultPayMethod() {
		return defaultPayMethod;
	}
	
	public void setDefaultPayMethod(Boolean defaultPayMethod) {
		this.defaultPayMethod = defaultPayMethod;
		if (defaultPayMethod != null && defaultPayMethod) {
			resetOfferPayMethod();
		}
	}
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public boolean isShowOfferCopyWindow() {
		return showOfferCopyWindow;
	}

	public void setShowOfferCopyWindow(boolean value) {
		this.showOfferCopyWindow = value;
	}
	
	public String getOfferSeries() {
		return offerSeries;
	}

	public void setOfferSeries(String offerSeries) {
		this.offerSeries = offerSeries;
	}

	public int getOfferNumber() {
		return offerNumber;
	}

	public void setOfferNumber(int offerNumber) {
		this.offerNumber = offerNumber;
	}

	public Target getOfferTarget() {
		return offerTarget;
	}

	public void setOfferTarget(Target offerTarget) {
		this.offerTarget = offerTarget;
	}

	public Date getOfferDate() {
		return offerDate;
	}

	public void setOfferDate(Date offerDate) {
		this.offerDate = offerDate;
	}

	public boolean isShowSalesWindow() {
		return showSalesWindow;
	}

	public void setShowSalesWindow(boolean value) {
		this.showSalesWindow = value;
	}
	
	public String getSalesSeries() {
		return salesSeries;
	}

	public void setSalesSeries(String salesSeries) {
		this.salesSeries = salesSeries;
	}

	public int getSalesNumber() {
		return salesNumber;
	}

	public void setSalesNumber(int salesNumber) {
		this.salesNumber = salesNumber;
	}

	public Date getSalesDate() {
		return salesDate;
	}

	public void setSalesDate(Date salesDate) {
		this.salesDate = salesDate;
	}

	public boolean isShowInvoiceWindow() {
		return showInvoiceWindow;
	}

	public void setShowInvoiceWindow(boolean value) {
		this.showInvoiceWindow = value;
	}
	
	public String getInvoiceSeries() {
		return invoiceSeries;
	}

	public void setInvoiceSeries(String invoiceSeries) {
		this.invoiceSeries = invoiceSeries;
	}

	public int getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(int invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public boolean isShowProjectTasWindow() {
		return showProjectTasWindow;
	}

	public void setShowProjectTasWindow(boolean value) {
		this.showProjectTasWindow = value;
	}
	
	public String getProjectTasSeries() {
		return projectTasSeries;
	}

	public void setProjectTasSeries(String projectTasSeries) {
		this.projectTasSeries = projectTasSeries;
	}

	public int getProjectTasNumber() {
		return projectTasNumber;
	}

	public void setProjectTasNumber(int projectTasNumber) {
		this.projectTasNumber = projectTasNumber;
	}

	public Date getProjectTasDate() {
		return projectTasDate;
	}

	public void setProjectTasDate(Date projectTasDate) {
		this.projectTasDate = projectTasDate;
	}

	public TasItem getProjectTasItem() {
		return projectTasItem;
	}

	public void setProjectTasItem(TasItem projectTasItem) {
		this.projectTasItem = projectTasItem;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
	public Double getListTotal() {
		return listTotal;
	}

	public void setListTotal(Double listTotal) {
		this.listTotal = listTotal;
	}

	public boolean isShowEmailOptionWindow() {
		return showEmailOptionWindow;
	}

	public void setShowEmailOptionWindow(boolean showEmailOptionWindow) {
		this.showEmailOptionWindow = showEmailOptionWindow;
	}

	public boolean isIncludeEmailOfferAttach() {
		return includeEmailOfferAttach;
	}

	public void setIncludeEmailOfferAttach(boolean includeEmailOfferAttach) {
		this.includeEmailOfferAttach = includeEmailOfferAttach;
	}

	public boolean isIncludeEmailOfferReport() {
		return includeEmailOfferReport;
	}

	public void setIncludeEmailOfferReport(boolean includeEmailOfferReport) {
		this.includeEmailOfferReport = includeEmailOfferReport;
	}

	public boolean isIncludeEmailSddMandateReport() {
		return includeEmailSddMandateReport;
	}

	public void setIncludeEmailSddMandateReport(boolean includeEmailSddMandateReport) {
		this.includeEmailSddMandateReport = includeEmailSddMandateReport;
	}

	public boolean isDocumentOnlineSign() {
		return documentOnlineSign;
	}

	public void setDocumentOnlineSign(boolean documentOnlineSign) {
		this.documentOnlineSign = documentOnlineSign;
	}

	public SddMandateObject getSddMandate() {
		return sddMandate;
	}

	public void setSddMandate(SddMandateObject sddMandate) {
		this.sddMandate = sddMandate;
	}

	private Offer getOffer() {
		return (Offer) this.getTo();
	}
	
	public boolean isReadOnly() {
		return !isPending() || getOffer().isSigned(); 
	}	

	public boolean isTargetReadOnly() throws ManagerBeanException {
		if (getOffer().getProject() != null && getOffer().getProject().getId() != null) {
			return true;
		}
		return isReadOnly();
	}

	public boolean isPending() {
		return OfferStatus.PENDING == getOffer().getStatus();
	}
	
	public boolean isApproved() {
		return OfferStatus.APPROVED == getOffer().getStatus();		
	}

	public boolean isRefused() {
		return OfferStatus.REFUSED == getOffer().getStatus();		
	}
	
	public boolean isInvoiced() {
		return OfferStatus.INVOICED == getOffer().getStatus();
	}

	public boolean isBlocked() {
		return OfferStatus.BLOCKED == getOffer().getStatus();
	}
	
	public boolean isProject() {
		return getOffer().getProject() != null && getOffer().getProject().getId() != null; 
	}	

	public boolean isLinesPending() throws ManagerBeanException {
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), getOffer().getId());
		criteria.addNotNullExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ITEM_ID));
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_STATUS), OfferDetailStatus.PENDING);
		return offerDetailBean.getCount(criteria) > 0;
	}

	public boolean isLinesSold() throws ManagerBeanException {
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), getOffer().getId());
		criteria.addNotNullExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ITEM_ID));
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_STATUS), OfferDetailStatus.ON_SALE);
		return offerDetailBean.getCount(criteria) > 0;
	}
	
	public ApplicationParameter getDocumentOnlineSignParam() throws ManagerBeanException {
		ApplicationParameter appParam = AppParamUtil.getParameter("DOCUMENT_ONLINE_SIGN");
		return appParam;
	}

	public void targetData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Target target = (Target)event.getNewValue();
			getOffer().setTarget(target);
			getOffer().setScope(target.getScope());
			loadAddresses(target.getId());
			loadProjects(target.getId());
			loadCommercial(target.getId());
			loadDefaultPayMethod(target.getRegistry(), true);
		} else {
			setAddresses(null);
			setProjects(null);
		}
	}
	
	public void onWorkPlaceChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			WorkPlace workPlace = (WorkPlace)event.getNewValue();
			((Offer)this.getTo()).setWorkPlace(workPlace);
			((Offer)this.getTo()).setScope(workPlace.getScope());
		}
	}
	
	public void loadAddresses(Integer id) throws ManagerBeanException {
		List<SelectItem> addresses = new LinkedList<SelectItem>();
		if (id != null) {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
			Iterator<?> iter = rAddressBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryAddress address = (RegistryAddress)iter.next();
				String addressLabel = address.getFullAddress();
				addressLabel = (addressLabel.length()>30?addressLabel.substring(0,27)+"...":addressLabel) + " - " + address.getCity();
				addressLabel = addressLabel.length()>48?addressLabel.substring(0,45)+"...":addressLabel;
				SelectItem item = new SelectItem(address, addressLabel);
				addresses.add(item);
			}
		}
		this.addresses = addresses;
	}

	public int getAddressCount() {
		if (addresses != null){
			return addresses.size();
		}
		return 0;
	}

	public void loadProjects(Integer id) throws ManagerBeanException {
		this.projects = new LinkedList<SelectItem>();
		if (id != null) {
			IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID), id);
			criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_ACTIVE), Boolean.TRUE);
			criteria.addOrder(projectBean.getFieldName(IEntityAlias.PROJECT_NAME));
			Iterator<?> iterator = projectBean.getList(criteria).iterator();
			while(iterator.hasNext()) {
				Project project = (Project)iterator.next();
				SelectItem item = new SelectItem(project, project.getName());
				projects.add(item);
			}
		}
	}

	public int getProjectCount() {
		if (projects != null) {
			return projects.size();
		}
		return 0;
	}
	
	public void removeOfferProject(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
		to.setProject(null);
		getManagerBean().restoreNullSubPOJOs(to);
		getManagerBean().update(to);
		getManagerBean().initializePOJO(to);
	}

	public void loadCommercial(Integer id) throws ManagerBeanException {
		if (id != null) {
			Offer offer = getOffer();
			IManagerBean registrySellerBean = BeanManager.getManagerBean(RegistrySeller.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_REGISTRY_ID), id);
			criteria.addEqualExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_STATUS), RegistrySellerStatus.ACTIVE);
			criteria.addLessThanOrEqualExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_START_DATE), offer.getIssueDate());
			Expression endDateExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_END_DATE), offer.getIssueDate());
			Expression endNullExpr = ExpressionUtilities.getNullExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_END_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(endDateExpr, endNullExpr));
			criteria.addOrder(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_START_DATE));
			Iterator<ITransferObject> iter = registrySellerBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				offer.setSeller(((RegistrySeller)iter.next()).getSeller());
			} else {
				offer.setSeller(new Seller());
				offer.getSeller().setRegistry(new Registry());
			}
		}
	}

	public void loadDefaultPayMethod(Registry registry, boolean forceReset) throws ManagerBeanException {
		if (registry != null && registry.getId() != null) {
			if (forceReset) {
				resetOfferPayMethod();
				setDefaultPayMethod(registry.getPayMethod() != null);
			} else {
				Offer offer = getOffer();
				setDefaultPayMethod(offer.getPayMethod() == null || offer.getPayMethod().getId() == null);
			}
		}
	}

	public void resetOfferPayMethod() {
		Offer to = getOffer();
		to.setPayMethod(new PayMethod());
		to.setNumberOfPayments(1);
		to.setDaysToFirstPayment(0);
		to.setDaysBetweenPayments(0);
		to.setPaymentDays("");
		to.setBankAccount(new BankAccount());
		to.setBankAlias(null);
		to.setBic(null);
	}

	public BankAccountHelper getAccountHelper() {
		return accountHelper;
	}
	
	public DocumentOnlineSigner getDocumentOnlineSigner() {
		return documentOnlineSigner;
	}
	public void setDocumentOnlineSigner(DocumentOnlineSigner documentOnlineSigner) {
		this.documentOnlineSigner = documentOnlineSigner;
	}

	public void supplierData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Offer offer = getOffer();
			offer.setSupplier((Supplier)event.getNewValue());

			IManagerBean bean = BeanManager.getManagerBean(RegistrySupplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_SUPPLIER_REGISTRY_ID), offer.getTarget().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_SUPPLIER_SUPPLIER_ID), offer.getSupplier().getId());
			Iterator<?> iterator = bean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				RegistrySupplier registrySupplier = (RegistrySupplier)iterator.next();
				offer.setPayMethod(registrySupplier.getPayMethod());
				offer.setNumberOfPayments(registrySupplier.getNumberOfPayments());
				offer.setDaysToFirstPayment(registrySupplier.getDaysToFirstPayment());
				offer.setDaysBetweenPayments(registrySupplier.getDaysBetweenPayments());
				offer.setPaymentDays(registrySupplier.getPaymentDays());
				offer.setBankAccount(registrySupplier.getBankAccount());
				offer.setBankAlias(registrySupplier.getBankAlias());
				offer.setBic(registrySupplier.getBic());
				setDefaultPayMethod(false);
			}
		}
	}
	
	public boolean isDealership() {
		return getOffer().isDealership();
	}

	public void sellerData(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Seller seller = (Seller)event.getNewValue();
			getOffer().setSeller(seller);
		}
	}

	public double getTaxableBase(){
		return getPriceStrategy().getTaxableBase(getOffer());
	}

	public double getTotalPrice(){
		return getPriceStrategy().getTotalPrice(getOffer(), getOffer().getTarget());
	}

	public double getOfferTotalPrice() throws ManagerBeanException {
		Offer offer = (Offer)this.getModel().getRowData();
		return getOfferTotalPrice(offer);
	}
	
	public double getOfferTotalPrice(Offer offer) throws ManagerBeanException {
		return getPriceStrategy().getTotalPrice(offer, offer.getTarget());
	}

	public void obtainListTotals(ActionEvent event) {
		double listTotal = 0.0; 
		try {	
			for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
				listTotal += getOfferTotalPrice((Offer)ito);
			}
		} catch (ManagerBeanException e) {
			String message = "Imposible obtener el Total";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}		
		setListTotal(listTotal);
	}

	public void onVersion(ActionEvent event) throws ManagerBeanException {
		Offer to = ((Offer)this.getTo());
		this.getManagerBean().restoreNullSubPOJOs(to);
		if (isPending()) {
			to.setStatus(OfferStatus.REFUSED);
			this.getManagerBean().update(to);
		}

		OfferImportManager manager = new OfferImportManager();
		Offer offer = manager.createOfferVersion(to);
		
		this.onEditSearch(event);
		this.getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.OFFER_ID), offer.getId());
		this.onSearch(event);
		this.getModel().setRowIndex(0);
		this.onSelect(event);
	}

	public void onPending(ActionEvent event) {
		getOffer().setStatus(OfferStatus.PENDING);
		accept(event);
	}
	
	public void onApprove(ActionEvent event) {
		getOffer().setStatus(OfferStatus.APPROVED);
		accept(event);
	}

	public void onRefuse(ActionEvent event) {
		getOffer().setStatus(OfferStatus.REFUSED);
		accept(event);
	}

	public void onBlock(ActionEvent event) {
		getOffer().setStatus(OfferStatus.BLOCKED);
		accept(event);
	}
	
	public void onUnblock(ActionEvent event) throws ManagerBeanException {
		getOffer().setStatus(isLinesSold() ? OfferStatus.APPROVED : OfferStatus.PENDING);
		accept(event);
	}

	public void onOfferCopyShow(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
		initSeries(false);
		setOfferSeries(SeriesUtil.ensureOfferSeries(to.getSeries()));
		setOfferNumber(0);
		setOfferTarget(to.getTarget());
		setOfferDate(new Date());
	}

	public void onOfferNumberEditable(ActionEvent event) throws ManagerBeanException {
		updateOfferNumber(getOfferSeries());		
	}
	
	public void onOfferCopySeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		if ( isNumberEditable() ) {
			updateOfferNumber((String)event.getNewValue());	
		}
	}

	public void offerCopyTargetData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Target target = (Target)event.getNewValue();
			setOfferTarget(target);
		}
	}
	
	private void updateOfferNumber(String seriesId) {
		setOfferNumber(obtainMaxNumber(seriesId));
	}	
	
	public void onCopy(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
        if (StringUtils.isBlank(getOfferSeries())) {
        	setOfferSeries(null);
        }		
        if (getOfferNumber() == 0) {
        	updateOfferNumber(getOfferSeries());
		}		
		this.getManagerBean().restoreNullSubPOJOs(to);
		OfferImportManager manager = new OfferImportManager();
		Offer offer = manager.copyOffer(to, getOfferSeries(), getOfferNumber(), getOfferTarget(), getOfferDate(), !to.getTarget().equals(getOfferTarget()));

		this.onEditSearch(event);
		this.getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.OFFER_ID), offer.getId());
		this.onSearch(event);
		this.getModel().setRowIndex(0);
		this.onSelect(event);
	}
	
	private SalesController getSalesController() {
		return (SalesController) AonUtil.getRegisteredBean(ISalesConstants.SALES_CONTROLLER_NAME);
	}	

	public void onSalesShow(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
		getSalesController().initSeries(false);
		setSalesSeries(SeriesUtil.ensureSalesSeries(to.getSeries()));
		setSalesNumber(0);
		setSalesDate(new Date());
	}

	public void onSalesSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		if ( getSalesController().isNumberEditable() ) {			
			updateSalesNumber((String)event.getNewValue());	
		}
	}

	private void updateSalesNumber(String seriesId) {
		setSalesNumber(getSalesController().obtainMaxNumber(seriesId));
	}

	public void onSalesNumberEditable(ActionEvent event) throws ManagerBeanException {
		updateSalesNumber(getSalesSeries());		
	}
	
	public void onSales(ActionEvent event) {
		Offer to = getOffer();
		try {
			SalesManager salesManager = new SalesManager();
			Sales sales = salesManager.salesOrder(to, getSalesSeries(), getSalesNumber(), getSalesDate());

			BasicController salesController = (BasicController)AonUtil.getRegisteredBean(SALES_CONTROLLER_NAME);
			salesController.onLoad(event, sales.getId(), NAVIGATION_OFFER_FORM, OFFER_CONTROLLER_NAME + ".refresh");		
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private SaleInvoiceController getSaleInvoiceController() {
		return (SaleInvoiceController) AonUtil.getRegisteredBean(IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME);
	}		
	
	public void onInvoiceSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		if ( getSaleInvoiceController().isNumberEditable() ) {			
			updateInvoiceNumber((String)event.getNewValue());	
		}
	}
	
	public void onInvoiceNumberEditable(ActionEvent event) throws ManagerBeanException {
		updateInvoiceNumber(getInvoiceSeries());		
	}		

	private void updateInvoiceNumber(String seriesId) {
		setInvoiceNumber(getSaleInvoiceController().obtainMaxNumber(seriesId));
	}

	public void onInvoiceShow(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
		validate(to);
		SaleInvoiceController controller = getSaleInvoiceController();
		controller.onCancel(event);
		controller.initSeries(false);
		setInvoiceSeries(SeriesUtil.ensureInvoiceSeries(to.getSeries()));
		setInvoiceNumber(0);
		setInvoiceDate(new Date());
	}

	private void validate(Offer offer) {
		try {
			IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), offer.getId());
			criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ITEM_PRODUCT_SERIALIZABLE), Boolean.TRUE);
			Expression serialNullExp = ExpressionUtilities.getNullExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ITEM_SERIAL_NUMBER));
			Expression serialEmptyExp = ExpressionUtilities.getEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ITEM_SERIAL_NUMBER), "");
			criteria.addExpression(ExpressionUtilities.getOrExpression(serialNullExp, serialEmptyExp));
			if (offerDetailBean.getCount(criteria) > 0) {
				String message = AonUtil.addErrorMessageFromBundle(ITEM_SERIALIZABLE_REQUIRED_ERROR);
				throw new AbortProcessingException(message);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage());
		}
	}

	public void onInvoice(ActionEvent event) {
		Offer to = getOffer();
		try {
			OfferInvoicingManager invoicingManager = new OfferInvoicingManager();
			invoicingManager.invoice(to, getInvoiceSeries(), getInvoiceNumber(), getInvoiceDate());
			onLoadInvoice(event);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public Invoice getInvoice() throws ManagerBeanException {
		Offer offer = getOffer();
		if (offer != null && offer.getId() != null) {
			IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), offer.getId());
			criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_STATUS), OfferDetailStatus.ON_INVOICE);
			Iterator<?> iterator = offerDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				OfferDetail offerDetail = (OfferDetail)iterator.next();

				IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.OFFER);
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE_ID), offerDetail.getId());
				Iterator<?> iter = invoiceDetailBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
					return invoiceDetail.getInvoice();
				}
			}
		}
    	return null;
	}

	public String getInvoiceCode() throws ManagerBeanException {
		Invoice invoice = getInvoice();
		return (invoice != null) ? invoice.getReferenceCode() : null;
	}

	private ProjectTasController getProjectTasController() {
		return (ProjectTasController) AonUtil.getRegisteredBean(ITasConstants.PROJECT_TAS_CONTROLLER_NAME);
	}		
	
	public void onProjectTasShow(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
		getProjectTasController().initSeries(false);
		setProjectTasSeries(SeriesUtil.ensureProjectTasSeries(to.getSeries()));
		setProjectTasNumber(0);
		setProjectTasDate(new Date());
		setProjectTasItem((TasItem)BeanManager.getManagerBean(TasItem.class).createNewTo());
	}

	public void onProjectTasSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		if ( getProjectTasController().isNumberEditable() ) {			
			updateProjectTasNumber((String)event.getNewValue());	
		}
	}

	private void updateProjectTasNumber(String seriesId) {
		setProjectTasNumber(getProjectTasController().obtainMaxNumber(seriesId));
	}
	
	public void onProjectTasNumberEditable(ActionEvent event) throws ManagerBeanException {
		updateProjectTasNumber(getProjectTasSeries());		
	}	

	public void onProjectTas(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
        if ( StringUtils.isBlank(getProjectTasSeries()) ) {
        	setProjectTasSeries(null);
        }		
        if(getProjectTasNumber() == 0) {
        	updateProjectTasNumber(getProjectTasSeries());
		}										
		ProjectTasManager tasManager = new ProjectTasManager();
		ProjectTas projectTas = tasManager.projectTas(to, getProjectTasSeries(), getProjectTasNumber(), getProjectTasDate(), getProjectTasItem());

		IController projectTasController = FormUtil.getController(PROJECT_TAS_CONTROLLER_NAME);
		projectTasController.onEditSearch(event);
		projectTasController.getCriteria().addEqualExpression(projectTasController.getFieldName(IEntityAlias.PROJECT_TAS_ID), projectTas.getId());
		projectTasController.onSearch(event);
		projectTasController.getModel().setRowIndex(0);
		projectTasController.onSelect(event);
	}
	
	public void onShowEmailOptionWindow(ActionEvent event) {
		setIncludeEmailOfferReport(true);
		setIncludeEmailOfferAttach(true);
		setIncludeEmailSddMandateReport(false);
		setDocumentOnlineSign(false);
		Offer offer = (Offer) this.getTo();
		setSddMandate(new SddMandateObject());
		getSddMandate().setSignDate(offer.getDate());
		getSddMandate().setRegistry(offer.getTarget().getRegistry());
		getSddMandate().setReference(String.valueOf(offer.getTarget().getRegistry().getId()));
		setDocumentOnlineSigner(new DocumentOnlineSigner());
		getDocumentOnlineSigner().init();
	}

	public void onSendEmail(ActionEvent event) {
		if(isDocumentOnlineSign()) {
			try {
				getDocumentOnlineSigner().sendData(getOffer(), isIncludeEmailOfferReport(), isIncludeEmailOfferAttach(), isIncludeEmailSddMandateReport());
			} catch (Exception e) {
				String msg = "Se ha producido un error al solicitar la firma digital online.";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg + "["+e+"]");
			}
		} else {
			MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
			controller.onPrepareEmailWindow(event);
			if ( controller.isShowNewMessageWindow() ) {	
				try {
					controller.onNewMessage(event);
					emailUtil.initMessageController(controller, getOffer(), getSddMandate(), 
							isIncludeEmailOfferReport(), isIncludeEmailOfferAttach(), isIncludeEmailSddMandateReport() );
				} catch (Throwable th) {
					LOGGER.error(th.getMessage(), th);
					AonUtil.addErrorMessage(th.getMessage());
					throw new AbortProcessingException(th.getMessage(), th);
				}				
			}
		}
	}

	@Override
	public IManagerBean getAttachmentBean() {
		try {
			return BeanManager.getManagerBean(OfferAttachment.class);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String getAttachmentMimeTypeAlias() {
		return IEntityAlias.OFFER_ATTACHMENT_MIME_TYPE;
	}

	@Override
	public String getAttachmentParentAlias() {
		return IEntityAlias.OFFER_ATTACHMENT_OFFER_ID;
	}

	@Override
	public boolean isSigned(ITransferObject to) {
		return ((Offer) to).isSigned();
	}

	@Override
	public IAttachment newAttachment(ITransferObject parent, MimeType type) {
		OfferAttachment attachment = new OfferAttachment();
		attachment.setOffer((Offer) parent);
		return attachment;
	}

	@Override
	public void setSigned(ITransferObject to, boolean value) {
		((Offer) to).setSigned(value);
	}	
	
	@Override
	public IAttachment generateReportAttachment(ITransferObject to) {
		return getSignerController().getReport(to);
	}

	@Override
	public IAttachment getUnsignedAttachment(ITransferObject to, MimeType mimeType) {
		if ( mimeType == MimeType.MIME_PDF ) {
			return generateReportAttachment(to);	
		}
		return null;
	}

	@Override
	public String getDescription(ITransferObject parent) {
		Offer offer = (Offer) parent;
		return "offer_" + offer.getSeries() + "-" + offer.getNumber();
	}

	public SignerController getSignerController() {
		return (SignerController) AonUtil.getRegisteredBean(ICommercialConstants.OFFER_SIGNER_CONTROLLER_NAME);
	}		
	
	public String getTargetPhone() throws ManagerBeanException{			
		IManagerBean mediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE),MediaType.FIXED_PHONE);
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_COMMERCIAL),true);
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID),((Offer)this.getTo()).getTarget().getId());
		Iterator<?> iter = mediaBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			RegistryMedia rmedia = (RegistryMedia)iter.next();
			return rmedia.getValue();
		}
		return null;
	}
	
	public String getTargetCellularPhone() throws ManagerBeanException{			
		IManagerBean mediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE),MediaType.CELLULAR);
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_COMMERCIAL),true);
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID),((Offer)this.getTo()).getTarget().getId());
		Iterator<?> iter = mediaBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			RegistryMedia rmedia = (RegistryMedia)iter.next();
			return rmedia.getValue();
		}
		return null;
	}
	
	public String getTargetFax() throws ManagerBeanException{		
		IManagerBean mediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE),MediaType.FAX);
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_COMMERCIAL),true);
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID),((Offer)this.getTo()).getTarget().getId());
		Iterator<?> iter = mediaBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			RegistryMedia rmedia = (RegistryMedia)iter.next();
			return rmedia.getValue();
		}
		return null;
	}
	
	public String getTargetEmail() throws ManagerBeanException{			
		IManagerBean mediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE),MediaType.EMAIL);
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_COMMERCIAL),true);
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID),((Offer)this.getTo()).getTarget().getId());
		Iterator<?> iter = mediaBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			RegistryMedia rmedia = (RegistryMedia)iter.next();
			return rmedia.getValue();
		}
		return null;
	}
	
	public RegistryPayMethod getTargetPayMethod() throws ManagerBeanException{			
		IManagerBean mediaBean = BeanManager.getManagerBean(RegistryPayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_PAY_METHOD_REGISTRY_ID),((Offer)this.getTo()).getTarget().getId());
		Iterator<?> iter = mediaBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			RegistryPayMethod rpay = (RegistryPayMethod)iter.next();
			return rpay;
		}
		return null;
	}

	public void onLoadInvoice(ActionEvent event) throws ManagerBeanException {
		Invoice invoice = getInvoice();
		if (invoice != null) {
			BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
			invoiceController.onLoad(event, invoice.getId(), NAVIGATION_OFFER_FORM, OFFER_CONTROLLER_NAME + ".refresh");
		}
	}

	public String getReportTemplate() throws ManagerBeanException {
		Offer offer = (Offer) this.getTo();
		if(offer!=null && offer.getType()!=OfferType.PROFORMA) {
			String value = AppParamUtil.getValue(AppParam.APP_OFFER_TEMPLATE_PARAM);
			if ( value != null ) {
				return value;
			}
		}
		return OFFER_CONTROLLER_NAME;
	}
	
	@Override
	public List<SelectItem> getSeriesCodes() throws ManagerBeanException {
		ConfigCollectionsController ccc = (ConfigCollectionsController) AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		return ccc.getOfferSeriesIds();
	}

}