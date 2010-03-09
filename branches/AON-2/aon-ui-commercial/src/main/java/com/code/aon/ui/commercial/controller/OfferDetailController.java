package com.code.aon.ui.commercial.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.commercial.OfferDetail;
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
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the offerDetail maintenance.
 */
public class OfferDetailController extends LinesController {

	/** The OFFER_CONTROLLER_NAME. */
	private static final String OFFER_CONTROLLER_NAME = "offer";
	
	/** The price strategy. */
	private IPriceStrategy priceStrategy;
	
	/**
	 * On reset. Method launched by the menu
	 * 
	 * @param event the menu event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) throws ManagerBeanException{
		super.onReset(null);
	}

	/**
	 * On cancel. Method launched by the menu
	 * 
	 * @param event the menu event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unused")
    public void onCancel(MenuEvent event) throws ManagerBeanException{
        super.onCancel(null);
    }

	/**
	 * Retrieves the whole <code>Item</code> object when the lookup field changes
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void itemData(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_ID), event.getNewValue());
			Iterator iter = itemBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				Item item = (Item) iter.next();
				((OfferDetail) this.getTo()).setItem(item);
			}
		}
	}
	
	/**
	 * Gets the base price of the current offerDetail.
	 * 
	 * @return the base price
	 */
	public double getToBasePrice(){
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}
	
	/**
	 * Gets the base price of the offerDetail contained in the current row of the model
	 * 
	 * @return the base price
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public double getModelToBasePrice() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}
	
	/**
	 * Gets the taxable base of the current offerDetail.
	 * 
	 * @return the taxable base
	 */
	public double getTaxableBase(){
		OfferController offerController = (OfferController)AonUtil.getController(OFFER_CONTROLLER_NAME);
		return getPriceStrategy().getTaxableBase((ICalculableContainer)offerController.getTo());
	}
	
	/**
	 * Gets the price strategy.
	 * 
	 * @return the price strategy
	 */
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}
}