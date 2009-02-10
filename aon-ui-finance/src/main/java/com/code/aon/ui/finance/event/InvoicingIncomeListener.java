package com.code.aon.ui.finance.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.finance.controller.InvoicingController;
import com.code.aon.ui.finance.controller.InvoicingDetailController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IncomeController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

/**
 * Invoice income controller listener
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class InvoicingIncomeListener extends ControllerAdapter {
	
	/**
	 * The class logger
	 */
	private static final Logger LOGGER = Logger.getLogger(InvoicingIncomeListener.class.getName());
	
	/**
	 * Income controllers name
	 */
	private static final String INCOME_CONTROLLER_NAME = "income";
	
	/**
	 * invoicingdetail controllers name
	 */
	private static final String INVOICING_DETAIL_CONTROLLER_NAME = "invoicingDetail";

	/**
	 * finance controllers name
	 */
	private static final String FINANCE_CONTROLLER_NAME = "purchaseFinance";
	
	/**
	 * Reset the income and purchase of invoicingdetail controller
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		InvoicingDetailController invoicingDetailController = (InvoicingDetailController)AonUtil.getController(INVOICING_DETAIL_CONTROLLER_NAME);
		LinesController financeController = (LinesController)AonUtil.getController(FINANCE_CONTROLLER_NAME);
		financeController.onCancel(null);
		invoicingDetailController.setIncome(null);
		invoicingDetailController.setPurchase(null);
	}
	
	/**
	 * Assigns default type, status and address to invoice
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice) event.getController().getTo();
		invoice.setType(InvoiceType.PURCHASE);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setRegistryAddress(obtainRegistryAddress(invoice.getRegistry().getId()));
	}
	
	/**
	 * Recovers the first address of this registry ident
	 * 
	 * @param id related registry ident
	 * @return the first address
	 */
	@SuppressWarnings("unchecked")
	private RegistryAddress obtainRegistryAddress(Integer id) {
		try {
			IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID),id);
			Iterator iter = registryAddressBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				return (RegistryAddress) iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error obtaining RegistryAddress for registry with id: " + id, e);
		}
		return null;
	}
	
	/**
	 * Searchs related incomes
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		try {
			IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_SUPPLIER_ID), invoice.getRegistry().getId());
			criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_INCOME_STATUS), IncomeStatus.PENDING);
			IncomeController incomeController = (IncomeController)AonUtil.getController(INCOME_CONTROLLER_NAME);
			incomeController.clearCheckList();
			incomeController.setCriteria(criteria);
			incomeController.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving income model for supplier= " + invoice.getRegistry().getId(), e);
		}
	}
	
	/**
	 * Generates closed incomes, assigns them to income controllers model
	 * and updates invoicingdetail controllers references 
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		try {
			IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_SUPPLIER_ID), invoice.getRegistry().getId());
			Expression expression = ExpressionUtilities.getExpression(Integer.toString(IncomeStatus.PENDING.ordinal()), incomeBean.getFieldName(IWarehouseAlias.INCOME_INCOME_STATUS));
			Iterator iterator = obtainClosedIncomes(invoice.getId()).iterator();
			while (iterator.hasNext()) {
				Income income = (Income)iterator.next();
				Expression idExpression = ExpressionUtilities.getExpression(income.getId().toString(), incomeBean.getFieldName(IWarehouseAlias.INCOME_ID));
				expression = ExpressionUtilities.getOrExpression(expression, idExpression);
			}
			criteria.addExpression(expression);
			criteria.addOrder(incomeBean.getFieldName(IWarehouseAlias.INCOME_INCOME_STATUS), false);
			IncomeController incomeController = (IncomeController)AonUtil.getController(INCOME_CONTROLLER_NAME);
			incomeController.clearCheckList();
			incomeController.setCriteria(criteria);
			incomeController.onSearch(null);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving income model for supplier= " + invoice.getRegistry().getId(), e);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving income model for supplier= " + invoice.getRegistry().getId(), e);
		}
		updateDetailControllerReferences(invoice);
	}
	
	/**
	 * updates invoicingdetail controllers references if needed
	 * 
	 * @param invoice related invoice
	 */
	private void updateDetailControllerReferences(Invoice invoice) {
		InvoicingDetailController invoicingDetailController = (InvoicingDetailController)AonUtil.getController(INVOICING_DETAIL_CONTROLLER_NAME);
		IncomeDetail incomeDetail = obtainIncomeDetail(invoice);
		if(incomeDetail != null){
			invoicingDetailController.setIncome(incomeDetail.getIncome());
			invoicingDetailController.setPurchase(incomeDetail.getPurchaseDetail().getPurchase());
		}
	}
	
	/**
	 * Returns an incomedetail with this invoice 
	 * 
	 * @param invoice related invoice
	 * @return incomedetail
	 */
	@SuppressWarnings("unchecked")
	private IncomeDetail obtainIncomeDetail(Invoice invoice) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			Iterator iter = invoiceDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				return obtainIncomeDetail(invoiceDetail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining asociated income for invoice with id= " + invoice.getId(), e);
		}
		return null;
	}

	/**
	 * Retruns the incomedetail of this invoicedetail
	 * 
	 * @param invoiceDetail related invoicedetail
	 * @return invoiceDetail's incomedetail
	 */
	@SuppressWarnings("unchecked")
	private IncomeDetail obtainIncomeDetail(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ID), invoiceDetail.getSourceId());
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (IncomeDetail)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining asociated incomeDetail for invoiceDetail with id= " + invoiceDetail.getId(), e);
		}
		return null;
	}

	/**
	 * Before remove an invoice
	 * Gets invoicingDetailController model list of InvoiceDetails and for each invoice detail
	 * if it is direct purchase removes related incomedetails.
	 * Obtains relateds closed incomes and set status to pending.
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanRemoved(ControllerEvent event){
		try {
			InvoicingDetailController invoicingDetailController = (InvoicingDetailController) AonUtil.getController(INVOICING_DETAIL_CONTROLLER_NAME);
			Iterator iter = ((List)invoicingDetailController.getModel().getWrappedData()).iterator();
			while(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				if(invoiceDetail.getSource().equals(InvoiceSource.DIRECT_PURCHASE)){
					removeIncomeDetails(invoiceDetail);
				}
			}
			iter = obtainClosedIncomes(((Invoice)event.getController().getTo()).getId()).iterator();
			InvoicingController invoicingController = (InvoicingController)event.getController();
			IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
			while(iter.hasNext()){
				Income income = (Income)iter.next();
				invoicingController.removeInvoiceDetails(income.getId());
				income.setIncomeStatus(IncomeStatus.PENDING);
				incomeBean.update(income);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error removing invoiceDetails", e);
		}
	}
	
	/**
	 * Recovers income controlle and resets it
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		IncomeController incomeController = (IncomeController)AonUtil.getController(INCOME_CONTROLLER_NAME);
		try {
			incomeController.onReset(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	/**
	 * Removes incomeDetails related to this invoicedetail
	 * and removes the income and purchasedetails if needed
	 * 
	 * @param invoiceDetail related invoicedetail
	 */
	@SuppressWarnings("unchecked")
	private void removeIncomeDetails(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ID), invoiceDetail.getSourceId());
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			int number = incomeDetailBean.getCount(criteria);
			if(iter.hasNext()){
				IncomeDetail incomeDetail = (IncomeDetail)iter.next();
				incomeDetailBean.remove(incomeDetail);
				if(number == 1){
					IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
					incomeBean.remove(incomeDetail.getIncome());
				}
				removePurchaseDetails(incomeDetail.getPurchaseDetail(), number == 1);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error removing incomedetails for invoiceDetail with id= " + invoiceDetail.getId(), e);
		}
		
	}

	/**
	 * Removes purchasedetail and header if needed
	 * 
	 * @param purchaseDetail purchasedetail to delete
	 * @param hasToRemoveHeader true if has to removeheader
	 */
	private void removePurchaseDetails(PurchaseDetail purchaseDetail, boolean hasToRemoveHeader) {
		try {
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			purchaseDetailBean.remove(purchaseDetail);
			if(hasToRemoveHeader){
				IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
				purchaseBean.remove(purchaseDetail.getPurchase());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error removing purchasedetails ", e);
		}
	}

	/**
	 * Recovers closed incomes related to this invoice ident 
	 * 
	 * @param invoiceId related invoice ident
	 * @return a list of closed incomes
	 */
	@SuppressWarnings("unchecked")
	private List<ITransferObject> obtainClosedIncomes(Integer invoiceId) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoiceId);
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.INCOME);
			Iterator iter = invoiceDetailBean.getList(criteria).iterator();
			List<ITransferObject> incomes = new LinkedList<ITransferObject>();
			while(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				criteria = new Criteria();
				criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ID), invoiceDetail.getSourceId());
				Iterator iterator = incomeDetailBean.getList(criteria).iterator();
				if(iterator.hasNext()){
					IncomeDetail incomeDetail = (IncomeDetail)iterator.next();
					if(!incomes.contains(incomeDetail.getIncome())){
						incomes.add(incomeDetail.getIncome());
					}
				}
			}
			return incomes;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving incomes closed by invoice id= " + invoiceId, e);
		}
		return null;
	}

}
