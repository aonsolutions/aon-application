package com.code.aon.ui.purchase.controller;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

/**
 * Controller used in the purchaseDetail maintenance.
 */
public class PurchaseDetailController extends LinesController {
	
	/**
	 * Gets the total of the purchaseDetail contained in the current row of the model
	 * 
	 * @return the total
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public double getTotal() throws ManagerBeanException{
		if(this.getModel().isRowAvailable()){
			PurchaseDetail pDetail = (PurchaseDetail)this.getModel().getRowData();
			return round(pDetail.getPrice() * pDetail.getQuantity(), 2);
		}
		return 0.0;
	}
	
	/**
	 * Gets the amount of the current purchaseDetail.
	 * 
	 * @return the amount
	 */
	public double getAmount() {
		if(this.getTo() != null){
			return (round(((PurchaseDetail)this.getTo()).getPrice() * ((PurchaseDetail)this.getTo()).getQuantity() , 2));
		}
		return 0.0;
	}
	
	/**
	 * Gets the total of all the purchaseDetails loaded in the model.
	 * 
	 * @return the details total
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public double getDetailsTotal() throws ManagerBeanException{
		double total = 0;
		if(this.getModel().getRowCount() >0){
			Iterator iter = ((ArrayList)this.getModel().getWrappedData()).iterator();
			while(iter.hasNext()){
				PurchaseDetail purchaseDetail = (PurchaseDetail)iter.next();
				total += purchaseDetail.getPrice() * purchaseDetail.getQuantity(); 
			}
		}
		return round(total,2);
	}
	
	/**
	 * Retrieves the whole <code>Item</code> object when the lookup field changes
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void itemData(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_ID),event.getNewValue());
			Iterator iter = itemBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Item item = (Item)iter.next();
				((PurchaseDetail)this.getTo()).setItem(item);
			}
		}
	}

	/**
	 * Rounds a value using the parameter <code>precision</code>
	 * 
	 * @param value the value
	 * @param precision the precision to use rounding
	 * 
	 * @return the double
	 */
	private double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }
}
