package temp.com.code.aon.ui.finance.deprecated.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.purchase.Purchase;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.warehouse.Income;

/**
 * The invoicing detail controller 
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class InvoicingDetailController extends LinesController {

	/**
	 * Invoicing controller name
	 */
	private static final String INVOICING_CONTROLLER_NAME = "invoicing";
	
	/**
	 * Price strategy
	 */
	private IPriceStrategy priceStrategy;
	
	/**
	 * The purchase related
	 */
	private Purchase purchase;
	
	/**
	 * Income related
	 */
	private Income income;
	
	/**
	 * Returns related purchase
	 * 
	 * @return purchase
	 */
	public Purchase getPurchase() {
		return purchase;
	}

	/**
	 * Assigns related purchase
	 * 
	 * @param purchase related purchase
	 */
	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}
	
	/**
	 * Returns related income
	 * 
	 * @return related income
	 */
	public Income getIncome() {
		return income;
	}

	/**
	 * Assigns related income
	 * 
	 * @param income related income
	 */
	public void setIncome(Income income) {
		this.income = income;
	}
	
	/**
	 * Returns if is direct purchase
	 * 
	 * @return true is direct, false is not direct
	 * @throws ManagerBeanException
	 */
	public boolean isDirect() throws ManagerBeanException{
		return ((InvoiceDetail)this.getModel().getRowData()).getSource().equals(InvoiceSource.DIRECT_PURCHASE);
	}
	
	/**
	 * Searchs an item with this events value and assigns it to the InvoiceDetail
	 * 
	 * @param event event that contains the value
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
	 * Returns price strategy
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
	 * Returns the model's row base price 
	 * 
	 * @return base price
	 * @throws ManagerBeanException
	 */
	public double getModelToBasePrice() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}
	
	/**
	 * Returns the controller's transfer object base price 
	 * 
	 * @return base price
	 * @throws ManagerBeanException
	 */
	public double getToBasePrice(){
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}
	
	/**
	 * Returns the controller's transfer object taxable base 
	 * 
	 * @return taxable base
	 */
	public double getTaxableBase(){
		InvoicingController invoicingController = (InvoicingController)FormUtil.getController(INVOICING_CONTROLLER_NAME);
		return getPriceStrategy().getTaxableBase((ICalculableContainer)invoicingController.getTo());
	}

}
