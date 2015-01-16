package com.code.aon.ui.finance.event;

import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.finance.controller.InvoiceFinanceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InvoiceFinanceControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		InvoiceFinanceController financeController = (InvoiceFinanceController)event.getController();
		InvoiceController invoiceController = (InvoiceController)financeController.getMasterController();
		Invoice invoice = (Invoice)invoiceController.getTo();

		Finance finance = (Finance)financeController.getTo();
		finance.setPayment((InvoiceType.SALES == invoice.getType()) ? false : true);
		finance.setInvoice(invoice);
		finance.setRegistry(invoice.getRegistry());
		finance.setRegistryName(invoice.getRegistryName());
		finance.setRegistryDocument(invoice.getRegistryDocument());
		finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
		finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
        finance.setConcept(invoice.getDocumentNumber()); 
		finance.setManual(true);
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setSecurityLevel(invoice.getSecurityLevel());
		try {
			finance.setAmount(invoiceController.getPendingAmount());

			RegistryPayMethod rPayMethod = finance.getRegistry().getPayMethod();
			finance.setPayMethod((rPayMethod==null) ? new PayMethod() : rPayMethod.getPayment());
			finance.setBankAccount((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? new BankAccount() : rPayMethod.getBankAccount());
			finance.setBankAlias((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? null : rPayMethod.getBankAlias());
			finance.setBic((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? null : rPayMethod.getBic());

			financeController.setRegistryBank((rPayMethod==null) ? null : rPayMethod.getRegistryBank());
			financeController.setShowBankManualInput(false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		InvoiceFinanceController financeController = (InvoiceFinanceController)event.getController();
		Finance finance = (Finance)financeController.getTo();
		try {
			financeController.setRegistryBank(null);
			if (StringUtils.isNotEmpty(finance.getBankAccount().getIban())) {
				for (SelectItem selectItem : financeController.getAllBanks()) {
					RegistryBank rBank = (RegistryBank)selectItem.getValue();
					if (finance.getBankAccount().getIban().equals(rBank.getBankAccount().getIban())) {
						financeController.setRegistryBank(rBank);
						break;
					}
				}
			}
			financeController.setShowBankManualInput(financeController.getRegistryBank() == null && StringUtils.isNotEmpty(finance.getBankAccount().getIban()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Finance finance = (Finance)event.getController().getTo();
		if (!finance.isManual()) {
			Date dueDate = finance.getDueDate();
			Integer payMethod = (finance.getPayMethod() != null) ? finance.getPayMethod().getId() : null;
			String bankAccount = (finance.getBankAccount() != null) ? finance.getBankAccount().getIban() : null;
			Double amount = finance.getAmount();

			String select = "SELECT finance.due_date dueDate, finance.pay_method payMethod, " +
	    						"finance.bank_account bankAccount, finance.amount amount " +
	    						"FROM finance as finance " +
	    						"WHERE finance.id = " + finance.getId();
			Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
			SQLQuery query = session.createSQLQuery(select);
	        List<?> list = query.addScalar("dueDate", Hibernate.DATE).addScalar("payMethod", Hibernate.INTEGER).
	        				addScalar("bankAccount", Hibernate.STRING).addScalar("amount", Hibernate.DOUBLE).list();
	        if (!list.isEmpty()) {
	        	Object[] obj = (Object[])list.get(0);
	            Date savedDueDate = (Date)obj[0];
	            Integer savedPayMethod = (Integer)obj[1];
	            String savedBankAccount = (String)obj[2];
	            Double savedAmount = (Double)obj[3];
	            if (!ObjectUtils.equals(savedDueDate, dueDate) || !ObjectUtils.equals(savedPayMethod, payMethod) ||
	            		!ObjectUtils.equals(savedBankAccount, bankAccount) || !ObjectUtils.equals(savedAmount, amount)) {
	            	finance.setManual(true);
	            }
	        }
		}
	}

}
