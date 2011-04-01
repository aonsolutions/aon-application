package com.code.aon.finance.invoicing.finance;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IPayMethod;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;

public class FinanceGenerator {

	public Finance initializeFinanceData(Finance finance, double initialAmount) throws ManagerBeanException{
		if (finance.getInvoice() == null) {
			throw new IllegalArgumentException("El Vencimiento ha de tener Factura asociada.");
		}
		RegistryPayMethod rPayMethod = obtainRPayMethod(finance.getInvoice());
		return initializeFinanceData(finance,rPayMethod,initialAmount);
	}

	public Finance initializeFinanceData(Finance finance, RegistryPayMethod rPayMethod, double initialAmount){
		if (finance.getInvoice() == null) {
			throw new IllegalArgumentException("El Vencimiento ha de tener Factura asociada.");
		}
		RegistryBank rBank = null;
		if (rPayMethod != null) {
			rBank = rPayMethod.getRegistryBank();
			if (rBank != null) {
				finance.setBank(rBank.getBank());
				finance.setBankAccount( rBank.getBankAccount() );
			}
			finance.setPayMethod( rPayMethod.getPayment() );
		}
		finance.setDueDate(finance.getInvoice().getIssueDate());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setRegistry(finance.getInvoice().getRegistry());
		finance.setRegistryName(finance.getInvoice().getRegistryName());
		finance.setRegistryDocument(finance.getInvoice().getRegistryDocument());
		finance.setRegistryDocumentType(finance.getInvoice().getRegistryDocumentType());
		finance.setRegistryDocumentCountry(finance.getInvoice().getRegistryDocumentCountry());
		finance.setAmount(initialAmount);
		if(finance.getInvoice().getType().equals(InvoiceType.SALES)){
			finance.setPayment(false);
		}else{
			finance.setPayment(true);
		}
		return finance;
	}
	
	public List<Finance> generateFinances(Invoice invoice, IPayMethod payMethod, double totalPrice, boolean insert) throws ManagerBeanException{
		List<Finance> financeList = new LinkedList<Finance>();
		Date date = invoice.getIssueDate();
		if(payMethod == null || payMethod.getNumberOfPayments() == 1){
			date = (payMethod == null?date:calculatePaymentDate(payMethod.getDaysToFirstPayment(),payMethod.getPaymentDaysArray(),date));
			financeList.add(createFinance(invoice,date,(payMethod==null?null:payMethod.getPayment()),totalPrice,payMethod.getBank(),payMethod.getBankAccount()));
		}else{
			double paymentPrice = CommonUtil.round((totalPrice/payMethod.getNumberOfPayments()));
			date = calculatePaymentDate(payMethod.getDaysToFirstPayment(),payMethod.getPaymentDaysArray(),date);
			financeList.add(createFinance(invoice,date,payMethod.getPayment(),paymentPrice,payMethod.getBank(),payMethod.getBankAccount()));
			for(int i = 2;i <= payMethod.getNumberOfPayments() - 1;i++){
				date = calculatePaymentDate(payMethod.getDaysBetweenPayments(), payMethod.getPaymentDaysArray(), date);
				financeList.add(createFinance(invoice,date,payMethod.getPayment(),paymentPrice,payMethod.getBank(),payMethod.getBankAccount()));
			}
			paymentPrice = CommonUtil.round(totalPrice - (paymentPrice * (payMethod.getNumberOfPayments() - 1)));
			date = calculatePaymentDate(payMethod.getDaysBetweenPayments(), payMethod.getPaymentDaysArray(), date);
			financeList.add(createFinance(invoice,date,payMethod.getPayment(),paymentPrice,payMethod.getBank(),payMethod.getBankAccount()));
		}
		if(insert){
			insertFinances(financeList);
		}
		return financeList;
	}
	
	
	public List<Finance> generateFinances(Invoice invoice, double totalPrice, boolean insert) throws ManagerBeanException{
		List<Finance> financeList = new LinkedList<Finance>();
		RegistryPayMethod rPayMethod = obtainRPayMethod(invoice);
		RegistryBank rBank = rPayMethod==null?null:rPayMethod.getRegistryBank();
		Date date = invoice.getIssueDate();
		if(rPayMethod == null || rPayMethod.getNumberOfPayments() == 1){
			date = (rPayMethod == null?date:calculatePaymentDate(rPayMethod.getDaysToFirstPayment(),rPayMethod.getPaymentDaysArray(),date));
			financeList.add(createFinance(invoice,date,(rPayMethod==null?null:rPayMethod.getPayment()),totalPrice,rBank));
		}else{
			double paymentPrice = CommonUtil.round((totalPrice/rPayMethod.getNumberOfPayments()));
			date = calculatePaymentDate(rPayMethod.getDaysToFirstPayment(),rPayMethod.getPaymentDaysArray(),date);
			financeList.add(createFinance(invoice,date,rPayMethod.getPayment(),paymentPrice,rBank));
			for(int i = 2;i <= rPayMethod.getNumberOfPayments() - 1;i++){
				date = calculatePaymentDate(rPayMethod.getDaysBetweenPayments(), rPayMethod.getPaymentDaysArray(), date);
				financeList.add(createFinance(invoice,date,rPayMethod.getPayment(),paymentPrice,rBank));
			}
			paymentPrice = CommonUtil.round(totalPrice - (paymentPrice * (rPayMethod.getNumberOfPayments() - 1)));
			date = calculatePaymentDate(rPayMethod.getDaysBetweenPayments(), rPayMethod.getPaymentDaysArray(), date);
			financeList.add(createFinance(invoice,date,rPayMethod.getPayment(),paymentPrice,rBank));
		}
		if(insert){
			insertFinances(financeList);
		}
		return financeList;
	}
	
