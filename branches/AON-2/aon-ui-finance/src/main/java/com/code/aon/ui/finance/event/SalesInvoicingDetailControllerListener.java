package com.code.aon.ui.finance.event;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.sales.PointOfSale;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.finance.controller.SalesInvoicingController;
import com.code.aon.ui.finance.controller.SalesInvoicingDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * SalesInvoicingDetail controller's listener
 * 
 * @author Consulting & Development. 
 * @since 1.0
 *
 */
public class SalesInvoicingDetailControllerListener extends ControllerAdapter {
	
	/**
	 * The class logger
	 */
	private static final Logger LOGGER = Logger.getLogger(SalesInvoicingDetailControllerListener.class.getName());

	/**
	 * SalesInvoicing controller's name
	 */
	private static final String SALES_INVOICING_CONTROLLER_NAME = "salesInvoicing";
	
	/**
	 * Controller's Price Strategy
	 */
	private IPriceStrategy priceStrategy;

	/**
	 * Completes InvoiceDetail before add,
	 * inserts DeliveryDetail
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		SalesInvoicingDetailController salesInvoicingDetailController = (SalesInvoicingDetailController)event.getController();
		SalesInvoicingController salesInvoicingController = (SalesInvoicingController)AonUtil.getController(SALES_INVOICING_CONTROLLER_NAME);
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();  
		invoiceDetail.setInvoice((Invoice)salesInvoicingController.getTo());
		obtainTaxableBase(invoiceDetail);
		((InvoiceDetail)event.getController().getTo()).setSource(InvoiceSource.DIRECT_SALES);
		invoiceDetail.setSourceId(insertDeliveryDetail(invoiceDetail,salesInvoicingDetailController));
	}
	
	/**
	 * Completes InvoiceDetail before update,
	 * Updates DeliveryDetail
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		obtainTaxableBase(invoiceDetail);
		updateDeliveryDetail(invoiceDetail);
		invoiceDetail.setWorkPlace(obtainWorkPlace(invoiceDetail.getWorkPlace().getId()));
	}
	
	/**
	 * Calculates and assigns InvoiceDetail's taxable base
	 * 
	 * @param invoiceDetail related InvoiceDetail 
	 */
	private void obtainTaxableBase(InvoiceDetail invoiceDetail) {
		invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
	}
	
