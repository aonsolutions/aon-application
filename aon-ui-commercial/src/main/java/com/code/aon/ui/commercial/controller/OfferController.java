package com.code.aon.ui.commercial.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;

import java.io.IOException;
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
import org.xml.sax.SAXException;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferAttachment;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.TargetSeller;
import com.code.aon.commercial.TargetSupplier;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.TargetSellerStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.config.util.SeriesUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.OfferInvoicingManager;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
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
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.report.ReportException;
import com.code.aon.sales.Sales;
import com.code.aon.sales.bridge.ProjectTasManager;
import com.code.aon.sales.bridge.SalesManager;
import com.code.aon.seller.Seller;
import com.code.aon.supplier.Supplier;
import com.code.aon.tas.ProjectTas;
import com.code.aon.tas.TasItem;
import com.code.aon.ui.commercial.util.CommercialEmailUtil;
import com.code.aon.ui.commercial.util.OfferImportManager;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.sign.controller.ISignatureController;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.SecurityInfo;
import com.esferalia.aon.entity.IEntityAlias;

public class OfferController extends BasicController implements ISignatureController, ICommercialConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(OfferController.class.getName());
	
	private String selectedTab;
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
	private CommercialEmailUtil emailUtil;
	
	public OfferController() {
		this.emailUtil = new CommercialEmailUtil();
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
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

	private Offer getOffer() {
		return (Offer) this.getTo();
	}
	
	public Double getOffersTotalAmount() throws ManagerBeanException {
		double offersTotalAmount = 0.0; 
		for(ITransferObject to: this.getWrappedList()){
			Offer o = (Offer) to;
			offersTotalAmount += getOfferTotalPrice(o);
		}
		return offersTotalAmount;
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
		return (offerDetailBean.getCount(criteria) > 0);
	}

	public boolean isLinesSold() throws ManagerBeanException {
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), getOffer().getId());
		criteria.addNotNullExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ITEM_ID));
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_STATUS), OfferDetailStatus.ON_SALE);
		return (offerDetailBean.getCount(criteria) > 0);
	}

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		String series = (String) event.getNewValue();
		int number = obtainMaxNumber(series);
		SecurityLevel securityLevel = SeriesUtil.getSeriesSecurityLevel( series );
		Offer offer = getOffer();
		if (offer != null) {
			offer.setNumber(number);
			offer.setSecurityLevel(securityLevel);
		}
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	return SeriesNumberUtil.obtainNumber(seriesId, StringUtils.capitalize(this.getBeanName()));
	}

	public void targetData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Target target = (Target)event.getNewValue();
			getOffer().setTarget(target);
			getOffer().setTariff(target.getTariff());
			getOffer().setScope(target.getScope());
			loadAddresses(target.getId());
			loadProjects(target.getId());
			loadCommercial(target.getId());
			loadDefaultPayMethod(target.getId(), false);
		} else {
			setAddresses(null);
			setProjects(null);
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
				addressLabel = ((addressLabel.length()>30)?addressLabel.substring(0,27)+"...":addressLabel) + " - " + address.getCity();
				addressLabel = ((addressLabel.length()>48)?addressLabel.substring(0,45)+"...":addressLabel);
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
			criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_ACTIVE), new Boolean(true));
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
			IManagerBean targetSellerBean = BeanManager.getManagerBean(TargetSeller.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(targetSellerBean.getFieldName(IEntityAlias.TARGET_SELLER_TARGET_ID), id);
			criteria.addEqualExpression(targetSellerBean.getFieldName(IEntityAlias.TARGET_SELLER_STATUS), TargetSellerStatus.ACTIVE);
			criteria.addLessThanOrEqualExpression(targetSellerBean.getFieldName(IEntityAlias.TARGET_SELLER_START_DATE), offer.getIssueDate());
			Expression endDateExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(targetSellerBean.getFieldName(IEntityAlias.TARGET_SELLER_END_DATE), offer.getIssueDate());
			Expression endNullExpr = ExpressionUtilities.getNullExpression(targetSellerBean.getFieldName(IEntityAlias.TARGET_SELLER_END_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(endDateExpr, endNullExpr));
			criteria.addOrder(targetSellerBean.getFieldName(IEntityAlias.TARGET_SELLER_START_DATE));
			Iterator<ITransferObject> iter = targetSellerBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				offer.setSeller(((TargetSeller)iter.next()).getSeller());
			} else {
				offer.setSeller(new Seller());
				offer.getSeller().setRegistry(new Registry());
			}
		}
	}

	public void loadDefaultPayMethod(Integer id, boolean forceDefault) throws ManagerBeanException {
		if (id != null) {
			Offer offer = getOffer();
			if (offer.getPayMethod() != null && offer.getPayMethod().getId() != null) {
				setDefaultPayMethod(false);
			} else {
				if (forceDefault) {
					setDefaultPayMethod(true);
				} else {
					IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(rPayMethodBean.getFieldName(IEntityAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), id);
					Iterator<ITransferObject> iter = rPayMethodBean.getList(criteria).iterator();
					setDefaultPayMethod(iter.hasNext());
				}
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
		to.setBank(new Bank());
		to.setBankAccount(new BankAccount());
	}

	public void supplierData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Offer offer = getOffer();
			offer.setSupplier((Supplier)event.getNewValue());

			IManagerBean bean = BeanManager.getManagerBean(TargetSupplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TARGET_SUPPLIER_TARGET_ID), offer.getTarget().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TARGET_SUPPLIER_SUPPLIER_ID), offer.getSupplier().getId());
			Iterator<?> iterator = bean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				TargetSupplier targetSupplier = (TargetSupplier)iterator.next();
				offer.setTariff(targetSupplier.getTariff());
				offer.setPayMethod(targetSupplier.getPayMethod());
				offer.setNumberOfPayments(targetSupplier.getNumberOfPayments());
				offer.setDaysToFirstPayment(targetSupplier.getDaysToFirstPayment());
				offer.setDaysBetweenPayments(targetSupplier.getDaysBetweenPayments());
				offer.setPaymentDays(targetSupplier.getPaymentDays());
				offer.setBank(targetSupplier.getBank());
				offer.setBankAccount(targetSupplier.getBankAccount());
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
		setOfferSeries(to.getSeries());
		setOfferNumber(obtainMaxNumber(to.getSeries()));
		setOfferTarget(to.getTarget());
		setOfferDate(new Date());
	}

	public void onOfferCopySeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		setOfferNumber(obtainMaxNumber((String)event.getNewValue()));
	}

	public void offerCopyTargetData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Target target = (Target)event.getNewValue();
			setOfferTarget(target);
		}
	}
	
	public void onCopy(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
		this.getManagerBean().restoreNullSubPOJOs(to);
		OfferImportManager manager = new OfferImportManager();
		Offer offer = manager.copyOffer(to, getOfferSeries(), getOfferNumber(), getOfferTarget(), getOfferDate());

		this.onEditSearch(event);
		this.getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.OFFER_ID), offer.getId());
		this.onSearch(event);
		this.getModel().setRowIndex(0);
		this.onSelect(event);
	}

	public void onSalesShow(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
		setSalesSeries(SeriesUtil.ensureSalesSeries(to.getSeries()));
		setSalesNumber(obtainMaxSalesNumber(getSalesSeries()));
		setSalesDate(new Date());
	}

	public void onSalesSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		setSalesNumber(obtainMaxSalesNumber((String)event.getNewValue()));
	}

	private int obtainMaxSalesNumber(String seriesId) {
		return SeriesNumberUtil.obtainNumber(seriesId, "Sales");
	}

	public void onSales(ActionEvent event) {
		try {
			Offer to = getOffer();
			SalesManager salesManager = new SalesManager();
			Sales sales = salesManager.salesOrder(to, getSalesSeries(), getSalesNumber(), getSalesDate());
			IController salesController = FormUtil.getController(SALES_CONTROLLER_NAME);
			salesController.onEditSearch(event);
			salesController.getCriteria().addEqualExpression(salesController.getFieldName(IEntityAlias.SALES_ID), sales.getId());
			salesController.onSearch(event);
			salesController.getModel().setRowIndex(0);
			salesController.onSelect(event);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo grabar el pedido. (" + e.getMessage()+ ")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void onInvoiceShow(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
		setInvoiceSeries(SeriesUtil.ensureInvoiceSeries(to.getSeries()));
		setInvoiceNumber(obtainMaxInvoiceNumber(getInvoiceSeries()));
		setInvoiceDate(new Date());
	}

	public void onInvoiceSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		setInvoiceNumber(obtainMaxInvoiceNumber((String)event.getNewValue()));
	}

	private int obtainMaxInvoiceNumber(String seriesId) {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	public void onInvoice(ActionEvent event)  {
		try {
			Offer to = getOffer();
			OfferInvoicingManager invoicingManager = new OfferInvoicingManager();
			Invoice invoice = invoicingManager.invoice(to, getInvoiceSeries(), getInvoiceNumber(), getInvoiceDate());

			IController invoiceController = FormUtil.getController(SALE_INVOICE_CONTROLLER_NAME);
			invoiceController.onEditSearch(event);
			invoiceController.getCriteria().addEqualExpression(invoiceController.getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
			invoiceController.onSearch(event);
			invoiceController.getModel().setRowIndex(0);
			invoiceController.onSelect(event);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo generar la factura. (" + e.getMessage()+ ")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
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

	public void onProjectTasShow(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
		setProjectTasSeries(SeriesUtil.ensureProjectTasSeries(to.getSeries()));
		setProjectTasNumber(obtainMaxProjectTasNumber(getProjectTasSeries()));
		setProjectTasDate(new Date());
		setProjectTasItem((TasItem)BeanManager.getManagerBean(TasItem.class).createNewTo());
	}

	public void onProjectTasSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		setProjectTasNumber(obtainMaxProjectTasNumber((String)event.getNewValue()));
	}

	private int obtainMaxProjectTasNumber(String seriesId) {
		return SeriesNumberUtil.obtainNumber(seriesId, "ProjectTas");
	}

	public void onProjectTas(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
		ProjectTasManager tasManager = new ProjectTasManager();
		ProjectTas projectTas = tasManager.projectTas(to, getProjectTasSeries(), getProjectTasNumber(), getProjectTasDate(), getProjectTasItem());

		IController projectTasController = FormUtil.getController(PROJECT_TAS_CONTROLLER_NAME);
		projectTasController.onEditSearch(event);
		projectTasController.getCriteria().addEqualExpression(projectTasController.getFieldName(IEntityAlias.PROJECT_TAS_ID), projectTas.getId());
		projectTasController.onSearch(event);
		projectTasController.getModel().setRowIndex(0);
		projectTasController.onSelect(event);
	}

	public void onSendOfferByEmail(ActionEvent event) throws ManagerBeanException, ReportException, IOException, SAXException {
		sendOfferByEmail(null);
	}

	public void sendOfferByEmail(SecurityInfo securyInfo) throws ManagerBeanException, ReportException, IOException, SAXException {
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		if (mailConfig.getMailAccountCount() > 0) {
			MessageController messageController = (MessageController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
			messageController.initNewMessage();
			emailUtil.initMessageController(messageController, getOffer());
			messageController.setShowNewMessageWindow(true);
			messageController.setSecurityInfo(securyInfo);
		} else {
			AonUtil.addErrorMessageFromBundle(IWebMailConstants.BUNDLE_NAME, IWebMailConstants.NOT_MAIL_ACCOUNTS);
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
	public IAttachment newAttachment(ITransferObject parent) {
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
	public IAttachment getUnsignedAttachment(ITransferObject to) {
		return generateReportAttachment(to);
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

}
