package com.code.aon.ui.finance.util;

import java.util.Date;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceImportManager {

	public Invoice copyInvoice(Invoice source, String series, int number, String referenceCode, Registry registry, String registryName, Date date, 
			boolean registryChanged) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Integer sourceId = source.getId();
		Invoice invoice = source;
		invoice.setId(null);
		invoice.setSeries(series);
		invoice.setNumber(number);
		invoice.setReferenceCode(referenceCode);
		invoice.setRegistry(registry);
		invoice.setRegistryName(registryName);
		if (registryChanged) {
			invoice.setRegistryDocument(registry.getDocument());
			invoice.setRegistryDocumentType(registry.getDocumentType());
			invoice.setRegistryDocumentCountry(registry.getDocumentCountry());
			invoice.setRegistryAddress(registry.getDefaultAddress());
		}
		invoice.setIssueDate(date);
		invoice.setTaxDate(date);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setSigned(false);
		invoice.setScope(null);
		invoice.setRectificationType(RectificationType.NONE);
		invoice.setRectificationInvoice(null);
		invoice.setPosShift(null);
		invoice.setLines(null);
		invoice.setFinances(null);
		invoice.setAddresses(null);
		invoice.setAttachments(null);
		invoice = (Invoice)invoiceBean.insert(invoice);

		importInvoice(sourceId, invoice, registryChanged);
		return invoice;
	}

	private void importInvoice(Integer sourceId, Invoice invoice, boolean registryChanged) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), sourceId);
		List<ITransferObject> invoiceDetailList = invoiceDetailBean.getList(criteria);
		for (ITransferObject to : invoiceDetailList) {
			InvoiceDetail sourceDetail = (InvoiceDetail)to;
			boolean lastDetail = sourceDetail.equals(invoiceDetailList.get(invoiceDetailList.size()-1));
			InvoiceDetail invoiceDetail = sourceDetail;
			invoiceDetail.setId(null);
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
			invoiceDetail.setSourceId(null);
			invoiceDetail.getInvoice().setUpdateEnabled(lastDetail);
			invoiceDetailBean.insert(invoiceDetail);

			if (lastDetail) {
				invoice = invoiceDetail.getInvoice();
			}
		}

		if (!registryChanged) {
			IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
			criteria = new Criteria();
			criteria.addEqualExpression(invoiceAddressBean.getFieldName(IEntityAlias.INVOICE_ADDRESS_INVOICE_ID), sourceId);
			for (ITransferObject to : invoiceAddressBean.getList(criteria)) {
				InvoiceAddress sourceAddress = (InvoiceAddress)to;
				InvoiceAddress invoiceAddress = sourceAddress;
				invoiceAddress.setId(null);
				invoiceAddress.setInvoice(invoice);
				invoiceAddressBean.insert(invoiceAddress);
			}
		}

		FinanceGenerator financeGenerator = new FinanceGenerator();
		financeGenerator.generateFinances(invoice, invoice.getTotal());
	}

}
