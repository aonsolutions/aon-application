package com.code.aon.ui.sales.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.seller.Seller;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.customer.util.CustomerValidationManager;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * Controller used in the sales maintenance.
 */
public class SalesController extends BasicController {

	private List<SelectItem> addresses;

	private IPriceStrategy priceStrategy;
	
	private CustomerValidationManager cvm;
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	private CustomerValidationManager getCustomerValidationManager() {
		if (cvm == null) {
			cvm = new CustomerValidationManager(); 
		}
		return cvm;
	}
	
	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		Series series = SeriesNumberUtil.obtainSeries((String)event.getNewValue());
		if (this.getTo() != null) {
			((Sales)this.getTo()).setNumber(SeriesNumberUtil.obtainNumber((String)event.getNewValue(), StringUtils.capitalize(this.getBeanName())));
			((Sales)this.getTo()).setSecurityLevel((series!=null)?series.getSecurityLevel():null);
//			((Sales)this.getTo()).setWorkPlace((series!=null)?series.getWorkPlace():null);
		}
	}

	public boolean isPending(){
		Sales sales = (Sales)this.getTo();
		if (sales.getStatus() != null) {
			return sales.getStatus().equals(SalesStatus.PENDING);
		}
		return false;
	}

	public void customerData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			isBlocked(customer); //Sacar la ventanita de los bloqueos. REVISAR
			((Sales)this.getTo()).setCustomer(customer);
			loadAddresses(customer.getId());
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
	
	public void sellerData(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Seller seller = (Seller)event.getNewValue();
			((Sales)this.getTo()).setSeller(seller);
		}
	}

	public double getTaxableBase(){
		return getPriceStrategy().getTaxableBase((ICalculableContainer)getTo());
	}

	public double getTotalPrice(){
		return getPriceStrategy().getTotalPrice((ICalculableContainer)getTo(), ((Sales)getTo()).getCustomer());
	}

	/*
    public void onReport(ActionEvent event) {
        ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("sales");
        manager.setOutputFormat(OutputFormat.PDF);
    }
    */

	private boolean isBlocked(Customer customer) {
		return getCustomerValidationManager().isBlocked(customer);
	}

	/***************************************************************************************
	 *	BOTON DE TRASPASO A PEDIDO CREADO PARA DEMO DEL 03/12/2008. BORRAR POSTERIORMENTE 
	 ***************************************************************************************/

	public void createDelivery(ActionEvent event) throws ManagerBeanException {
		Sales sales = (Sales)getTo();
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);

		Delivery delivery = new Delivery();
		delivery.setSeries(sales.getSeries());
		delivery.setNumber(SeriesNumberUtil.obtainNumber(sales.getSeries(), "Delivery"));
		delivery.setCustomer(sales.getCustomer());
		delivery.setRaddress(sales.getShippingAddress());
		delivery.setIssueTime(sales.getIssueDate());
		delivery.setSecurityLevel(sales.getSecurityLevel());
		delivery.setStatus(DeliveryStatus.PENDING);
		delivery = (Delivery)deliveryBean.insert(delivery);

		Iterator iterator = sales.getDetailList().iterator();
		while (iterator.hasNext()) {
			SalesDetail salesDetail = (SalesDetail)iterator.next();

			DeliveryDetail deliveryDetail = new DeliveryDetail();
			deliveryDetail.setDelivery(delivery);
			deliveryDetail.setItem(salesDetail.getItem());
			deliveryDetail.setDescription(salesDetail.getDescription());
			deliveryDetail.setWarehouse(obtainGenericWarehouse());
			deliveryDetail.setQuantity(salesDetail.getQuantity());
			deliveryDetail.setPrice(salesDetail.getPrice());
			deliveryDetail.setDiscountExpression(salesDetail.getDiscountExpression());
			deliveryDetail.setSalesDetail(salesDetail);
			deliveryDetailBean.insert(deliveryDetail);
		}

//		sales.setPos(null);
		sales.setStatus(SalesStatus.CLOSED);
		salesBean.update(sales);

		IController deliveryController = FormUtil.getController("delivery");
		deliveryController.clearCriteria();
		deliveryController.getCriteria().addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_ID), delivery.getId());
		deliveryController.onSearch(null);
	}

	private Warehouse obtainGenericWarehouse() {
		Warehouse warehouse = new Warehouse();
		warehouse.setId(1);
		return warehouse;
	}

	/***************************************************************************************
	 *	BOTON DE TRASPASO A PEDIDO CREADO PARA DEMO DEL 03/12/2008. BORRAR POSTERIORMENTE 
	 ***************************************************************************************/

	// ***************************************	
	public void sendFarsaMail(ActionEvent event ) {
		Sales sales = (Sales)getTo();
		String email = "cliente@esferalia.com";
		try {
			email = sales.getCustomer().getRegistry().getEmail().getValue(); 
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
		}	
		AonUtil.addErrorMessage("No se pudo enviar el correo electrónico a " +
				email + "." +
				" No se puede resolver la dirección del servidor de correo saliente (pop3.esferalia.com)."
				);
	}
	// ***************************************

}
