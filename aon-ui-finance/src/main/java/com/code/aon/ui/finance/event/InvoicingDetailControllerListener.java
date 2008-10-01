package com.code.aon.ui.finance.event;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.temp.SeriesNumberUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.Supplier;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.controller.InvoicingController;
import com.code.aon.ui.finance.controller.InvoicingDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

/**
 * Invoice detail controller listener
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class InvoicingDetailControllerListener extends ControllerAdapter {
	
	/**
	 * The logger of the class
	 */
	private static final Logger LOGGER = Logger.getLogger(InvoicingDetailControllerListener.class.getName());

	/**
	 * Invoicing controllers name
	 */
	private static final String INVOICING_CONTROLLER_NAME = "invoicing";
	
	/**
	 * The price strategy
	 */
	private IPriceStrategy priceStrategy;

	/**
	 * Fills invoicedetail before add it
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoicingDetailController invoicingDetailController = (InvoicingDetailController)event.getController();
		InvoicingController invoicingController = (InvoicingController)AonUtil.getController(INVOICING_CONTROLLER_NAME);
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();  
		invoiceDetail.setInvoice((Invoice)invoicingController.getTo());
		obtainTaxableBase(invoiceDetail);
		((InvoiceDetail)event.getController().getTo()).setSource(InvoiceSource.DIRECT_PURCHASE);
		invoiceDetail.setDeliveryDetail(insertIncomeDetail(invoiceDetail,invoicingDetailController));
	}
	
	/**
	 * Fills invoicedetail and updates incomedetail before update it
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		obtainTaxableBase(invoiceDetail);
		updateIncomeDetail(invoiceDetail);
	}
	
	/**
	 * Returns invocedetails taxable base
	 * 
	 * @param invoiceDetail invoicedetail
	 */
	private void obtainTaxableBase(InvoiceDetail invoiceDetail) {
		invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
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
	 * Removes incomedetail linked and set null to controllers income and purchase
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		InvoicingDetailController invoicingDetailController = (InvoicingDetailController)event.getController();
		int incomeDetailNumber = obtainIncomeDetailNumber(invoicingDetailController.getIncome());
		if(incomeDetailNumber != 0){
			removeIncomeDetail((InvoiceDetail)invoicingDetailController.getTo(),incomeDetailNumber == 1);
			if(incomeDetailNumber == 1){
				invoicingDetailController.setIncome(null);
				invoicingDetailController.setPurchase(null);
			}
		}
	}
	
	/**
	 * Returns the number of incomedetails for this income 
	 * 
	 * @param income the parent income
	 * @return number of incomedetail lines
	 */
	private int obtainIncomeDetailNumber(Income income) {
		if(income != null){
			try {
				IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
				return incomeDetailBean.getCount(criteria);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error retrieving incomeDetail number for income with id= " + income.getId(), e);
			}
		}
		return 0;
	}

	/**
	 * Creates and inserts and incomedetail instead of an invoicedetail
	 * 
	 * @param invoiceDetail related invoicedetail
	 * @param invoicingDetailController related invoicedetail Controller
	 * @return the ident of the incomedetail
	 */
	private Integer insertIncomeDetail(InvoiceDetail invoiceDetail, InvoicingDetailController invoicingDetailController) {
		IncomeDetail incomeDetail = new IncomeDetail();
		incomeDetail.setDiscountExpression(invoiceDetail.getDiscountExpression());
		incomeDetail.setItem(invoiceDetail.getItem());
        incomeDetail.setDescription(invoiceDetail.getDescription());
		incomeDetail.setPrice(invoiceDetail.getPrice());
		incomeDetail.setQuantity(invoiceDetail.getQuantity());
		InvoicingController invoicingController = (InvoicingController)AonUtil.getController(INVOICING_CONTROLLER_NAME);
		incomeDetail.setWarehouse(new Warehouse());
		incomeDetail.getWarehouse().setId(invoicingController.getWarehouseId());
		incomeDetail.setPurchaseDetail(insertPurchaseDetail(invoiceDetail,invoicingDetailController));
		if(invoicingDetailController.getIncome() == null){
			incomeDetail.setIncome(insertIncome(invoiceDetail.getInvoice(),invoicingDetailController));
		}else{
			incomeDetail.setIncome(invoicingDetailController.getIncome());
		}
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			return ((IncomeDetail)incomeDetailBean.insert(incomeDetail)).getId();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting incomeDetail for invoice with id: " + invoiceDetail.getInvoice().getId(),e);
			return null;
		}
	}
	
	/**
	 * Creates and inserts a purchasedetail with this invoicedetail 
	 * 
	 * @param invoiceDetail related invoicedetail
	 * @param invoicingDetailController related invoicingdetail controller
	 * @return the inserted purchasedetail
	 */
	private PurchaseDetail insertPurchaseDetail(InvoiceDetail invoiceDetail, InvoicingDetailController invoicingDetailController) {
		PurchaseDetail purchaseDetail = new PurchaseDetail();
        purchaseDetail.setLine(invoiceDetail.getLine());
		purchaseDetail.setItem(invoiceDetail.getItem());
        purchaseDetail.setDescription(invoiceDetail.getDescription());
		purchaseDetail.setPrice(invoiceDetail.getPrice());
		purchaseDetail.setQuantity(invoiceDetail.getQuantity());
		purchaseDetail.setTaxes(invoiceDetail.getTaxes());
		if(invoicingDetailController.getPurchase() == null){
			purchaseDetail.setPurchase(insertPurchase(invoiceDetail.getInvoice(),invoicingDetailController));
		}else{
			purchaseDetail.setPurchase(invoicingDetailController.getPurchase());
		}
		try {
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			return (PurchaseDetail)purchaseDetailBean.insert(purchaseDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting purchaseDetail for invoice with id: " + invoiceDetail.getInvoice().getId(),e);
			return null;
		}
	}
	
	/**
	 * Creates and inserts a purchase with this invoice 
	 * 
	 * @param invoice related invoice
	 * @param invoicingDetailController related invoicingdetail controller
	 * @return the inserted purchase
	 */
	private Purchase insertPurchase(Invoice invoice, InvoicingDetailController invoicingDetailController) {
		Purchase purchase = new Purchase();
		purchase.setSupplier(new Supplier());
		purchase.getSupplier().setAlias(invoice.getRegistry().getAlias());
		purchase.getSupplier().setDocument(invoice.getRegistry().getDocument());
		purchase.getSupplier().setId(invoice.getRegistry().getId());
		purchase.getSupplier().setName(invoice.getRegistry().getName());
		purchase.getSupplier().setSurname(invoice.getRegistry().getSurname());
		purchase.setIssueDate(invoice.getIssueDate());
		purchase.setSeries(invoice.getSeries());
		purchase.setNumber(SeriesNumberUtil.obtainNumber(invoice.getSeries(), "Purchase"));
		purchase.setPayMethod(null);
		purchase.setSecurityLevel(invoice.getSecurityLevel());
		purchase.setStatus(PurchaseStatus.CLOSED);
		purchase.setWorkPlace(obtainWorkPlace());
		try {
			IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
			purchase = (Purchase)purchaseBean.insert(purchase);
			invoicingDetailController.setPurchase(purchase);
			return purchase;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting purchase for invoice with id: " + invoice.getId(),e);
			return null;
		}
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
	 * Creates and inserts a income with this invoice 
	 * 
	 * @param invoice related invoice
	 * @param invoicingDetailController related invoicingdetail controller
	 * @return the inserted income
	 */
	private Income insertIncome(Invoice invoice, InvoicingDetailController invoicingDetailController) {
		Income income = new Income();
		income.setSupplier(new Supplier());
		income.getSupplier().setAlias(invoice.getRegistry().getAlias());
		income.getSupplier().setDocument(invoice.getRegistry().getDocument());
		income.getSupplier().setId(invoice.getRegistry().getId());
		income.getSupplier().setName(invoice.getRegistry().getName());
		income.getSupplier().setSurname(invoice.getRegistry().getSurname());
		income.setIssueTime(invoice.getIssueDate());
		income.setRegistryAddress(invoice.getRegistryAddress());
		income.setSeries(invoice.getSeries());
		income.setNumber(SeriesNumberUtil.obtainNumber(invoice.getSeries(), "Income"));
		income.setSecurityLevel(invoice.getSecurityLevel());
		income.setIncomeStatus(IncomeStatus.CLOSED);
		try {
			IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
			income = (Income)incomeBean.insert(income);
			invoicingDetailController.setIncome(income);
			return income;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting income for invoice with id: " + invoice.getId(),e);
			return null;
		}
	}
	
	/**
	 * Updates related incomedetail instead of this invoicedetail
	 * 
	 * @param invoiceDetail related invoicedetail
	 */
	private void updateIncomeDetail(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ID),invoiceDetail.getDeliveryDetail());
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				IncomeDetail incomeDetail = (IncomeDetail)iter.next();
				incomeDetail.setDiscountExpression(invoiceDetail.getDiscountExpression());
                incomeDetail.setItem(invoiceDetail.getItem());
                incomeDetail.setDescription(invoiceDetail.getDescription());
				incomeDetail.setPrice(invoiceDetail.getPrice());
				incomeDetail.setQuantity(invoiceDetail.getQuantity());
				incomeDetail.setPurchaseDetail(updatePurchaseDetail(incomeDetail, invoiceDetail));
				incomeDetailBean.update(incomeDetail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error updating incomeDetail for invoiceDetail with id: " + invoiceDetail.getId(),e);
		}
	}
	
	/**
	 * Updates incomedetail's purchasedetail with this invoicedetail
	 * 
	 * @param incomeDetail related incomeDetail 
	 * @param invoiceDetail related invoiceDetail
	 * @return updated purchasedetail
	 */
	private PurchaseDetail updatePurchaseDetail(IncomeDetail incomeDetail, InvoiceDetail invoiceDetail) {
		PurchaseDetail purchaseDetail = incomeDetail.getPurchaseDetail();
		purchaseDetail.setItem(invoiceDetail.getItem());
        purchaseDetail.setDescription(invoiceDetail.getDescription());
		purchaseDetail.setPrice(invoiceDetail.getPrice());
		purchaseDetail.setQuantity(invoiceDetail.getQuantity());
		purchaseDetail.setTaxes(invoiceDetail.getTaxes());
		try {
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			purchaseDetail = (PurchaseDetail)purchaseDetailBean.update(purchaseDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error updating purchaseDetail for incomeDetail with id: " + invoiceDetail.getId(),e);
		}
		return purchaseDetail;
	}
	
	/**
	 * Removes incomedetail and related purchasedetails
	 * if hasToRemoveHeaders is true removes headers too
	 * 
	 * @param invoiceDetail related invoiceDetail 
	 * @param hasToRemoveHeaders true to remove headers
	 */
	private void removeIncomeDetail(InvoiceDetail invoiceDetail, boolean hasToRemoveHeaders) {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ID),invoiceDetail.getDeliveryDetail());
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				IncomeDetail incomeDetail = (IncomeDetail)iter.next();
				incomeDetailBean.remove(incomeDetail);
				if(hasToRemoveHeaders){
					removeIncome(incomeDetail.getIncome());
				}
				removePurchaseDetail(incomeDetail.getPurchaseDetail(),hasToRemoveHeaders);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error removing incomeDetail for invoiceDetail with id: " + invoiceDetail.getId(), e);
		}
	}

	/**
	 * Removes this income
	 * 
	 * @param income related income
	 */
	private void removeIncome(Income income) {
		try {
			IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
			incomeBean.remove(income);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error removing income with id: " + income.getId(), e);
		}
	}

	/**
	 * Removes this purchasedetail and purchase if hasToRemoveHeaders is true 
	 * 
	 * @param purchaseDetail related purchasedetail
	 * @param hasToRemoveHeaders remove headers if true
	 */
	private void removePurchaseDetail(PurchaseDetail purchaseDetail, boolean hasToRemoveHeaders) {
		try {
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			purchaseDetailBean.remove(purchaseDetail);
			if(hasToRemoveHeaders){
				removePurchase(purchaseDetail.getPurchase());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error removing purchaseDetail with id: " + purchaseDetail.getId(), e);
		}
		
	}

	/**
	 * Removes related purchase
	 * 
	 * @param purchase related purchase
	 */
	private void removePurchase(Purchase purchase) {
		try {
			IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
			purchaseBean.remove(purchase);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error removing purchase with id: " + purchase.getId(), e);
		}
	}
}