package com.code.aon.ui.warehouse.controller;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.DeliveryDetail;

/**
 * Controller for Delivery Detail.
 * 
 * @author Consulting & Development.
 */
public class DeliveryDetailController extends LinesController {
	
	/**
	 * Price calculator
	 */
	private IPriceStrategy priceStrategy;
	
	/**
	 * Sales linked to the current SalesDetail
	 */
	private Sales sales;
	
	/**
	 * Date of the sales invoice
	 */
	private Date salesInvoiceDate;
	
	/**
	 * Is an importing offer
	 */
	private boolean importingOffer;
	
	/**
	 * The name of the Delivery controller
	 */
	private static final String DELIVERY_CONTROLLER_NAME = "delivery";
	
	/**
	 * Returns the sales invoice date
	 * 
	 * @return sales invoice date
	 */
	public Date getSalesInvoiceDate() {
		return salesInvoiceDate;
	}

	/**
	 * Assigns the sales invoice date
	 * 
	 * @param salesInvoiceDate the sales invoice date
	 */
	public void setSalesInvoiceDate(Date salesInvoiceDate) {
		this.salesInvoiceDate = salesInvoiceDate;
	}
	
	/**
	 * Returns if is an importing offer
	 * 
	 * @return true if is importing
	 */
	public boolean isImportingOffer() {
		return importingOffer;
	}

	/**
	 * Assigns if is an importing offer
	 * 
	 * @param importingOffer true if is importing
	 */
	public void setImportingOffer(boolean importingOffer) {
		this.importingOffer = importingOffer;
	}

	/**
	 * Accepts the controller and edit new line 
	 * 
	 * @param event the event of the menu
	 */
    public void onAcceptEditNew(ActionEvent event) {
		super.onAccept(event);
		super.onReset(event);
	}
	
	/**
	 * Resets the controller 
	 * 
	 * @param event the event of the menu
	 */
	public void onStart(ActionEvent event) {
		super.onReset(null);
		setSales(null);
	}
	

	/**
	 * Assigns the item with this ident to the DeliveryDetail
	 * 
	 * @param event contains the ident of the Item
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
				((DeliveryDetail)this.getTo()).setItem(item);
			}
		}
	}
	
	/**
	 * Returns the base price instead of this price strategy of this transfer object
	 * 
	 * @return the base price
	 */
	public double getToBasePrice(){
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}
	
	/**
	 * Returns the base price instead of this price strategy of the seleceted object in the model
	 * 
	 * @return the base price
	 * @throws ManagerBeanException
	 */
	public double getModelToBasePrice() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}
	
	/**
	 * Returns the taxable base instead of this price strategy of the delivery controller transfer object
	 * 
	 * @return taxable base
	 */
	public double getTaxableBase(){
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		return getPriceStrategy().getTaxableBase((ICalculableContainer)deliveryController.getTo());
	}
	
	/**
	 * Returns price strategy
	 * 
	 * @return the price strategy
	 */
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}
	
	/**
	 * Returns linked sales
	 * 
	 * @return sales linked
	 */
	public Sales getSales() {
		return sales;
	}

	/**
	 * Assigns the sales
	 * 
	 * @param sales to link
	 */
	public void setSales(Sales sales) {
		this.sales = sales;
	}

	/**
	 * Assigns sales invoice date
	 * 
	 * @param event event that contains the date
	 */
	public void loadSalesInvoiceDate(ValueChangeEvent event) {
		this.salesInvoiceDate = (Date)event.getNewValue();
	}
}