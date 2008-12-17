package com.code.aon.ui.finance.csb;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.csb.fd0.core.Account;
import com.code.aon.csb.fd0.model.FileFiller;
import com.code.aon.csb.fd0.model.CSB19.CSB19;
import com.code.aon.csb.fd0.model.CSB19.data.Individual;
import com.code.aon.csb.fd0.model.CSB19.data.Lot;
import com.code.aon.csb.fd0.model.CSB19.data.Orderer;
import com.code.aon.csb.fd0.model.CSB19.data.Presenter;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.csb.CSBOutput;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.finance.controller.FBatchDetailController;
import com.code.aon.ui.util.AonUtil;

public class CSB19Writer {
	
	private static final String FINANCE_BATCH_DETAIL_CONTROLLER_NAME = "fBatchDetail";

	@SuppressWarnings("unchecked")
	public CSBOutput createCSB19(Company company, FinanceBatch fbatch) throws ManagerBeanException {
		Lot lot = new Lot();
		try {
		if(fbatch.getFinanceBatchType().equals(FinanceBatchType.CSB_19_D)){
			lot.setType(Lot.EXTENDED);
		}
		if(fbatch.getFinanceBatchType().equals(FinanceBatchType.CSB_19)){
			lot.setType(Lot.RESUMED);
		}
		RegistryBank companyRBank = fbatch.getRegistryBank();
		Presenter presenter = new Presenter();
		presenter.setCode(company.getDocument());
		presenter.setSufix(companyRBank.getSufix());
		presenter.setMakeDate(fbatch.getIssueDate());
		presenter.setName(company.getName());
		presenter.setEntity(companyRBank.getBankAccount().substring(0,4));
		presenter.setOffice(companyRBank.getBankAccount().substring(4,8));
		
		lot.setPresenter(presenter);
		
		Orderer orderer = new Orderer();
		Account companyAccount = new Account();
		companyAccount.parse(companyRBank.getBankAccount());
		orderer.setAccount(companyAccount);
		orderer.setCode(company.getDocument());
        orderer.setName(company.getName());
		orderer.setMakeDate(fbatch.getIssueDate());
		orderer.setProcedure(new Integer(1));
		orderer.setStartDate(fbatch.getIssueDate());
		orderer.setSufix(companyRBank.getSufix());
		FBatchDetailController fBatchDetailController = (FBatchDetailController)AonUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator iter = ((List)fBatchDetailController.getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)iter.next();
			Individual individual  = createIndividual(fBatchDetail, lot.getType());
			orderer.addIndividual(individual);
		}
		
		lot.addOrderer(orderer);
		
		File file = File.createTempFile("CSB19_", ".txt");
		FileFiller csb19 = new CSB19(lot, file.getAbsolutePath());
		CSBOutput output = new CSBOutput();
		output.setFile(file);
		output.setErrors(csb19.create());
		return output;
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error creating CSB", e);
		} catch (IOException e) {
			throw new ManagerBeanException("Error creating CSB", e);
		}
	}
	
	private Individual createIndividual(FinanceBatchDetail fBatchDetail, int lotType) throws ManagerBeanException {
		Individual individual = new Individual();
		individual.setAmount(new Double(fBatchDetail.getFinance().getTotalAmount()));
		Account detailAccount = new Account();
		detailAccount.parse(fBatchDetail.getFinance().getBankAccount());
		individual.setAccount(detailAccount);
		individual.setConcept(fBatchDetail.getFinance().getConcept());
		individual.setInternalCode(fBatchDetail.getFinance().getInvoice().getSeries() + "/" + fBatchDetail.getFinance().getInvoice().getNumber());
		individual.setName(fBatchDetail.getFinance().getRegistry().getName() + " " + fBatchDetail.getFinance().getRegistry().getSurname());
		individual.setReferenceCode(fBatchDetail.getFinance().getRegistry().getId().toString());
		individual.setReturnCode(fBatchDetail.getFinance().getRegistry().getId().toString());
		RegistryAddress detailAddress = obtainRegistryAddress(fBatchDetail.getFinance().getRegistry().getId());
		individual.setAccountUserName(fBatchDetail.getFinance().getRegistry().getName() + " " + fBatchDetail.getFinance().getRegistry().getSurname());
		if(detailAddress != null){
			individual.setAccountUserAddress(detailAddress.getAddress() + " " + detailAddress.getAddress2() + " " + detailAddress.getAddress3());
			individual.setAccountUserAddress2(detailAddress.getCity());
			try {
				individual.setAccountUserPCode(new Integer(detailAddress.getZip()));
			} catch (NumberFormatException e) {
				individual.setAccountUserPCode(new Integer(0));
			}
		}
		if(lotType == Lot.EXTENDED){
			addExtendedData(individual, fBatchDetail.getFinance().getInvoice());
		}
		return individual;
	}
	
	@SuppressWarnings("unchecked")
	private void addExtendedData(Individual individual, Invoice invoice) throws ManagerBeanException {
		SimpleDateFormat format = new SimpleDateFormat();
		format.applyLocalizedPattern("dd/MM/yy");
		individual.addConcept("DEL " + format.format(invoice.getDate()));
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		InvoicePriceStrategy strategy = new InvoicePriceStrategy();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator iter = invoiceDetailBean.getList(criteria).iterator();
		for(int i = 0;i<5 && iter.hasNext();i++){
			InvoiceDetail detail = (InvoiceDetail)iter.next();
			String descConcept = new String();
			String priceConcep = new String();
			if(detail.getDescription().length() > 40){
				descConcept = detail.getDescription().substring(0,39);
				priceConcep = (detail.getDescription().length() > 48?detail.getDescription().substring(40,47):detail.getDescription().substring(40,detail.getDescription().length()));
			}else{
				descConcept = detail.getDescription();
			}
			individual.addConcept(descConcept);
			priceConcep = priceConcep + " " 
						  + detail.getQuantity() + " " 
						  + detail.getPrice() + " "
						  + detail.getItem().getProduct().getVat().getPercentage() + "% "
						  + strategy.getBasePrice(detail);
			individual.addConcept(priceConcep);
		}
		if(iter.hasNext()){
			double total = 0.0;
			while(iter.hasNext()){
				InvoiceDetail detail = (InvoiceDetail)iter.next();
				total += detail.getTaxableBase();
			}
			individual.addConcept("OTROS CONCEPTOS");
			individual.addConcept(StringUtils.repeat(" ", 30) + total);
		}
		Iterator taxIter = strategy.getTaxBreakDowns(invoice, invoice).iterator();
		for(int i=0;i<2;i++){
			if(taxIter.hasNext()){
				TaxBreakDown taxBreakDown = (TaxBreakDown)taxIter.next();
				if(taxBreakDown.getTaxType().equals(TaxType.VAT)){
					String taxConcept = "BASE:" + taxBreakDown.getBase();
					taxConcept = taxConcept + StringUtils.repeat(" ", 19 - taxConcept.length());
					taxConcept = taxConcept + taxBreakDown.getTaxPercent() + "% IVA" + ": " + taxBreakDown.getTaxQuota();
					individual.addConcept(taxConcept);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private RegistryAddress obtainRegistryAddress(Integer registryId) throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), registryId);
		Iterator iter = rAddressBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAddress)iter.next();
		}
		return null;
	}

}
