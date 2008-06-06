package com.code.aon.ui.warehouse.event;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.temp.SeriesNumberUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Scope;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IncomeController;
import com.code.aon.ui.warehouse.controller.IncomeDetailController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;

/**
 * An IncomeDetail listener
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class IncomeDetailControllerListener extends ControllerAdapter {
	
	/**
	 * Income controller name 
	 */
	private static final String INCOME_CONTROLLER_NAME = "income";
	
	/**
	 * The class logger
	 */
	private static final Logger LOGGER = Logger.getLogger(IncomeDetailControllerListener.class.getName());

	/**
	 * Recover or creates if needed the purchase related to this income 
	 * and insert in it purchasedetail related to this incomedetail.
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IncomeDetailController incomeDetailController = (IncomeDetailController)event.getController();
		IncomeController incomeController = (IncomeController)AonUtil.getController(INCOME_CONTROLLER_NAME);
		IncomeDetail incomeDetail = (IncomeDetail)incomeDetailController.getTo();
		Warehouse warehouse = new Warehouse();
		warehouse.setId(incomeController.getWarehouseId());
		incomeDetail.setWarehouse(warehouse);
		Purchase purchase = null;
		try {
			if(incomeDetailController.getModel().getRowCount() == 0){
				purchase = insertPurchase((Income)incomeController.getTo());
				incomeDetailController.setPurchase(purchase);
			}else{
				purchase = incomeDetailController.getPurchase();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting purchase for income with id: " + ((Income)incomeDetailController.getTo()).getId());
		}
		PurchaseDetail purchaseDetail = insertPurchaseDetail(purchase, incomeDetail);
		incomeDetail.setPurchaseDetail(purchaseDetail);
	}

	/**
	 * Recover the purchase related to this income 
	 * and updates the purchasedetail related to this incomedetail.
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		IncomeDetail incomeDetail = (IncomeDetail)event.getController().getTo();
		IncomeController incomeController = (IncomeController)AonUtil.getController(INCOME_CONTROLLER_NAME);
		Warehouse warehouse = new Warehouse();
		warehouse.setId(incomeController.getWarehouseId());
		incomeDetail.setWarehouse(warehouse);
		PurchaseDetail purchaseDetail = incomeDetail.getPurchaseDetail();
		purchaseDetail.setItem(incomeDetail.getItem());
        purchaseDetail.setDescription(incomeDetail.getDescription());
		purchaseDetail.setPrice(incomeDetail.getPrice());
		purchaseDetail.setQuantity(incomeDetail.getQuantity());
		IManagerBean purchaseDetailBean;
		try {
			purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			purchaseDetail = (PurchaseDetail)purchaseDetailBean.update(purchaseDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error updating purchaseDetail for incomeDetail with id: " + incomeDetail.getId());
		}
	}

	/**
	 * Removes the purchasedetail related to this incomedetail,
	 * and deletes the purchase header if needed 
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		IncomeDetail incomeDetail = (IncomeDetail)event.getController().getTo();
		try {
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			purchaseDetailBean.remove(incomeDetail.getPurchaseDetail());
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(purchaseDetailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_PURCHASE_ID),incomeDetail.getPurchaseDetail().getPurchase().getId());
			if(purchaseDetailBean.getCount(criteria) == 0){
				IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
				purchaseBean.remove(incomeDetail.getPurchaseDetail().getPurchase());
				IncomeDetailController incomeDetailController = (IncomeDetailController)event.getController();
				incomeDetailController.setPurchase(null);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error removing purchaseDetail for incomeDetail with id: " + incomeDetail.getId());
		}
	}
	
	/**
	 * Inserts a purchase with this income
	 * 
 	 * @param income the related income
	 * @return the purchase inserted
	 */
	private Purchase insertPurchase(Income income) {
		Purchase purchase = new Purchase();
		purchase.setSupplier(income.getSupplier());
		purchase.setStatus(PurchaseStatus.CLOSED);
		purchase.setSeries(income.getSeries());
		purchase.setNumber(SeriesNumberUtil.obtainNumber(income.getSeries(), "Purchase"));
		purchase.setPayMethod(null);
		purchase.setIssueDate(income.getIssueTime());
		purchase.setWorkPlace(obtainWorkPlace());
		IManagerBean purchaseBean;
		try {
			purchaseBean = BeanManager.getManagerBean(Purchase.class);
			purchase = (Purchase)purchaseBean.insert(purchase);
			return purchase;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting purchase for income with id: " + income.getId());
		}
		return null;
	}
	
	/**
	 * Returns workplace.
	 * 
	 * @return workplace
	 */
	private WorkPlace obtainWorkPlace() {
		try {
			IManagerBean wpBean = BeanManager.getManagerBean(WorkPlace.class);
			List<ITransferObject> wpLst = wpBean.getList(null);
			if (wpLst.size() > 0) {
				WorkPlace wp = (WorkPlace)wpLst.get(0);
				return wp;
			}
		}
		catch (ManagerBeanException mbe) {
			mbe.printStackTrace();
		}
		return null;
	}

	/**
	 * Inserts a purchasedetail with an incomedetail and a purchase as header
	 * 
	 * @param purchase the parent purchase
	 * @param incomeDetail related incomedetail
	 * @return the created purchasedetail
	 */
	private PurchaseDetail insertPurchaseDetail(Purchase purchase, IncomeDetail incomeDetail) {
		PurchaseDetail purchaseDetail = new PurchaseDetail();
		purchaseDetail.setPurchase(purchase);
		purchaseDetail.setItem(incomeDetail.getItem());
        purchaseDetail.setDescription(incomeDetail.getDescription());
		purchaseDetail.setPrice(incomeDetail.getPrice());
		purchaseDetail.setQuantity(incomeDetail.getQuantity());
		IManagerBean purchaseDetailBean;
		try {
			purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			purchaseDetail = (PurchaseDetail)purchaseDetailBean.insert(purchaseDetail);
			return purchaseDetail;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting purchaseDetail for incomeDetail with id: " + incomeDetail.getId());
		}
		return null;
	}
}
