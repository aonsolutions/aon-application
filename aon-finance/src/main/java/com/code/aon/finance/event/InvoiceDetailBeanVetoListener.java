package com.code.aon.finance.event;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceDetailBeanVetoListener.class.getName());

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
		setDefaultValues(invoiceDetail);
		checkLimitDate(invoiceDetail.getInvoice());
		if (invoiceDetail.getItem() != null && invoiceDetail.getItem().getId() != null) {
			invoiceDetail.setPrepayment(invoiceDetail.getItem().getProduct().getType() == ProductType.PREPAYMENT);
			if (invoiceDetail.getItem().getProduct().isInventoriable()) {
				try {
					invoiceDetail.setWarehouse(obtainWarehouse(invoiceDetail.getInvoice(), invoiceDetail.getSource(), invoiceDetail.getWorkPlace()));
				} catch (ManagerBeanException e) {
					LOGGER.error("Error obtaining warehouse for invoiceDetail with id= " + invoiceDetail.getId(), e);
				}
			}
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
		setDefaultValues(invoiceDetail);
		checkLimitDate(invoiceDetail.getInvoice());
		if (invoiceDetail.isUpdateEnabled() && invoiceDetail.getItem() != null) {
			invoiceDetail.setPrepayment(invoiceDetail.getItem().getProduct().getType() == ProductType.PREPAYMENT);
			try {
				removeInvoiceTax(invoiceDetail);
			} catch (ManagerBeanException e) {
				LOGGER.error("Error removing invoiceTax for invoiceDetail with id= " + invoiceDetail.getId(), e);
			}
		}
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
		checkLimitDate(invoiceDetail.getInvoice());
		try {
			removeInvoiceTax(invoiceDetail);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error removing invoiceTax for invoiceDetail with id= " + invoiceDetail.getId(), e);
		}
	}

	private void setDefaultValues(InvoiceDetail invoiceDetail) {
		if (invoiceDetail.getDiscountExpression() == null || StringUtils.isBlank(invoiceDetail.getDiscountExpression().getDiscountExpr())) {
			invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
		}
	}

	private void checkLimitDate(Invoice invoice) throws ManagerBeanVetoListenerException {
		if (!FinanceUtil.isValidLimitDate(invoice)) {
			throw new ManagerBeanVetoListenerException("La Fecha de la Factura rebasa la Fecha Limite de Operaciones.");
		}
	}

	private Warehouse obtainWarehouse(Invoice invoice, InvoiceSource source, WorkPlace workPlace) throws ManagerBeanException {
		if ((invoice.isSales() || invoice.isPurchase()) && invoice.isNoRectification() && source != InvoiceSource.DELIVERY && source != InvoiceSource.INCOME) {
			IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_ACTIVE), Boolean.TRUE);
			if (warehouseBean.getCount(criteria) == 1) {
				return (Warehouse)warehouseBean.getList(criteria).get(0);
			} else {
				criteria.addEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_ID), workPlace.getId());
				if (warehouseBean.getCount(criteria) == 1) {
					return (Warehouse)warehouseBean.getList(criteria).get(0);
				}
			}
		}

		return null;
	}

	private void removeInvoiceTax(InvoiceDetail invoiceDetail) throws ManagerBeanException {
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_ID),invoiceDetail.getId());
		for (ITransferObject ito : invoiceTaxBean.getList(criteria)) {
			invoiceTaxBean.remove((InvoiceTax)ito);
		}
	}
}
