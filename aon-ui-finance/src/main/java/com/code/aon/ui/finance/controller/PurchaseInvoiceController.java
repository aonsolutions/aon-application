package com.code.aon.ui.finance.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import org.xml.sax.SAXException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.report.ReportException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.finance.IFinanceMessages;

public class PurchaseInvoiceController extends InvoiceController implements IFinanceConstants, IFinanceMessages {
	
	private static final Logger LOGGER = Logger.getLogger(PurchaseInvoiceController.class.getName());
	
	//private CustomerValidationManager cvm;
	//private DeliveryTransferManager deliveryTransferManager;
	//private boolean showDeliveryTransferWindow;

	public PurchaseInvoiceController() {
		setInvoiceAddressControllerName(PURCHASE_INVOICE_ADDRESS_CONTROLLER_NAME);
		setInvoiceDetailControllerName(PURCHASE_INVOICE_DETAIL_CONTROLLER_NAME);
		setInvoiceFinanceControllerName(PURCHASE_INVOICE_FINANCE_CONTROLLER_NAME);
	}

	/*private CustomerValidationManager getCustomerValidationManager() {
		if (cvm == null) {
			cvm = new CustomerValidationManager(); 
		}
		return cvm;
	}*/
	
	public void supplierData(LookupChangeEvent event) throws ManagerBeanException{
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Supplier supplier = (Supplier)event.getNewValue();
			//isBlocked(customer); // Saca el mensaje de bloqueo.
			getInvoice().setRegistryName(supplier.getRegistry().getFullName());
			getInvoice().setRegistryDocument(supplier.getRegistry().getDocument());
			getInvoice().setRegistry(supplier.getRegistry());
			loadAddresses(supplier.getId());
		} else {
			setAddresses(null);	
		}
	}

	/*private boolean isBlocked(Customer customer) {
		return getCustomerValidationManager().isBlocked(customer);
	}*/

	/*public DeliveryTransferManager getDeliveryTransferManager() {
		if (deliveryTransferManager == null) {
			deliveryTransferManager = new DeliveryTransferManager(); 
		}
		return deliveryTransferManager;
	}

	public void setDeliveryTransferManager(DeliveryTransferManager deliveryTransferManager) {
		this.deliveryTransferManager = deliveryTransferManager;
	}

	public boolean isShowDeliveryTransferWindow() {
		return showDeliveryTransferWindow;
	}

	public void setShowDeliveryTransferWindow(boolean value) {
		this.showDeliveryTransferWindow = value;
	}
	
	public void onDeliveryTransferShow(ActionEvent event) throws ManagerBeanException {
		List<ITransferObject> invoicedDeliveryList = new LinkedList<ITransferObject>();
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DELIVERY);
		criteria.addOrder(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_LINE));
		Iterator<?> iterator = invoiceDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
			DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailBean.get(invoiceDetail.getSourceId());
			if (!invoicedDeliveryList.contains(deliveryDetail.getDelivery())) {
				invoicedDeliveryList.add(deliveryDetail.getDelivery());
				getDeliveryTransferManager().setDeliveryRowChecked(deliveryDetail.getDelivery(), true);
			}
		}

		getDeliveryTransferManager().setInvoicedDeliveryList(invoicedDeliveryList);

		List<ITransferObject> deliveryList = new LinkedList<ITransferObject>();
		deliveryList.addAll(invoicedDeliveryList);
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		criteria = new Criteria();
		criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID), getInvoice().getRegistry().getId());
		criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_STATUS), DeliveryStatus.PENDING);
		criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_SECURITY_LEVEL), getInvoice().getSecurityLevel());
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_ISSUE_TIME));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_SERIES));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_NUMBER));
		deliveryList.addAll(deliveryBean.getList(criteria));

		getDeliveryTransferManager().setDeliveryList(deliveryList);
	}

	public void onDeliveryTransfer(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = getDeliveryTransferManager().getInvoicedDeliveryList().iterator();
		while (iterator.hasNext()) {
			Delivery delivery = (Delivery)iterator.next();
			if (!getDeliveryTransferManager().getCheckedDelivery().contains(delivery)) {
				removeInvoicedDelivery(delivery);
			}
			getDeliveryTransferManager().getCheckedDelivery().remove(delivery);
		}

		InvoicingEngineFactory.register(InvoicingEngineFactory.CUSTOMER_FEE_ENGINE_KEY, new DeliveryInvoicingEngine());
		try {
			IInvoicingEngine engine = InvoicingEngineFactory.getInvoicingEngine(InvoicingEngineFactory.CUSTOMER_FEE_ENGINE_KEY);
			engine.setInvoicingDAO(new DeliveryInvoicingDAO());
			engine.setInvoicingFeedBack(new ProgressionInvoicingFeedBack());
			((DeliveryInvoicingEngine)engine).invoiceDeliveryList(getInvoice(), getDeliveryTransferManager().getCheckedDelivery());
		} catch (InvoicingException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}

		IController detailController = FormUtil.getController(IFinanceConstants.SALE_INVOICE_DETAIL_CONTROLLER_NAME);
		detailController.onSearch(null);
	}

	private void removeInvoicedDelivery(Delivery delivery) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)iterator.next();
			criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DELIVERY);
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE_ID), deliveryDetail.getId());
			if (invoiceDetailBean.getList(criteria).iterator().hasNext()) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)invoiceDetailBean.getList(criteria).iterator().next();
				invoiceDetailBean.remove(invoiceDetail);
			}
		}
	}*/

	public void onSendInvoiceByEmail( ActionEvent event ) throws ManagerBeanException, ReportException, IOException, SAXException {
		sendInvoiceByEmail( null, false );
	}

}