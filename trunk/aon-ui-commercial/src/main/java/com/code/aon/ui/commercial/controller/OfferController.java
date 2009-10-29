package com.code.aon.ui.commercial.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferAttachment;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Series;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.OfferInvoicingManager;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.report.ReportException;
import com.code.aon.sales.Sales;
import com.code.aon.sales.bridge.SalesManager;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.seller.Seller;
import com.code.aon.ui.commercial.util.EmailUtilController;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.sign.controller.ISignatureController;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.AonFile;
import com.code.aon.webmail.SecurityInfo;

/**
 * Controller used in the offer maintenance.
 */
public class OfferController extends BasicController implements ISignatureController, ICommercialConstants {

	private static final Logger LOGGER = Logger.getLogger(OfferController.class.getName());
	
	private final String SALES_CONTROLLER = "sales";
	private final String SALE_INVOICE_CONTROLLER = "saleInvoice";

	private List<SelectItem> addresses;
	private Boolean defaultPayMethod;
	private IPriceStrategy priceStrategy;
	private boolean showSalesWindow;
	private String salesSeries;
	private int salesNumber;
	private Date salesDate;
	private boolean showInvoiceWindow;
	private String invoiceSeries;
	private int invoiceNumber;
	private Date invoiceDate;
	private String selectedTab;
	
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

	private Offer getOffer() {
		return (Offer) this.getTo();
	}
	
	public boolean isPending() {
		return OfferStatus.PENDING == getOffer().getStatus();
	}
	