	/**
	 * Returns Price Strategy
	 * 
	 * @return PriceStrategy
	 */
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}
	
	/**
	 * Before remove InvoiceDetail,
	 * removes related DeliveryDetail and resets SalesInvoicingDetailController
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		SalesInvoicingDetailController salesInvoicingDetailController = (SalesInvoicingDetailController)event.getController();
		int deliveryDetailNumber = obtainDeliveryDetailNumber(salesInvoicingDetailController.getDelivery());
		if(deliveryDetailNumber != 0){
			removeDeliveryDetail((InvoiceDetail)salesInvoicingDetailController.getTo(),deliveryDetailNumber == 1);
			if(deliveryDetailNumber == 1){
				salesInvoicingDetailController.setDelivery(null);
				salesInvoicingDetailController.setSales(null);
			}
		}
	}
	
	/**
	 * Returns the number of lines of this Delivery
	 * 
	 * @param delivery Related Delivery
	 * @return total lines
	 */
	private int obtainDeliveryDetailNumber(Delivery delivery) {
		if(delivery != null){
			try {
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
				return deliveryDetailBean.getCount(criteria);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error retrieving deliveryDetail number for delivery with id= " + delivery.getId(), e);
			}
		}
		return 0;
	}

	/**
	 * Creates a DeliveryDetail, and completes it
	 * with this InvoiceDetail and SalesInvoicingDetailController
	 * 
	 * @param invoiceDetail related InvoiceDetail
	 * @param salesInvoicingDetailController related SalesInvoicingDetailController
	 * @return The ident of the DeliveryDetail
	 */
	private Integer insertDeliveryDetail(InvoiceDetail invoiceDetail, SalesInvoicingDetailController salesInvoicingDetailController) {
		DeliveryDetail deliveryDetail = new DeliveryDetail();
		deliveryDetail.setDiscountExpression(invoiceDetail.getDiscountExpression());
		deliveryDetail.setItem(invoiceDetail.getItem());
        deliveryDetail.setDescription(invoiceDetail.getDescription());
		deliveryDetail.setPrice(invoiceDetail.getPrice());
		deliveryDetail.setQuantity(invoiceDetail.getQuantity());
		SalesInvoicingController salesInvoicingController = (SalesInvoicingController)AonUtil.getController(SALES_INVOICING_CONTROLLER_NAME);
		deliveryDetail.setWarehouse(new Warehouse());
		deliveryDetail.getWarehouse().setId(salesInvoicingController.getWarehouseId());
		//deliveryDetail.setSalesDetail(insertSalesDetail(invoiceDetail,salesInvoicingDetailController));
		if(salesInvoicingDetailController.getDelivery() == null){
			deliveryDetail.setDelivery(insertDelivery(invoiceDetail.getInvoice(),salesInvoicingDetailController));
		}else{
			deliveryDetail.setDelivery(salesInvoicingDetailController.getDelivery());
		}
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			return ((DeliveryDetail)deliveryDetailBean.insert(deliveryDetail)).getId();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting deliveryDetail for invoice with id: " + invoiceDetail.getInvoice().getId(),e);
			return null;
		}
	}
	
	/**
	 * Creates a SalesDetail, and completes it
	 * with this InvoiceDetail and SalesInvoicingDetailController
	 * 
	 * @param invoiceDetail related InvoiceDetail
	 * @param salesInvoicingDetailController related SalesInvoicingDetailController
	 * @return The SalesDetail
	 */
	private SalesDetail insertSalesDetail(InvoiceDetail invoiceDetail, SalesInvoicingDetailController salesInvoicingDetailController) {
		SalesDetail salesDetail = new SalesDetail();
        salesDetail.setLine(invoiceDetail.getLine());
		salesDetail.setItem(invoiceDetail.getItem());
        salesDetail.setDescription(invoiceDetail.getDescription());
		salesDetail.setPrice(invoiceDetail.getPrice());
		salesDetail.setQuantity(invoiceDetail.getQuantity());
		if(salesInvoicingDetailController.getSales() == null){
			salesDetail.setSales(insertSales(invoiceDetail.getInvoice(),salesInvoicingDetailController));
		}else{
			salesDetail.setSales(salesInvoicingDetailController.getSales());
		}
		try {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			return (SalesDetail)salesDetailBean.insert(salesDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting salesDetail for invoice with id: " + invoiceDetail.getInvoice().getId(),e);
			return null;
		}
	}
	
	/**
	 * Creates a Sales, and completes it
	 * with this Invoice and SalesInvoicingDetailController
	 * 
	 * @param invoice related Invoice
	 * @param salesInvoicingDetailController related SalesInvoicingDetailController
	 * @return created Sales
	 */
	private Sales insertSales(Invoice invoice, SalesInvoicingDetailController salesInvoicingDetailController) {
		Sales sales = new Sales();
		SalesInvoicingController salesInvoicingController = (SalesInvoicingController)AonUtil.getController(SALES_INVOICING_CONTROLLER_NAME);
		sales.setPos(obtainPos(salesInvoicingController.getPosId()));
		sales.setCustomer(new Customer());
		sales.getCustomer().setRegistry(new Registry());
		sales.getCustomer().getRegistry().setAlias(invoice.getRegistry().getAlias());
		sales.getCustomer().getRegistry().setDocument(invoice.getRegistry().getDocument());
		sales.getCustomer().setId(invoice.getRegistry().getId());
		sales.getCustomer().getRegistry().setId(invoice.getRegistry().getId());
		sales.getCustomer().getRegistry().setName(invoice.getRegistry().getName());
		sales.getCustomer().getRegistry().setSurname(invoice.getRegistry().getSurname());
		sales.setIssueDate(invoice.getIssueDate());
		sales.setSeries(invoice.getSeries());
		sales.setNumber(SeriesNumberUtil.obtainNumber(invoice.getSeries(), "Sales"));
		sales.setPayMethod(null);
		sales.setSecurityLevel(invoice.getSecurityLevel());
		sales.setStatus(SalesStatus.CLOSED);
		sales.setWorkPlace(obtainWorkPlace());
		try {
			IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
			sales = (Sales)salesBean.insert(sales);
			salesInvoicingDetailController.setSales(sales);
			return sales;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting sales for invoice with id: " + invoice.getId(),e);
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

	private WorkPlace obtainWorkPlace(Integer id) {
		try {
			IManagerBean wpBean = BeanManager.getManagerBean(WorkPlace.class);
			return (WorkPlace)wpBean.get(id);
		}
		catch (ManagerBeanException mbe) {
			mbe.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates a Delivery, and completes it
	 * with this Invoice and SalesInvoicingDetailController
	 * 
	 * @param invoice related Invoice
	 * @param salesInvoicingDetailController related SalesInvoicingDetailController
	 * @return created Delivery
	 */
	private Delivery insertDelivery(Invoice invoice, SalesInvoicingDetailController salesInvoicingDetailController) {
		Delivery delivery = new Delivery();
		delivery.setCustomer(new Customer());
		delivery.getCustomer().setRegistry(new Registry());
		delivery.getCustomer().getRegistry().setAlias(invoice.getRegistry().getAlias());
		delivery.getCustomer().getRegistry().setDocument(invoice.getRegistry().getDocument());
		delivery.getCustomer().setId(invoice.getRegistry().getId());
		delivery.getCustomer().getRegistry().setId(invoice.getRegistry().getId());
		delivery.getCustomer().getRegistry().setName(invoice.getRegistry().getName());
		delivery.getCustomer().getRegistry().setSurname(invoice.getRegistry().getSurname());
		delivery.setIssueTime(invoice.getIssueDate());
		delivery.setRaddress((invoice.getRegistryAddress()!=null&&invoice.getRegistryAddress().getId()!=null)?invoice.getRegistryAddress():null);
		delivery.setSeries(invoice.getSeries());
		delivery.setNumber(SeriesNumberUtil.obtainNumber(invoice.getSeries(), "Delivery"));
		delivery.setSecurityLevel(invoice.getSecurityLevel());
		delivery.setStatus(DeliveryStatus.CLOSED);
		delivery.setWorkPlace(obtainWorkPlace());
		try {
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			delivery = (Delivery)deliveryBean.insert(delivery);
			salesInvoicingDetailController.setDelivery(delivery);
			return delivery;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting delivery for invoice with id: " + invoice.getId(),e);
			return null;
		}
	}
	
	/**
	 * Recoves the related DeliveryDetail and updates it with this InoviceDetail 
	 * 
	 * @param invoiceDetail related InvoiceDetail
	 */
	@SuppressWarnings("unchecked")
	private void updateDeliveryDetail(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID),invoiceDetail.getSourceId());
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				DeliveryDetail deliveryDetail = (DeliveryDetail)iter.next();
				deliveryDetail.setDiscountExpression(invoiceDetail.getDiscountExpression());
                deliveryDetail.setItem(invoiceDetail.getItem());
                deliveryDetail.setDescription(invoiceDetail.getDescription());
				deliveryDetail.setPrice(invoiceDetail.getPrice());
				deliveryDetail.setQuantity(invoiceDetail.getQuantity());
				//deliveryDetail.setSalesDetail(updateSalesDetail(deliveryDetail));
				deliveryDetailBean.update(deliveryDetail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error updating deliveryDetail for invoiceDetail with id: " + invoiceDetail.getId(),e);
		}
	}
	
	/**
	 * Recovers SalesDetail and updates it with this DeliveryDetail
	 * 
	 * @param deliveryDetail related DeliveryDetail
	 * @return Updated SalesDetail
	 */
	private SalesDetail updateSalesDetail(DeliveryDetail deliveryDetail) {
		SalesDetail salesDetail = deliveryDetail.getSalesDetail();
		salesDetail.setItem(deliveryDetail.getItem());
        salesDetail.setDescription(deliveryDetail.getDescription());
		salesDetail.setPrice(deliveryDetail.getPrice());
		salesDetail.setQuantity(deliveryDetail.getQuantity());
		try {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			salesDetail = (SalesDetail)salesDetailBean.update(salesDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error updating salesDetail for deliveryDetail with id: " + deliveryDetail.getId(),e);
		}
		return salesDetail;
	}
	
	/**
	 * Recoves and Removes this InvoiceDetail's DeliveryDetail
	 * and the Header if needed
	 * 
	 * @param invoiceDetail related InvoiceDetail
	 * @param hasToRemoveHeaders true to remove header
	 */
	@SuppressWarnings("unchecked")
	private void removeDeliveryDetail(InvoiceDetail invoiceDetail, boolean hasToRemoveHeaders) {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID),invoiceDetail.getSourceId());
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				DeliveryDetail deliveryDetail = (DeliveryDetail)iter.next();
				deliveryDetailBean.remove(deliveryDetail);
				if(hasToRemoveHeaders){
					removeDelivery(deliveryDetail.getDelivery());
				}
				if (deliveryDetail.getSalesDetail() != null && deliveryDetail.getSalesDetail().getId() != null) {
					removeSalesDetail(deliveryDetail.getSalesDetail(),hasToRemoveHeaders);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error removing deliveryDetail for invoiceDetail with id: " + invoiceDetail.getId(), e);
		}
	}

	/**
	 * Removes this Delivery
	 * 
	 * @param delivery related Delivery
	 */
	private void removeDelivery(Delivery delivery) {
		try {
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			deliveryBean.remove(delivery);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error removing delivery with id: " + delivery.getId(), e);
		}
	}

	/**
	 * Removes this SalesDetail
	 * and the Header if needed
	 * 
	 * @param salesDetail related SalesDetail
	 * @param hasToRemoveHeaders true to remove header
	 */
	private void removeSalesDetail(SalesDetail salesDetail, boolean hasToRemoveHeaders) {
		try {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			salesDetailBean.remove(salesDetail);
			if(hasToRemoveHeaders){
				removeSales(salesDetail.getSales());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error removing salesDetail with id: " + salesDetail.getId(), e);
		}
		
	}

	/**
	 * Removes this Sales
	 * 
	 * @param sales related Sales
	 */
	private void removeSales(Sales sales) {
		try {
			IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
			salesBean.remove(sales);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error removing sales with id: " + sales.getId(), e);
		}
	}
	
	/**
	 * Returns the PointOfSale with the given id
	 * 
	 * @param posId the ident of the point of sale
	 * @return the point of sale
	 */
	@SuppressWarnings("unchecked")
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
