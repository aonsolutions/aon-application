package com.code.aon.ui.commercial.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.bridge.invoicing.OfferInvoicingManager;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.seller.Seller;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;

/**
 * Controller used in the offer maintenance.
 */
public class OfferController extends BasicController {

	private static final String SALE_INVOICE_CONTROLLER_NAME = "saleInvoice";

	private List<SelectItem> addresses;

	private Boolean defaultPayMethod;

	private IPriceStrategy priceStrategy;
	
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

	public boolean isPending(){
		Offer offer = (Offer)this.getTo();
		if (offer.getStatus() != null) {
			return offer.getStatus().equals(OfferStatus.PENDING);
		}
		return false;
	}

	public boolean isApproved(){
		Offer offer = (Offer)this.getTo();
		if (offer.getStatus() != null) {
			return offer.getStatus().equals(OfferStatus.APPROVED);
		}
		return false;
	}

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		Series series = SeriesNumberUtil.obtainSeries((String)event.getNewValue());
		if (this.getTo() != null) {
			((Offer)this.getTo()).setNumber(SeriesNumberUtil.obtainNumber((String)event.getNewValue(), StringUtils.capitalize(this.getBeanName())));
			((Offer)this.getTo()).setSecurityLevel((series!=null)?series.getSecurityLevel():null);
		}
	}

	public void targetData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Target target = (Target)event.getNewValue();
			((Offer)this.getTo()).setTarget(target);
			loadAddresses(target.getId());
			loadDefaultPayMethod(target.getId());
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
	public void loadDefaultPayMethod(Integer id) throws ManagerBeanException {
		if (id != null) {
			if (((Offer)this.getTo()).getPayMethod() != null && ((Offer)this.getTo()).getPayMethod().getId() != null) {
				setDefaultPayMethod(false);
			} else {
				IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(rPayMethodBean.getFieldName(IRegistryAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), id);
				Iterator iter = rPayMethodBean.getList(criteria).iterator();
				setDefaultPayMethod(iter.hasNext());
			}
		}
	}

	public void resetOfferPayMethod() {
		Offer to = (Offer)this.getTo();
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
			((Offer)this.getTo()).setSeller(seller);
		}
	}

	public double getTaxableBase(){
		return getPriceStrategy().getTaxableBase((ICalculableContainer)getTo());
	}

	public double getTotalPrice(){
		return getPriceStrategy().getTotalPrice((ICalculableContainer)getTo(), ((Offer)getTo()).getTarget());
	}

	public double getOfferTotalPrice() throws ManagerBeanException {
		Offer offer = (Offer)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(offer, offer.getTarget());
	}

	public void onInvoice(ActionEvent event) throws ManagerBeanException {
		Offer to = (Offer)this.getTo();
		OfferInvoicingManager invoicingManager = new OfferInvoicingManager();
		Invoice invoice = invoicingManager.invoice(to, to.getSeries(), new Date());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		IController saleInvoiceController = FormUtil.getController(SALE_INVOICE_CONTROLLER_NAME);
		saleInvoiceController.clearCriteria();
		saleInvoiceController.getCriteria().addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_ID), invoice.getId());
		saleInvoiceController.onSearch(null);
	}


	/***************************************************************************************
	 *	BOTON DE TRASPASO A PEDIDO CREADO PARA DEMO DEL 03/12/2008. BORRAR POSTERIORMENTE 
	 ***************************************************************************************/
/*
	public void createSales(ActionEvent event) throws ManagerBeanException {
		Offer offer = (Offer)getTo();
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);

		Sales sales = new Sales();
		sales.setSeries(offer.getSeries()); //Pedir!!
		sales.setNumber(SeriesNumberUtil.obtainNumber(offer.getSeries(), "Sales"));
		sales.setCustomer(obtainCustomer(offer));
		sales.setShippingAddress(offer.getAddress());
		sales.setSeller(offer.getSeller());
		sales.setIssueDate(offer.getIssueDate());
		sales.setPayMethod(offer.getPayMethod());
		sales.setDocumentType(DocumentType.NORMAL);
		sales.setSecurityLevel(offer.getSecurityLevel());
		sales.setStatus(SalesStatus.PENDING);
		sales.setWorkPlace(offer.getWorkPlace());
		sales = (Sales)salesBean.insert(sales);

		Iterator iterator = offer.getDetailList().iterator();
		while (iterator.hasNext()) {
			OfferDetail offerDetail = (OfferDetail)iterator.next();

			SalesDetail salesDetail = new SalesDetail();
			salesDetail.setSales(sales);
			salesDetail.setItem(offerDetail.getItem());
			salesDetail.setDescription(offerDetail.getDescription());
			salesDetail.setQuantity(offerDetail.getQuantity());
			salesDetail.setPrice(offerDetail.getPrice());
			salesDetail.setDiscountExpression(offerDetail.getDiscountExpression());
			salesDetail.setSalesDetailStatus(SalesDetailStatus.PENDING);
			salesDetailBean.insert(salesDetail);
		}

		offer.setStatus(OfferStatus.PROCESSED);
		offerBean.update(offer);

		IController salesController = FormUtil.getController("sales");
		salesController.clearCriteria();
		salesController.getCriteria().addEqualExpression(salesBean.getFieldName(ISalesAlias.SALES_ID), sales.getId());
		salesController.onSearch(null);
	}

*/
	/***************************************************************************************
	 *	BOTON DE TRASPASO A PEDIDO CREADO PARA DEMO DEL 03/12/2008. BORRAR POSTERIORMENTE 
	 ***************************************************************************************/

}
