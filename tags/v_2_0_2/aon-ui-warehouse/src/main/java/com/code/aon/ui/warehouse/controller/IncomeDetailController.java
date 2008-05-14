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
import com.code.aon.purchase.Purchase;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.IncomeDetail;

/**
 * Controller for Income Detail.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class IncomeDetailController extends LinesController {
	
	/**
	 * Income controller's name
	 */
	private static final String INCOME_CONTROLLER_NAME = "income";
	
	/**
	 * This class assigned price strategy
	 */
	private IPriceStrategy priceStrategy;
	
	/**
	 * Related purchase
	 */
	private Purchase purchase;
	
	/**
	 * The invoice date
	 */
	private Date invoiceDate;
	
	
	/**
	 * Accepts the controller and edit new line 
	 * 
	 * @param event the event of the menu
	 */
	@SuppressWarnings("unused")
    public void onAcceptEditNew(ActionEvent event) {
		super.onAccept(event);
		super.onReset(event);
	}
	
	/**
	 * Resets the controller
	 * 
	 * @param event the event of the menu
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) throws ManagerBeanException{
		super.onReset(null);
		setPurchase(null);
	}
	
	/**
	 * Cancels the controller
	 * 
	 * @param event the event of the menu
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unused")
	public void onCancel(MenuEvent event) throws ManagerBeanException{
		super.onCancel(null);
	}

	/**
	 * Returns the invoice date
	 * 
	 * @return returns the date of the invoice
	 */
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	/**
	 * Assigns the invoice date
	 * 
	 * @param invoiceDate the date to assign
	 */
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	
	/**
	 * Searchs the item instead of this events value and assigns to the transfer object, IncomeDetail
	 * 
	 * @param event the event that has the value
	 * @throws ManagerBeanException
	 */
	public void itemData(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_ID),event.getNewValue());
			Iterator iter = itemBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Item item = (Item)iter.next();
				((IncomeDetail)this.getTo()).setItem(item);
			}
		}
	}
	
	/**
	 * Returns the base price of the transfer object
	 * 
	 * @return the base price
	 */
	public double getToBasePrice(){
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}
	
	/**
	 * Returns the base price of this row
	 * 
	 * @return the base price
	 * @throws ManagerBeanException
	 */
	public double getModelToBasePrice() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}
	
	/**
	 * Returns the taxable base of the transfer object
	 * 
	 * @return the taxable base
	 */
	public double getTaxableBase(){
		IncomeController incomeController = (IncomeController)AonUtil.getController(INCOME_CONTROLLER_NAME);
		return getPriceStrategy().getTaxableBase((ICalculableContainer)incomeController.getTo());
	}
	
	/**
	 * Returns the price strategy linked to this class
	 * 
	 * @return price strategy
	 */
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	/**
	 * Returns the purchase
	 * 
	 * @return the purchase
	 */
	public Purchase getPurchase() {
		return purchase;
	}

	/**
	 * Assigns the purchase
	 * 
	 * @param purchase the purchase
	 */
	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}
	
	/**
	 * Assigns the invoice date 
	 * 
	 * @param event the event that contains the date
	 */
	public void loadInvoiceDate(ValueChangeEvent event) {
		this.invoiceDate  = (Date)event.getNewValue();
	}
}