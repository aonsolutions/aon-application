package com.code.aon.finance.invoicing;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.PayMethod;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.RegistryPayMethod;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;

public class FinanceGenerator {
	
	public void generateFinances(Invoice invoice, double totalPrice) throws ManagerBeanException{
		RegistryPayMethod rPayMethod = obtainRPayMethod(invoice.getRegistry());
		RegistryBank rBank = obtainRBank(invoice.getRegistry());
		Date date = invoice.getIssueDate();
		if(rPayMethod == null || rPayMethod.getNumberOfPayments() == 1){
			date = (rPayMethod == null?new Date():calculatePaymentDate(rPayMethod.getDaysToFirstPayment(),rPayMethod.getPaymentDaysArray(),date));
			insertFinance(invoice,date,(rPayMethod==null?null:rPayMethod.getPayment()),totalPrice,rBank);
		}else{
			double paymentPrice = round((totalPrice/rPayMethod.getNumberOfPayments()),2);
			date = calculatePaymentDate(rPayMethod.getDaysToFirstPayment(),rPayMethod.getPaymentDaysArray(),date);
			insertFinance(invoice,date,rPayMethod.getPayment(),paymentPrice,rBank);
			for(int i = 2;i <= rPayMethod.getNumberOfPayments() - 1;i++){
				date = calculatePaymentDate(rPayMethod.getDaysBetweenPayments(), rPayMethod.getPaymentDaysArray(), date);
				insertFinance(invoice,date,rPayMethod.getPayment(),paymentPrice,rBank);
				}
			paymentPrice = totalPrice - (paymentPrice * (rPayMethod.getNumberOfPayments() - 1));
			date = calculatePaymentDate(rPayMethod.getDaysBetweenPayments(), rPayMethod.getPaymentDaysArray(), date);
			insertFinance(invoice,date,rPayMethod.getPayment(),paymentPrice,rBank);
		}
	}

	private RegistryPayMethod obtainRPayMethod(Registry registry) throws ManagerBeanException {
		IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rPayMethodBean.getFieldName(IFinanceAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), registry.getId());
		Iterator iter = rPayMethodBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryPayMethod)iter.next();
		}
		return null;
	}

	private RegistryBank obtainRBank(Registry registry) throws ManagerBeanException {
		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
		Iterator iter = rBankBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryBank)iter.next();
		}
		return null;
	}
	
	private void insertFinance(Invoice invoice, Date date, PayMethod payMethod, double totalPrice, RegistryBank rBank) throws ManagerBeanException {
		Finance finance = new Finance();
		finance.setAmount(totalPrice);
		finance.setBank((rBank==null?null:rBank.getBank()));
		finance.setBankAccount((rBank==null?"":rBank.getBankAccount()));
		finance.setDueDate(date);
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setInvoice(invoice);
        finance.setConcept("Factura: " + invoice.getSeries() + ((invoice.getSeries()!=null&&!invoice.getSeries().equals(""))?"/":"") + invoice.getNumber());
		if(invoice.getType().equals(InvoiceType.SALES)){
			finance.setPayment(false);
		}else{
			finance.setPayment(true);
		}
		finance.setPayMethod(payMethod);
		finance.setRegistry(invoice.getRegistry());
		finance.setSecurityLevel(invoice.getSecurityLevel());
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			financeBean.insert(finance);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error inserting finance", e);
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
			calendar.add(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, paymentDaysArray[0]);
		}
		return calendar.getTime();
	}
	
	protected double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }
}