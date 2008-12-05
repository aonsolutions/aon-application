package com.code.aon.ui.commercial.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.faces.component.richfaces.lookup.LookupChangeEvent;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.Seller;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the offer maintenance.
 */
public class OfferController extends BasicController {

	private List<SelectItem> addresses;

	private IPriceStrategy priceStrategy;
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		Series series = SeriesNumberUtil.obtainSeries((String)event.getNewValue());
		if (this.getTo() != null) {
			((Offer)this.getTo()).setNumber(SeriesNumberUtil.obtainNumber((String)event.getNewValue(), StringUtils.capitalize(this.getBeanName())));
			((Offer)this.getTo()).setSecurityLevel((series!=null)?series.getSecurityLevel():null);
			((Offer)this.getTo()).setWorkPlace((series!=null)?series.getWorkPlace():null);
		}
	}

	public boolean isProcessed(){
		Offer offer = (Offer)this.getTo();
		if (offer.getStatus() != null) {
			return offer.getStatus().equals(OfferStatus.PROCESSED);
		}
		return false;
	}

	public void targetData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Target target = (Target)event.getNewValue();
			((Offer)this.getTo()).setTarget(target);
			loadAddresses(target.getId());
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

	public List<SelectItem> getAddresses() {
		return addresses;
	}
	
	public void setAddresses(List<SelectItem> addresses) {
		this.addresses = addresses;
	}
	
	public int getAddressCount() {
		if (addresses != null){
			return addresses.size();
		}
		return 0;
	}
	
	public void sellerData(LookupChangeEvent event) throws ManagerBeanException {
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

	@SuppressWarnings("unused")
	/*
    public void onReport(ActionEvent event) {
        ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("offer");
        manager.setOutputFormat(OutputFormat.PDF);
    }
    */

	/***************************************************************************************
	 *	BOTON DE TRASPASO A PEDIDO CREADO PARA DEMO DEL 03/12/2008. BORRAR POSTERIORMENTE 
	 ***************************************************************************************/

	public void createSales(ActionEvent event) throws ManagerBeanException {
		Offer offer = (Offer)getTo();
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);

		Sales sales = new Sales();
		sales.setSeries(offer.getSeries());
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

	private Customer obtainCustomer(Offer offer) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_ID), offer.getTarget().getRegistry().getId());
		Iterator iterator = customerBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (Customer)iterator.next();
		} else {
			Customer customer = new Customer();
			customer.setRegistry(offer.getTarget().getRegistry());
			customer.setTariff(offer.getTariff());
			customer.setStatus(CustomerStatus.ACTIVE);
			customer.setScope(obtainGenericScope());
			return (Customer)customerBean.insert(customer);
		}
	}

	private Scope obtainGenericScope() {
		Scope scope = new Scope();
		scope.setId(1);
		return scope;
	}

	
	public void sendFarsaMail(ActionEvent event ) {
		Offer offer = (Offer)getTo();
		String email = "cliente@esferalia.com";
		try {
			email = offer.getTarget().getRegistry().getEmail().getValue(); 
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
		}	
		AonUtil.addErrorMessage("No se pudo enviar el correo electrónico a " +
				email + "." +
				" No se puede resolver la dirección del servidor de correo saliente (pop3.esferalia.com)."
				);
	}
	/***************************************************************************************
	 *	BOTON DE TRASPASO A PEDIDO CREADO PARA DEMO DEL 03/12/2008. BORRAR POSTERIORMENTE 
	 ***************************************************************************************/

}
