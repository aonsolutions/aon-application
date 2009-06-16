package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * Sales invoice details controller
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class SalesInvoicingDetailController extends LinesController {

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(SalesInvoicingDetailController.class.getName());
	
	/**
	 * Sales invoice controllers name
	 */
	private static final String SALES_INVOICING_CONTROLLER_NAME = "salesInvoicing";
	
	/**
	 * the price strategy 
	 */
	private IPriceStrategy priceStrategy;
	
	/**
	 * The sales
	 */
	private Sales sales;
	
	/**
	 * The delivery
	 */
	private Delivery delivery;
	
	/**
	 * Returns sales
	 * 
	 * @return sales
	 */
	public Sales getSales() {
		return sales;
	}

	/**
	 * Assigns sales
	 * 
	 * @param sales sales
	 */
	public void setSales(Sales sales) {
		this.sales = sales;
	}
	
	/**
	 * Returns the controllers delivery
	 * 
	 * @return delivery
	 */
	public Delivery getDelivery() {
		return delivery;
	}

	/**
	 * Aasigns controller delivery
	 * 
	 * @param delivery delivery
	 */
	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	/**
	 * Is direct sale?
	 * 
	 * @return true if is direct sale
	 * @throws ManagerBeanException
	 */
	public boolean isDirect() throws ManagerBeanException{
		return ((InvoiceDetail)this.getModel().getRowData()).getSource().equals(InvoiceSource.DIRECT_SALES);
	}
	
	/**
	 * Assigns Item to InvoiceDetail
	 * 
	 * @param event the event that contains item ident
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	public void itemData(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_ID),event.getNewValue());
			Iterator iter = itemBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Item item = (Item)iter.next();
				((InvoiceDetail)this.getTo()).setItem(item);
			}
		}
	}
	
	/**
	 * Price calculator
	 * 
	 * @return price strategy
	 */
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}
	
	/**
	 * Returns selected rows base price 
	 * 
	 * @return base price
	 * @throws ManagerBeanException
	 */
	public double getModelToBasePrice() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}
	
	/**
	 * Returns current transfer objects baseprice 
	 * 
	 * @return base price
	 * @throws ManagerBeanException
	 */
	public double getToBasePrice(){
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}
	
	
	/**
	 * Returns current transfer objects taxable base 
	 * 
	 * @return taxable base
	 * @throws ManagerBeanException
	 */
	public double getTaxableBase(){
		SalesInvoicingController salesInvoicingController = (SalesInvoicingController)AonUtil.getController(SALES_INVOICING_CONTROLLER_NAME);
		return getPriceStrategy().getTaxableBase((ICalculableContainer)salesInvoicingController.getTo());
	}

	public String getDeliveryData() throws ManagerBeanException {
		DeliveryDetail deliveryDetail = obtainDeliveryDetail(((InvoiceDetail)this.getModel().getRowData()).getDeliveryDetail());
		String deliveryData = "";
		if (deliveryDetail != null) {
			deliveryData = (deliveryDetail.getDelivery().getSeries()==null?"":deliveryDetail.getDelivery().getSeries()+"/")+deliveryDetail.getDelivery().getNumber();
		}
		return deliveryData;
	}

	@SuppressWarnings("unchecked")
	private DeliveryDetail obtainDeliveryDetail(Integer deliveryDetailId) {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), deliveryDetailId);
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (DeliveryDetail)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining associated tasDelivery", e);
		}
		return null;
	}

	/**
	 * Calls onCancel action of the controller 
	 * 
	 * @param event menu event
	 */
	@SuppressWarnings("unused")
    public void onCancel(MenuEvent event) {
        super.onCancel(null);
    }

}