	public boolean isReadOnly() {
		return !isPending() || getOffer().isSigned(); 
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
	
	public boolean isLinesPending() throws ManagerBeanException {
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), getOffer().getId());
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_STATUS), OfferDetailStatus.PENDING);
		return (offerDetailBean.getCount(criteria) > 0);
	}

	public boolean isLinesSold() throws ManagerBeanException {
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), getOffer().getId());
		criteria.addNotNullExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_ITEM_ID));
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_STATUS), OfferDetailStatus.ON_SALE);
		return (offerDetailBean.getCount(criteria) > 0);
	}

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		int number = obtainMaxNumber((String)event.getNewValue());
		SecurityLevel securityLevel = obtainSeriesSecurityLevel((String)event.getNewValue());
		Offer offer = getOffer();
		if (offer != null) {
			offer.setNumber(number);
			offer.setSecurityLevel(securityLevel);
		}
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	return SeriesNumberUtil.obtainNumber(seriesId, StringUtils.capitalize(this.getBeanName()));
	}

	@SuppressWarnings("unchecked")
	private SecurityLevel obtainSeriesSecurityLevel(String seriesId) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_ID), seriesId);
		Iterator iter = seriesBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			Series series = (Series)iter.next(); 
			if (series.getSecurityLevel() != null) {
				return series.getSecurityLevel();
			}
		}
		return null;
	}

	public void targetData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Target target = (Target)event.getNewValue();
			getOffer().setTarget(target);
			loadAddresses(target.getId());
			loadDefaultPayMethod(target.getId(), false);
		} else {
			setAddresses(null);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadAddresses(Integer id) throws ManagerBeanException {
		List<SelectItem> addresses = new LinkedList<SelectItem>();
		if (id != null) {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
			Iterator iter = rAddressBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryAddress address = (RegistryAddress)iter.next();
				String addressLabel = address.getAddress() + " " + address.getAddress2() + " " + address.getAddress3();
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

	@SuppressWarnings("unchecked")
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
					criteria.addEqualExpression(rPayMethodBean.getFieldName(IRegistryAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), id);
					Iterator iter = rPayMethodBean.getList(criteria).iterator();
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
		return getPriceStrategy().getTotalPrice(offer, offer.getTarget());
	}

	public void onApprove(ActionEvent event) {
		getOffer().setStatus(OfferStatus.APPROVED);
		accept(event);
	}

	public void onRefuse(ActionEvent event) {
		getOffer().setStatus(OfferStatus.REFUSED);
		accept(event);
	}

	public void onPending(ActionEvent event) {
		getOffer().setStatus(OfferStatus.PENDING);
		accept(event);
	}
	
	public void onUnblock(ActionEvent event) throws ManagerBeanException {
		getOffer().setStatus(isLinesSold() ? OfferStatus.APPROVED : OfferStatus.PENDING);
		accept(event);
	}

	public void onBlock(ActionEvent event) {
		getOffer().setStatus(OfferStatus.BLOCKED);
		accept(event);
	}
	
	public void onSalesShow(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
		setSalesSeries(to.getSeries());
		setSalesNumber(obtainMaxSalesNumber(to.getSeries()));
		setSalesDate(new Date());
	}

	public void onSalesSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		setSalesNumber(obtainMaxSalesNumber((String)event.getNewValue()));
	}

	private int obtainMaxSalesNumber(String seriesId) {
		return SeriesNumberUtil.obtainNumber(seriesId, "Sales");
	}

	public void onSales(ActionEvent event) throws ManagerBeanException {
		setShowSalesWindow(false);

		Offer to = getOffer();
		SalesManager salesManager = new SalesManager();
		Sales sales = salesManager.salesOrder(to, getSalesSeries(), getSalesNumber(), getSalesDate());

		IController salesController = FormUtil.getController(SALES_CONTROLLER);
		salesController.clearCriteria();
		salesController.getCriteria().addEqualExpression(salesController.getFieldName(ISalesAlias.SALES_ID), sales.getId());
		salesController.onSearch(null);
		salesController.getModel().setRowIndex(0);
		salesController.onSelect(null);
	}

	public void onInvoiceShow(ActionEvent event) throws ManagerBeanException {
		Offer to = getOffer();
		setInvoiceSeries(to.getSeries());
		setInvoiceNumber(obtainMaxInvoiceNumber(to.getSeries()));
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

	public void onInvoice(ActionEvent event) throws ManagerBeanException {
		setShowInvoiceWindow(false);

		Offer to = getOffer();
		OfferInvoicingManager invoicingManager = new OfferInvoicingManager();
		Invoice invoice = invoicingManager.invoice(to, getInvoiceSeries(), getInvoiceNumber(), getInvoiceDate());

		IController invoiceController = FormUtil.getController(SALE_INVOICE_CONTROLLER);
		invoiceController.clearCriteria();
		invoiceController.getCriteria().addEqualExpression(invoiceController.getFieldName(IFinanceAlias.INVOICE_ID), invoice.getId());
		invoiceController.onSearch(null);
		invoiceController.getModel().setRowIndex(0);
		invoiceController.onSelect(null);
	}

	public String getInvoiceCode() throws ManagerBeanException {
		Offer offer = getOffer();
		if (offer != null && offer.getId() != null) {
			IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), offer.getId());
			criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_STATUS), OfferDetailStatus.ON_INVOICE);
			Iterator<?> iterator = offerDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				OfferDetail offerDetail = (OfferDetail)iterator.next();

				IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.OFFER);
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE_ID), offerDetail.getId());
				Iterator<?> iter = invoiceDetailBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
					return invoiceDetail.getInvoice().getReferenceCode();
				}
			}
		}
    	return null;
	}

	public void onSendOfferByEmail( ActionEvent event ) throws ManagerBeanException, ReportException, IOException, SAXException {
		sendOfferByEmail( null );
	}

	public void sendOfferByEmail( SecurityInfo securyInfo ) throws ManagerBeanException, ReportException, IOException, SAXException {
		Offer offer = getOffer();
		EmailUtilController emailController = new EmailUtilController();
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(WebMailConstants.BEAN_MESSAGE);
		messageController.initNewMessage();
		Target target = offer.getTarget();
		if ( target != null ) {
			String[] emails = emailController.getEmails(target.getRegistry());
			if (! ArrayUtils.isEmpty(emails) ) {
				messageController.setRecipientsTo( emails[0] );
				if ( emails.length > 1 ) { 
					String recipientsCc = StringUtils.join( emails, ',', 1, emails.length );
					messageController.setRecipientsCc( recipientsCc );
				}
			}			
		}
		messageController.setSubject(emailController.getEmailSubject(offer));
		messageController.setContent(emailController.getEmailBody(offer));
		messageController.addAttachment(emailController.getOfferFile(offer));
		for( AonFile aonFile : emailController.getOfferAttachemnts(offer) ) {
			messageController.addAttachment(aonFile);
		}
		messageController.setShowNewMessageWindow(true);
		messageController.setSecurityInfo( securyInfo );
	}

	@Override
	public IManagerBean getAttachmentBean() {
		try {
			return BeanManager.getManagerBean(OfferAttachment.class);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String getAttachmentMimeTypeAlias() {
		return ICommercialAlias.OFFER_ATTACHMENT_MIME_TYPE;
	}

	@Override
	public String getAttachmentParentAlias() {
		return ICommercialAlias.OFFER_ATTACHMENT_OFFER_ID;
	}

	@Override
	public boolean isSigned(ITransferObject to) {
		return ((Offer) to).isSigned();
	}

	@Override
	public IAttachment newAttachment(ITransferObject parent) {
		OfferAttachment attachment = new OfferAttachment();
		attachment.setOffer( (Offer) parent );
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
}
