package com.code.aon.finance.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.Finance;

public class FinanceBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Finance finance = (Finance)evt.getTo();
		if(finance.getAmount() == 0){
			throw new ManagerBeanVetoListenerException("Importe no puede ser 0.0");
		}
		if (StringUtils.isEmpty(finance.getRegistryName())) {
			finance.setRegistryName((!finance.isEmptyInvoice()) ? finance.getInvoice().getRegistryName() : finance.getRegistry().getFullName());
		}
		if (StringUtils.isEmpty(finance.getRegistryDocument())) {
			finance.setRegistryDocument((!finance.isEmptyInvoice()) ? finance.getInvoice().getRegistryDocument() : finance.getRegistry().getDocument());
		}
		if (StringUtils.isEmpty(finance.getConcept()) && !finance.isEmptyInvoice()) {
	        finance.setConcept(finance.getInvoice().getDocumentNumber()); 
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Finance finance = (Finance)evt.getTo();
		if(finance.getAmount() == 0){
			throw new ManagerBeanVetoListenerException("Importe no puede ser 0.0");
		}
		if (StringUtils.isEmpty(finance.getRegistryName())) {
			finance.setRegistryName((!finance.isEmptyInvoice()) ? finance.getInvoice().getRegistryName() : finance.getRegistry().getFullName());
		}
		if (StringUtils.isEmpty(finance.getRegistryDocument())) {
			finance.setRegistryDocument((!finance.isEmptyInvoice()) ? finance.getInvoice().getRegistryDocument() : finance.getRegistry().getDocument());
		}
		if (StringUtils.isEmpty(finance.getConcept()) && !finance.isEmptyInvoice()) {
	        finance.setConcept(finance.getInvoice().getDocumentNumber()); 
		}
	}
}