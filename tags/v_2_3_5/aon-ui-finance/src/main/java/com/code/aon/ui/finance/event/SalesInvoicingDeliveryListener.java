package com.code.aon.ui.finance.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.SelectItem;

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
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.ui.finance.controller.SalesInvoicingController;
import com.code.aon.ui.finance.controller.SalesInvoicingDetailController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * SalesInvoicing controller's listener
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class SalesInvoicingDeliveryListener extends ControllerAdapter {
	
	/**
	 * The class logger
	 */
	private static final Logger LOGGER = Logger.getLogger(SalesInvoicingDeliveryListener.class.getName());
	
	/**
	 * Delivery controller's name
	 */
	private static final String DELIVERY_CONTROLLER_NAME = "delivery";
	
	/**
	 * SalesInvoicingDetail controller's name
	 */
	private static final String SALES_INVOICING_DETAIL_CONTROLLER_NAME = "salesInvoicingDetail";

	/**
	 * SalesFinance controller's name
	 */
	private static final String SALES_FINANCE_CONTROLLER = "salesFinance";
	
	/**
	 * Completes Invoice before add, type and status
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice) event.getController().getTo();
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
	}
	
	/**
	 * Searchs Delivery controller instead of this Invoice
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		try {
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID), invoice.getRegistry().getId());
			criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_STATUS), DeliveryStatus.PENDING);
			DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
			deliveryController.setCriteria(criteria);
			deliveryController.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving delivery model for customer= " + invoice.getRegistry().getId(), e);
		}
	}
	
	/**
	 * When an Invoice is selected load a correct environment;
	 * Recovers pending deliveries and add to closed,
	 * Reloads deliveryControllers data model,
	 * Updates SalesInvoicingDetailController references,
	 * Loas SalesInvoicingController addresses.
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		List<ITransferObject> pending = obtaingPendingDeliveries(invoice.getRegistry().getId());
		List<ITransferObject> closed = obtainClosedDeliveries(invoice.getId());
		closed.addAll(pending);
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		PageDataModel pdm = new PageDataModel(event.getController(), ((BasicController)event.getController()).getPageLimit());
		pdm.setWrappedData(closed);
		pdm.resize(closed.size());
		deliveryController.setModel(pdm);
		updateDetailControllerReferences(invoice);
		((SalesInvoicingController)event.getController()).loadAddresses(invoice.getRegistry().getId());
	}
	
	/**
	 * Updates SalesInvoicingDetailController, 
	 * assigns Delivery and Sales
	 * 
	 * @param invoice
	 */
	private void updateDetailControllerReferences(Invoice invoice) {
		SalesInvoicingDetailController salesInvoicingDetailController = (SalesInvoicingDetailController)AonUtil.getController(SALES_INVOICING_DETAIL_CONTROLLER_NAME);
		DeliveryDetail deliveryDetail = obtainDeliveryDetail(invoice);
		if(deliveryDetail != null){
			salesInvoicingDetailController.setDelivery(deliveryDetail.getDelivery());
			salesInvoicingDetailController.setSales(deliveryDetail.getSalesDetail().getSales());
		}
	}

	/**
	 * Recovers a DeliveryDetail with this Invoice
	 * 
	 * @param invoice related invoice
	 * @return DeliveryDetail
	 */ 
	private DeliveryDetail obtainDeliveryDetail(Invoice invoice) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			Iterator iter = invoiceDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				return obtainDeliveryDetail(invoiceDetail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining asociated delivery for invoice with id= " + invoice.getId(), e);
		}
		return null;
	}

	/**
	 * Recovers a DeliveryDetail with this InvoiceDetail
	 * 
	 * @param invoiceDetail related InvoiceDetail
	 * @return DeliveryDetail
	 */
	private DeliveryDetail obtainDeliveryDetail(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), invoiceDetail.getDeliveryDetail());
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (DeliveryDetail)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining asociated deliveryDetail for invoiceDetail with id= " + invoiceDetail.getId(), e);
		}
		return null;
	}
	
	/**
	 * Removes all DeliveryDetails related to SalesInvoicingDetails,
	 * Obtains closed deliveries and updates to pending.
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event){
		try {
			SalesInvoicingDetailController salesInvoicingDetailController = (SalesInvoicingDetailController) AonUtil.getController(SALES_INVOICING_DETAIL_CONTROLLER_NAME);
			Iterator iter = ((List)salesInvoicingDetailController.getModel().getWrappedData()).iterator();
			while(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				if(invoiceDetail.getSource().equals(InvoiceSource.DIRECT_SALES)){
					removeDeliveryDetails(invoiceDetail);
				}
			}
			iter = obtainClosedDeliveries(((Invoice)event.getController().getTo()).getId()).iterator();
			SalesInvoicingController salesInvoicingController = (SalesInvoicingController)event.getController();
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			while(iter.hasNext()){
				Delivery delivery = (Delivery)iter.next();
				salesInvoicingController.removeInvoiceDetails(delivery.getId());
				delivery.setStatus(DeliveryStatus.PENDING);
				deliveryBean.update(delivery);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error removing invoiceDetails", e);
		}
	}
	
	/**
	 * Initialices controllers addreses and other related controllers,  
	 * salesFinanceController and salesInvoicingDetailController
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SalesInvoicingDetailController salesInvoicingDetailController = (SalesInvoicingDetailController)AonUtil.getController(SALES_INVOICING_DETAIL_CONTROLLER_NAME);
		LinesController salesFinanceController = (LinesController)AonUtil.getController(SALES_FINANCE_CONTROLLER);
		salesFinanceController.onCancel(null);
		salesInvoicingDetailController.setDelivery(null);
		salesInvoicingDetailController.setSales(null);
		((SalesInvoicingController)event.getController()).setAddresses(new LinkedList<SelectItem>());
	}
	
	/**
	 * Resets DeliveryController
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		try {
			deliveryController.onReset(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	/**
	 * Removes DeliveryDetails and Delivery (header) if needed. 
	 * 
	 * @param invoiceDetail related InvoiceDetail
	 */
	private void removeDeliveryDetails(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), invoiceDetail.getDeliveryDetail());
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			int number = deliveryDetailBean.getCount(criteria);
			if(iter.hasNext()){
				DeliveryDetail deliveryDetail = (DeliveryDetail)iter.next();
				deliveryDetailBean.remove(deliveryDetail);
				if(number == 1){
					IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
					deliveryBean.remove(deliveryDetail.getDelivery());
				}
				removeSalesDetails(deliveryDetail.getSalesDetail(), number == 1);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error removing deliverydetails for invoiceDetail with id= " + invoiceDetail.getId(), e);
		}
		
	}

	/**
	 * Removes SalesDetail and Sales (header) if needed
	 * 
	 * @param salesDetail related SalesDetail
	 * @param hasToRemoveHeader true if wanted to remove header
	 */
	private void removeSalesDetails(SalesDetail salesDetail, boolean hasToRemoveHeader) {
		try {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			salesDetailBean.remove(salesDetail);
			if(hasToRemoveHeader){
				IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
				salesBean.remove(salesDetail.getSales());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error removing salesdetails ", e);
		}
	}

	/**
	 * Recovers a list of closed Deliveries
	 * 
	 * @param invoiceId invoice ident
	 * @return list of Deliveries
	 */
	private List<ITransferObject> obtainClosedDeliveries(Integer invoiceId) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoiceId);
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DELIVERY);
			Iterator iter = invoiceDetailBean.getList(criteria).iterator();
			List<ITransferObject> deliveries = new LinkedList<ITransferObject>();
			List<Integer> ids = new LinkedList<Integer>();
			while(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				criteria = new Criteria();
				criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), invoiceDetail.getDeliveryDetail());
				Iterator iterator = deliveryDetailBean.getList(criteria).iterator();
				if(iterator.hasNext()){
					DeliveryDetail deliveryDetail = (DeliveryDetail)iterator.next();
					if(!ids.contains(deliveryDetail.getDelivery().getId())){
						ids.add(deliveryDetail.getDelivery().getId());
						deliveries.add(deliveryDetail.getDelivery());
					}
				}
			}
			return deliveries;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving deliveries closed by invoice id= " + invoiceId, e);
		}
		return null;
	}
	
	/**
	 * Recovers Pending Deliveries for this Customer
	 * 
	 * @param customerId customer's ident
	 * @return list of Deliveries
	 */
	private List<ITransferObject> obtaingPendingDeliveries(Integer customerId) {
		try {
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID), customerId);
			criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_STATUS), DeliveryStatus.PENDING);
			return deliveryBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving pending deliveries for customer with id= " + customerId, e);
		}
		return null;
	}

}
