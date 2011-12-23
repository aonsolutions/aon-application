package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.ProgressionInvoicingFeedBack;
import com.code.aon.finance.invoicing.engine.IInvoicingEngine;
import com.code.aon.finance.invoicing.engine.InvoicingEngineFactory;
import com.code.aon.finance.invoicing.engine.delivery.DeliveryInvoicingDAO;
import com.code.aon.finance.invoicing.engine.delivery.DeliveryInvoicingEngine;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.customer.util.CustomerValidationManager;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.sign.controller.ISignatureController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.bridge.DeliveryTransferManager;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class SaleInvoiceController extends InvoiceController implements ISignatureController, IFinanceConstants, IFinanceMessages {
	
	private RegistryValidationManager vm;
	private DeliveryTransferManager deliveryTransferManager;
	private boolean showDeliveryTransferWindow;
	
	public SaleInvoiceController() {
		setInvoiceAddressControllerName(SALE_INVOICE_ADDRESS_CONTROLLER_NAME);
		setInvoiceDetailControllerName(SALE_INVOICE_DETAIL_CONTROLLER_NAME);
		setInvoiceFinanceControllerName(SALE_INVOICE_FINANCE_CONTROLLER_NAME);
	}

	private RegistryValidationManager getRegistryValidationManager() {
		if (vm == null) {
			vm = new CustomerValidationManager(); 
		}
		return vm;
	}

	public boolean isSeriesActive() throws ManagerBeanException {
		String seriesId = getInvoice().getSeries();
		if (StringUtils.isEmpty(seriesId)) {
			return true;
		} else {
			IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
			Series series = (Series)seriesBean.get(seriesId);
			return (series != null && series.isActive());
		}
	}

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		int number = obtainMaxNumber((String)event.getNewValue());
		SecurityLevel securityLevel = obtainSeriesSecurityLevel((String)event.getNewValue());
		if (getInvoice() != null) {
			getInvoice().setNumber(number);
			getInvoice().setSecurityLevel(securityLevel);
		}
	}

	private int obtainMaxNumber(String seriesId)  {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
    	return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	@SuppressWarnings("unchecked")
	private SecurityLevel obtainSeriesSecurityLevel(String seriesId) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ID), seriesId);
		Iterator iterator = seriesBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			Series series = (Series)iterator.next(); 
			if (series.getSecurityLevel() != null) {
				return series.getSecurityLevel();
			}
		}
		return null;
	}

	public void onFindNextFreeNumber(ActionEvent event) throws ManagerBeanException {
		int number = (getInvoice().getNumber() == 0 ? 1 : getInvoice().getNumber());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		while (true) {
			Criteria criteria = new Criteria();
			if (getInvoice().getSeries() == null) {
				criteria.addNullExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES));
			} else {
				criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES), getInvoice().getSeries());
			}
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_NUMBER), number);
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
			if (invoiceBean.getCount(criteria) == 0) {
				break;
			}
			number++;
		}

		getInvoice().setNumber(number);
	}

	public void customerData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			isBlocked(customer); // Saca el mensaje de bloqueo.
			getInvoice().setRegistryName(customer.getRegistry().getFullName());
			getInvoice().setRegistryDocument(customer.getRegistry().getDocument());
			getInvoice().setRegistryDocumentType(customer.getRegistry().getDocumentType());
			getInvoice().setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry());
			getInvoice().setRegistry(customer.getRegistry());
			loadAddresses(customer.getId());
			loadProjects(customer.getId());
		} else {
			setAddresses(null);	
			setProjects(null);	
		}
	}

	private boolean isBlocked(Customer customer) {
		return getRegistryValidationManager().isBlocked(customer);
	}

	public Customer getCustomer() throws ManagerBeanException{
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		if (getInvoice() != null) {
			criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_ID), getInvoice().getRegistry().getId());
			Iterator<?> iterator = customerBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				return (Customer)iterator.next();
			}
		}
		return null;
	}

	public DeliveryTransferManager getDeliveryTransferManager() {
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
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DELIVERY);
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
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
		if (!isReadOnly()) {
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			criteria = new Criteria();
			criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_CUSTOMER_ID), getInvoice().getRegistry().getId());
			criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_STATUS), DeliveryStatus.PENDING);
			criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_SECURITY_LEVEL), getInvoice().getSecurityLevel());
			criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_ISSUE_TIME));
			criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_SERIES));
			criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_NUMBER));
			deliveryList.addAll(deliveryBean.getList(criteria));
		}
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

		InvoicingEngineFactory.register(InvoicingEngineFactory.DELIVERY_ENGINE_KEY, new DeliveryInvoicingEngine());
		try {
			IInvoicingEngine engine = InvoicingEngineFactory.getInvoicingEngine(InvoicingEngineFactory.DELIVERY_ENGINE_KEY);
			engine.setInvoicingDAO(new DeliveryInvoicingDAO());
			engine.setInvoicingFeedBack(new ProgressionInvoicingFeedBack());
			((DeliveryInvoicingEngine)engine).invoiceDeliveryList(getInvoice(), getDeliveryTransferManager().getCheckedDelivery());
		} catch (InvoicingException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}

		refresh(null);
		IController detailController = FormUtil.getController(IFinanceConstants.SALE_INVOICE_DETAIL_CONTROLLER_NAME);
		detailController.onSearch(null);
	}

	private void removeInvoicedDelivery(Delivery delivery) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)iterator.next();
			criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DELIVERY);
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE_ID), deliveryDetail.getId());
			if (invoiceDetailBean.getList(criteria).iterator().hasNext()) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)invoiceDetailBean.getList(criteria).iterator().next();
				invoiceDetailBean.remove(invoiceDetail);
			}
		}
	}

}