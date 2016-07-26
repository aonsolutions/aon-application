package com.code.aon.ui.finance.file;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.file.bank.model.CSB34.CSB34;
import com.code.aon.file.bank.model.CSB34.data.Check;
import com.code.aon.file.bank.model.CSB34.data.Detail;
import com.code.aon.file.bank.model.CSB34.data.Master;
import com.code.aon.file.bank.model.CSB34.data.Orderer;
import com.code.aon.file.bank.model.CSB34.data.Receiver;
import com.code.aon.file.bank.model.CSB34.data.Transfer;
import com.code.aon.file.format.core.Account;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.esferalia.aon.entity.IEntityAlias;

public class AEB34Writer implements IFinanceConstants {

	private LogPanelController logPanel;

	
	public void setLogPanel(LogPanelController logPanel) {
		this.logPanel = logPanel;
	}
	
	private void updateLogPanel( int current, int total ) {
		if ( this.logPanel != null ) {
			this.logPanel.info("Vencimiento "+ current + " de " + total + " procesado.");
		}
	}

	public Master getMaster(Company company, FinanceBatch fBatch, List<FinanceBatchDetail> fbatchDetails) throws ManagerBeanException {
		Orderer orderer = new Orderer();
		orderer.setCode(StringUtils.leftPad(company.getDocument(), 10));
		orderer.setName(company.getName());
		orderer.setAddress(company.getDefaultAddress().getFullAddress());
		orderer.setCity(company.getDefaultAddress().getCity());

		Account account = new Account();
		account.parse(fBatch.getRegistryBank().getBankAccount().getBban());

		Master master = new Master();
		master.setOrderer(orderer);
		master.setAccount(account);
		master.setSendDate(fBatch.getIssueDate());
		master.setOrderDate(fBatch.getIssueDate());
		master.setDetail("0");

		int current = 0;
		for( FinanceBatchDetail fBatchDetail : fbatchDetails ) {
			Detail detail = createDetail(fBatchDetail.getFinance());
			master.addReceiver(detail);
			updateLogPanel(++current, fbatchDetails.size());
		}
		return master;
	}

	public FileOutput createAEB34(Company company, FinanceBatch fBatch, List<FinanceBatchDetail> fbatchDetails) throws ManagerBeanException {
		Master master = getMaster(company, fBatch, fbatchDetails);
		try {
			File file = File.createTempFile("AEB34_", ".txt");
			FileFiller csb34 = new CSB34(master, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(csb34.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private Detail createDetail(Finance finance) throws ManagerBeanException {
		Receiver receiver = new Receiver();
		receiver.setCode(finance.getRegistryDocument());
		receiver.setName(finance.getRegistryName());
		IAddress iAddress = obtainInvoiceAddress(finance.getInvoice(), finance.getRegistry());
		if (iAddress != null) {
			receiver.setAddress(iAddress.getFullAddress());
			receiver.setZip(iAddress.getZip());
			receiver.setCity(iAddress.getCity());
			receiver.setProvince(iAddress.getGeozone().getName());
		}

		Account account = new Account();
		account.parse(finance.getBankAccount().getBban());

		Detail detail = (finance.getPayMethod().getType() == PayMethodType.BANK_TRANSFER) ? new Transfer() : new Check();
		detail.setReceiver(receiver);
		detail.setAccount(account);
		detail.setMode(finance.isPayroll() ? "1" : "9");
		detail.setAmount(new Double(finance.getTotalAmount()));
		detail.setConcept((!finance.isEmptyInvoice()) ? "PAGO FACTURA: " + finance.getInvoice().getReferenceCode() : finance.getConcept());
		return detail;
	}

	@SuppressWarnings("rawtypes")
	public static IAddress obtainInvoiceAddress(Invoice invoice, Registry registry) throws ManagerBeanException {
		if (invoice != null && invoice.getId() != null) {
			IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceAddressBean.getFieldName(IEntityAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
			Iterator iterator = invoiceAddressBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				return (InvoiceAddress)iterator.next();
			}
		}
		return obtainRegistryAddress(registry.getId());
	}

	@SuppressWarnings("rawtypes")
	public static IAddress obtainRegistryAddress(Integer registryId) throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), registryId);
		Iterator iterator = rAddressBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (RegistryAddress)iterator.next();
		}
		return null;
	}

}