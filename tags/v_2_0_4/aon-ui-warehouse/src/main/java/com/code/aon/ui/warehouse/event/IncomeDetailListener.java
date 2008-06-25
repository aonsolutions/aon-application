package com.code.aon.ui.warehouse.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.IncomeDetailController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * An incomedetail listener 
 * 
 * @author Consulting & Development. 
 * @since 1.0
 *
 */
public class IncomeDetailListener extends ControllerAdapter {

	/**
	 * the class logger
	 */
	private final Logger LOGGER = Logger.getLogger(IncomeDetailListener.class.getName());
	
	/**
	 * The incomedetail controller name
	 */
	private static final String INCOME_DETAIL_CONTROLLER_NAME = "incomeDetail";
	
	/**
	 * After selecting an incomedetail, recovers the related purchase 
	 * and assigns it to the incomedetail controller
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Income income = (Income)event.getController().getTo();
		Criteria criteria = new Criteria();
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID),income.getId());
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			Purchase purchase = null;
			if(iter.hasNext()){
				purchase = ((IncomeDetail)iter.next()).getPurchaseDetail().getPurchase();
			}
			IncomeDetailController incomeDetailController = (IncomeDetailController)getController(INCOME_DETAIL_CONTROLLER_NAME);
			incomeDetailController.setPurchase(purchase);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error obtaining purchase for imcome with id: " + income.getId(),e);
		}
	}
	
	/**
	 * After removing a incomedetail,  
	 * Removes related purchase and purchasedetails
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		IncomeDetailController incomeDetailController = (IncomeDetailController)getController(INCOME_DETAIL_CONTROLLER_NAME);
		Purchase purchase = incomeDetailController.getPurchase();
		if(purchase != null){
			try {
				Criteria criteria = new Criteria();
				IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
				criteria.addEqualExpression(purchaseDetailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_PURCHASE_ID),purchase.getId());
				Iterator iter = purchaseDetailBean.getList(criteria).iterator();
				while(iter.hasNext()){
					purchaseDetailBean.remove((PurchaseDetail)iter.next());
				}
				IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
				purchaseBean.remove(purchase);
				incomeDetailController.setPurchase(null);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE,"Error obtaining purchase for imcome with id: " + ((Income)event.getController().getTo()).getId(),e);
			}
		}
	}
	
	/**
	 * Recovers the controller with this name
	 * 
	 * @param name
	 * @return
	 */
	private IController getController(String name) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = ctx.getApplication().createValueBinding("#{" + name + "}");
		return (IController)vb.getValue(ctx);
	}
}
