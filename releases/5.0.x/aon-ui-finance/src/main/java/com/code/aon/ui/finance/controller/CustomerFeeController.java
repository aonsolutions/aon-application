package com.code.aon.ui.finance.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class CustomerFeeController extends LinesController {

	private static final String CUSTOMER_CONTROLLER_NAME = "customer";
	/** Message file base path. */
   private static final String BASE_NAME = "com.code.aon.ui.registry.i18n.report";
   /** Message key prefix. */
   private static final String MSG_KEY_PREFIX = "aon_no_fee_customer_report";

	private IPriceStrategy priceStrategy;
	private DataModel noFeeCustomersModel;
	private List<Customer> noFeeCustomersList;
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public DataModel getNoFeeCustomersModel() {
		if (noFeeCustomersModel == null) {
			noFeeCustomersModel = new ListDataModel(getNoFeeCustomersList());
		}
		return noFeeCustomersModel;
	}

	public void setNoFeeCustomersModel(DataModel noFeeCustomersModel) {
		this.noFeeCustomersModel = noFeeCustomersModel;
	}

	public List<Customer> getNoFeeCustomersList() {
		return noFeeCustomersList;
	}

	public void setNoFeeCustomersList(List<Customer> noFeeCustomersList) {
		this.noFeeCustomersList = noFeeCustomersList;
	}

	public void onItemChanged(LookupChangeEvent event) {
		CustomerFee fee = (CustomerFee)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			fee.setItem(item);
			String description = item.getProduct().getName();
			if (! StringUtils.isBlank(item.getDetail()) ) {
				description += " " + item.getDetail();
			}
			fee.setDescription( description );

			Date date = fee.getInitialDate();
			CustomerController customerController = (CustomerController)FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
			Customer customer = (Customer)customerController.getTo();
			Tariff tariff = customer.getTariff();
			price = getPriceStrategy().getUnitPrice(fee, date, tariff);
		}
		fee.setPrice(price);
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		CustomerFee fee = (CustomerFee)getTo();
		if (fee.getItem() != null && fee.getItem().getId() != null) {
			double price = 0;
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				fee.setQuantity((Double)event.getNewValue());
	
				Date date = fee.getInitialDate();
				CustomerController customerController = (CustomerController)FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
				Customer customer = (Customer)customerController.getTo();
				Tariff tariff = customer.getTariff();
				price = getPriceStrategy().getUnitPrice(fee, date, tariff);
			}
			fee.setPrice(price);
		}
	}
	
	public String getReportTitle(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX);
	}

	@SuppressWarnings("unchecked")
	public void onNoFeeCustomers(ActionEvent event) {
		Calendar calendar = new GregorianCalendar();
		String select = "select distinct(customer) "
			+ "from Customer as customer "
			+ "where customer.status= 0 AND ( "
			+ "customer.id NOT IN (select customerFee.customer.id from CustomerFee as customerFee) "
			+ "AND "
			+ "customer.id NOT IN (select customerFee.customer.id from CustomerFee as customerFee where finalDate < '"
			+ calendar.getTime() + "')))";	
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		noFeeCustomersList = query.list();
	}

}
