package com.code.aon.ui.finance.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.sales.PointOfSale;
import com.code.aon.sales.Sales;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.ui.finance.controller.SalesInvoicingController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * SalesInvoicingController's listener.
 * 
 * @author Consulting & Development.
 * @since 1.0
 */
public class SalesInvoicingListener extends ControllerAdapter {

	/** The class logger. */
	private static final Logger LOGGER = Logger.getLogger(SalesInvoicingListener.class.getName());

	/**
	 * Completes tax info of the Invoice.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		if(invoice.getRegistryAddress() != null && invoice.getRegistryAddress().getId() == null){
			invoice.setRegistryAddress(null);
		}
		fillTaxInfo(invoice);
	}

	/**
	 * Completes tax info of the Invoice.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		if(invoice.getRegistryAddress() != null && invoice.getRegistryAddress().getId() == null){
			invoice.setRegistryAddress(null);
		}
		fillTaxInfo(invoice);
	}
	
	/**
	 * Updates the point of sale
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			SalesInvoicingController salesInvoicingController = (SalesInvoicingController)event.getController();
			Invoice invoice = (Invoice)salesInvoicingController.getTo();
			updateSalesPos(invoice, salesInvoicingController.getPosId());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	/**
	 * Updates the point of sale.
	 * 
	 * @param invoice the invoice
	 * @throws ManagerBeanException 
	 */
	private void updateSalesPos(Invoice invoice, Integer posId) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator iter = invoiceDetailBean.getList(criteria).iterator();
		if(iter.hasNext()){
			InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
			DeliveryDetail deliveryDetail = obtainDeliveryDetail(invoiceDetail.getDeliveryDetail());
			Sales sales = deliveryDetail.getSalesDetail().getSales();
			sales.setPos(obtainPos(posId));
		}
	}

	/**
	 * After select a Invoice completes SalesInvoicingController's status.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SalesInvoicingController salesInvoicingController = (SalesInvoicingController)event.getController();
		salesInvoicingController.setWarehouseId(obtainWarehouseId((Invoice)salesInvoicingController.getTo()));
		salesInvoicingController.setPosId(obtainPosId((Invoice)salesInvoicingController.getTo()));
		Invoice invoice = (Invoice)salesInvoicingController.getTo();
		if(invoice.getRegistryAddress() == null){
			invoice.setRegistryAddress(new RegistryAddress());
		}
		super.afterBeanSelected(event);
	}
	
	/**
	 * After create a Invoice completes SalesInvoicingController's status.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		((SalesInvoicingController)event.getController()).setWarehouseId(null);
		((SalesInvoicingController)event.getController()).setPosId(null);
		super.afterBeanCreated(event);
	}
	
	/**
	 * Completes criteria.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeModelInitialized(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		SalesInvoicingController salesInvoicingController = (SalesInvoicingController)event.getController();
		Criteria criteria;
		try {
			criteria = salesInvoicingController.getCriteria();
			criteria.addEqualExpression(salesInvoicingController.getManagerBean().getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.SALES);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Calculates taxfree and surcharge for invoice.
	 * 
	 * @param invoice related invoice
	 */
	private void fillTaxInfo(Invoice invoice) {
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), invoice.getRegistry().getId());
			Iterator iter = customerBean.getList(criteria).iterator();
			boolean surcharge = false;
			boolean taxFree = false;
			if(iter.hasNext()){
				Customer customer = (Customer)iter.next();
				surcharge = customer.isSurcharge();
				taxFree = customer.isTaxFree();
			}
			invoice.setTaxFree(taxFree);
			invoice.setSurcharge(surcharge);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error obtaining customer", e);
		}
	}
	
	/**
	 * Searchs for Warehouse ident instead of an Invoice.
	 * 
	 * @param invoice related invoice
	 * 
	 * @return warehouse ident
	 */
	private Integer obtainWarehouseId(Invoice invoice) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DIRECT_SALES);
			Iterator iter = invoiceDetailBean.getList(criteria, 0, 1).iterator();
			DeliveryDetail deliveryDetail = null;
			if(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				deliveryDetail = obtainDeliveryDetail(invoiceDetail.getDeliveryDetail());
			}else{
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
				iter = invoiceDetailBean.getList(criteria, 0, 1).iterator();
				if(iter.hasNext()){
					InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
					deliveryDetail = obtainDeliveryDetail(invoiceDetail.getDeliveryDetail());
				}
			}
			if(deliveryDetail != null){
				return deliveryDetail.getWarehouse().getId();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining warehouse for invoice with id= " + invoice.getId(), e);
		}
		return null;
	}
	
	/**
	 * Obtain pos id.
	 * 
	 * @param invoice the invoice
	 * 
	 * @return the integer
	 */
	private Integer obtainPosId(Invoice invoice) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			Iterator iter = invoiceDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				DeliveryDetail deliveryDetail = obtainDeliveryDetail(invoiceDetail.getDeliveryDetail());
				if(deliveryDetail != null){
					return deliveryDetail.getSalesDetail().getSales().getPos().getId();
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining point of sale for invoice with id= " + invoice.getId(), e);
		}
		return null;
	}

	/**
	 * Searchs a DeliveryDetail eith this ident.
	 * 
	 * @param deliveryDetail DeliveryDetail's ident
	 * 
	 * @return related DeliveryDetail
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	private DeliveryDetail obtainDeliveryDetail(Integer deliveryDetail) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), deliveryDetail);
		Iterator iter = deliveryDetailBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (DeliveryDetail)iter.next();
		}
		return null;
	}
	
	/**
	 * Returns the PointOfSale with the given id
	 * 
	 * @param delivery the Delivery
	 * @return the Ident of the Warehouse
	 */
	private PointOfSale obtainPos(Integer posId) {
		try {
			IManagerBean posBean = BeanManager.getManagerBean(PointOfSale.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(posBean.getFieldName(ISalesAlias.POINT_OF_SALE_ID), posId);
			Iterator iter = posBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (PointOfSale)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining pos with id= " + posId, e);
		}
		return null;
	}
}