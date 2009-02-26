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

	public Finance initializeFinanceData(Finance finance) throws ManagerBeanException{
		RegistryPayMethod rPayMethod = obtainRPayMethod(finance.getInvoice());
		return initializeFinanceData(finance,rPayMethod);
	}

	public Finance initializeFinanceData(Finance finance, RegistryPayMethod rPayMethod){
		if (rPayMethod != null) {
			RegistryBank rBank = rPayMethod.getRegistryBank();
			if (rBank != null) {
				finance.setBank(rBank.getBank());
				finance.setBankAccount( rBank.getBankAccount() );
			}
			finance.setPayMethod( rPayMethod.getPayment() );
		}
		return finance;
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
			double paymentPrice = CommonUtil.round((totalPrice/rPayMethod.getNumberOfPayments()),2);
			date = calculatePaymentDate(rPayMethod.getDaysToFirstPayment(),rPayMethod.getPaymentDaysArray(),date);
			financeList.add(createFinance(invoice,date,rPayMethod.getPayment(),paymentPrice,rBank));
			for(int i = 2;i <= rPayMethod.getNumberOfPayments() - 1;i++){
				date = calculatePaymentDate(rPayMethod.getDaysBetweenPayments(), rPayMethod.getPaymentDaysArray(), date);
				financeList.add(createFinance(invoice,date,rPayMethod.getPayment(),paymentPrice,rBank));
				}
			paymentPrice = CommonUtil.round(totalPrice - (paymentPrice * (rPayMethod.getNumberOfPayments() - 1)), 2);
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

	private Finance createFinance(Invoice invoice, Date date, PayMethod payMethod, double totalPrice, RegistryBank rBank){
		Finance finance = new Finance();
		finance.setAmount(totalPrice);
		finance.setBank((rBank==null?null:rBank.getBank()));
		finance.setBankAccount((rBank==null?null:rBank.getBankAccount()));
		finance.setDueDate(date);
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setInvoice(invoice);
		if(invoice.getType().equals(InvoiceType.SALES)){
			finance.setPayment(false);
		}else{
			finance.setPayment(true);
		}
		finance.setPayMethod(payMethod);
		finance.setRegistry(invoice.getRegistry());
		finance.setSecurityLevel(invoice.getSecurityLevel());
		return finance;
	}
	
	@SuppressWarnings("unchecked")
	private void insertFinances(List<Finance> financeList) throws ManagerBeanException {
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Iterator iter = financeList.iterator();
			while(iter.hasNext()){
				Finance finance = (Finance)iter.next();
				financeBean.insert(finance);
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error inserting finances", e);
		}
	}
	
	private Date calculatePaymentDate(int daysNumber, int[] paymentDaysArray, Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, daysNumber);
		if(paymentDaysArray.length > 0){
			for(int i = 0;i<paymentDaysArray.length;i++){
				if(calendar.get(Calendar.DAY_OF_MONTH) <= paymentDaysArray[i]){
					calendar.set(Calendar.DAY_OF_MONTH, paymentDaysArray[i]);
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

}