	public List<Finance> generateFinances(Invoice invoice, double totalPrice) throws ManagerBeanException{
		return generateFinances(invoice, totalPrice, true);
	}

	@SuppressWarnings("unchecked")
	private RegistryPayMethod obtainRPayMethod(Invoice invoice) throws ManagerBeanException {
		IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rPayMethodBean.getFieldName(IRegistryAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), invoice.getRegistry().getId());
		Iterator iter = rPayMethodBean.getList(criteria).iterator();
		if (iter != null) {
			if(iter.hasNext()){
				return (RegistryPayMethod)iter.next();
			}
		}
		if (invoice.getType() != InvoiceType.SALES) {
			return obtainCompanyPayMethod(); 
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private RegistryPayMethod obtainCompanyPayMethod() throws ManagerBeanException {
		IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
		Company company = (Company) companyBean.getList(null).iterator().next();
		IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rPayMethodBean.getFieldName(IRegistryAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), company.getId());
		Iterator iter = rPayMethodBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryPayMethod)iter.next();
		}
		return null;
	}

	private Finance createFinance(Invoice invoice, Date date, PayMethod payMethod, double totalPrice, Bank bank, BankAccount bankAccount){
		Finance finance = new Finance();
		finance.setAmount(totalPrice);
		finance.setBank(bank);
		finance.setBankAccount(bankAccount);
		finance.setConcept(invoice.getDocumentNumber());
		finance.setDueDate(date);
		finance.setInvoice(invoice);
		finance.setFinanceStatus(FinanceStatus.PENDING);
		if(invoice.getType().equals(InvoiceType.SALES)){
			finance.setPayment(false);
		}else{
			finance.setPayment(true);
		}
		finance.setPayMethod(payMethod);
		finance.setRegistry(invoice.getRegistry());
		finance.setRegistryName(invoice.getRegistryName());
		finance.setRegistryDocument(invoice.getRegistryDocument());
		finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
		finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
		finance.setSecurityLevel(invoice.getSecurityLevel());
		finance.setScope(invoice.getScope());
		return finance;
	}
	
	private Finance createFinance(Invoice invoice, Date date, PayMethod payMethod, double totalPrice, RegistryBank rBank){
		Bank bank = (rBank==null?null:rBank.getBank());
		BankAccount bankAccount = (rBank==null?null:rBank.getBankAccount());
		return createFinance(invoice,date,payMethod,totalPrice, bank, bankAccount);
	}

	@SuppressWarnings("unchecked")
	private void insertFinances(List<Finance> financeList) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Iterator iter = financeList.iterator();
		while(iter.hasNext()){
			Finance finance = (Finance)iter.next();
			financeBean.insert(finance);
		}
	}
	
	private Date calculatePaymentDate(int daysNumber, int[] paymentDaysArray, Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, daysNumber);
		if(paymentDaysArray.length > 0){
			for(int i = 0;i<paymentDaysArray.length;i++){
				int days = CommonUtil.daysInMonth( date );
				int day = paymentDaysArray[i]>days?days:paymentDaysArray[i];
				if(calendar.get(Calendar.DAY_OF_MONTH) <= day){
					calendar.set(Calendar.DAY_OF_MONTH, day);
					return calendar.getTime();
				}
			}
			if (paymentDaysArray[0] != 0) {
				calendar.add(Calendar.MONTH, 1);
				calendar.set(Calendar.DAY_OF_MONTH, paymentDaysArray[0]);
			}
		}
		return calendar.getTime();
	}

	public Finance duplicateFinance(Finance finance, double newAmount) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Finance newFinance = new Finance();
		newFinance.setPayment(finance.isPayment());
		newFinance.setRegistry(finance.getRegistry());
		newFinance.setRegistryName(finance.getRegistryName());
		newFinance.setRegistryDocument(finance.getRegistryDocument());
		newFinance.setRegistryDocumentType(finance.getRegistryDocumentType());
		newFinance.setRegistryDocumentCountry(finance.getRegistryDocumentCountry());
		newFinance.setAmount(newAmount);
		newFinance.setExpenses(0.0);
		newFinance.setConcept(finance.getConcept());
		newFinance.setInvoice(finance.getInvoice());
		newFinance.setDueDate(finance.getDueDate());
		newFinance.setPayMethod(finance.getPayMethod());
		newFinance.setBank(finance.getBank());
		newFinance.setBankAccount(finance.getBankAccount());
		newFinance.setFinanceStatus(FinanceStatus.PENDING);
		newFinance.setSecurityLevel(finance.getSecurityLevel());
		newFinance.setScope(finance.getScope());
		return (Finance)financeBean.insert(newFinance);
	}

}