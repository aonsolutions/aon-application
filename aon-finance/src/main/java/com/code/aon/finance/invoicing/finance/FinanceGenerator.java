package com.code.aon.finance.invoicing.finance;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
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
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceGenerator implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public Finance initializeFinanceData(Finance finance, double initialAmount) throws ManagerBeanException {
		if (finance.getInvoice() == null) {
			throw new IllegalArgumentException("El Vencimiento ha de tener Factura asociada.");
		}
		RegistryPayMethod rPayMethod = obtainRPayMethod(finance.getInvoice());
		return initializeFinanceData(finance, rPayMethod, initialAmount);
	}

	public Finance initializeFinanceData(Finance finance, RegistryPayMethod rPayMethod, double initialAmount) {
		if (finance.getInvoice() == null) {
			throw new IllegalArgumentException("El Vencimiento ha de tener Factura asociada.");
		}
		finance.setPayment(!finance.getInvoice().getType().equals(InvoiceType.SALES));
		finance.setRegistry(finance.getInvoice().getRegistry());
		finance.setRegistryName(finance.getInvoice().getRegistryName());
		finance.setRegistryDocument(finance.getInvoice().getRegistryDocument());
		finance.setRegistryDocumentType(finance.getInvoice().getRegistryDocumentType());
		finance.setRegistryDocumentCountry(finance.getInvoice().getRegistryDocumentCountry());
		finance.setAmount(initialAmount);
		finance.setDueDate(finance.getInvoice().getIssueDate());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		RegistryBank rBank = null;
		if (rPayMethod != null) {
			rBank = rPayMethod.getRegistryBank();
			if (rBank != null) {
				finance.setBankAccount(rBank.getBankAccount());
				finance.setBankAlias(rBank.getBankAlias());
				finance.setBic(rBank.getBic());
			}
			finance.setPayMethod(rPayMethod.getPayment());
		}
		return finance;
	}
	
	public List<Finance> generateFinances(Invoice invoice, double totalPrice) throws ManagerBeanException{
		return generateFinances(invoice, totalPrice, true);
	}

	public List<Finance> generateFinances(Invoice invoice, double totalPrice, boolean insert) throws ManagerBeanException{
		List<Finance> financeList = new LinkedList<Finance>();
		RegistryPayMethod rPayMethod = obtainRPayMethod(invoice);
		RegistryBank rBank = (rPayMethod==null) ? null : rPayMethod.getRegistryBank();
		Date date = invoice.getIssueDate();
		if ((rPayMethod == null) || (rPayMethod.getNumberOfPayments() == 1)) {
			date = (rPayMethod==null) ? date : calculatePaymentDate(rPayMethod.getDaysToFirstPayment(), rPayMethod.getPaymentDaysArray(), date);
			financeList.add(createFinance(invoice, date, (rPayMethod==null) ? null : rPayMethod.getPayment(), rBank, totalPrice));
		} else {
			double paymentPrice = CommonUtil.round((totalPrice / rPayMethod.getNumberOfPayments()));
			date = calculatePaymentDate(rPayMethod.getDaysToFirstPayment(), rPayMethod.getPaymentDaysArray(), date);
			financeList.add(createFinance(invoice, date, rPayMethod.getPayment(), rBank, paymentPrice));
			for(int i=2; i<=rPayMethod.getNumberOfPayments()-1; i++) {
				date = calculatePaymentDate(rPayMethod.getDaysBetweenPayments(), rPayMethod.getPaymentDaysArray(), date);
				financeList.add(createFinance(invoice, date, rPayMethod.getPayment(), rBank, paymentPrice));
			}
			paymentPrice = CommonUtil.round(totalPrice - (paymentPrice * (rPayMethod.getNumberOfPayments() - 1)));
			date = calculatePaymentDate(rPayMethod.getDaysBetweenPayments(), rPayMethod.getPaymentDaysArray(), date);
			financeList.add(createFinance(invoice, date, rPayMethod.getPayment(), rBank, paymentPrice));
		}
		if (insert) {
			insertFinances(financeList);
		}
		return financeList;
	}
	
	public List<Finance> generateFinances(Invoice invoice, IPayMethod payMethod, double totalPrice, boolean manual, boolean insert) throws ManagerBeanException{
		List<Finance> financeList = new LinkedList<Finance>();
		Date date = invoice.getIssueDate();
		if ((payMethod == null) || (payMethod.getNumberOfPayments() == 1)) {
			date = (payMethod == null) ? date : calculatePaymentDate(payMethod.getDaysToFirstPayment(), payMethod.getPaymentDaysArray(), date);
			financeList.add(createFinance(invoice, date, payMethod, totalPrice, manual));
		} else {
			double paymentPrice = CommonUtil.round((totalPrice / payMethod.getNumberOfPayments()));
			date = calculatePaymentDate(payMethod.getDaysToFirstPayment(), payMethod.getPaymentDaysArray(), date);
			financeList.add(createFinance(invoice, date, payMethod, paymentPrice, manual));
			for(int i=2; i<=payMethod.getNumberOfPayments()-1; i++) {
				date = calculatePaymentDate(payMethod.getDaysBetweenPayments(), payMethod.getPaymentDaysArray(), date);
				financeList.add(createFinance(invoice, date, payMethod, paymentPrice, manual));
			}
			paymentPrice = CommonUtil.round(totalPrice - (paymentPrice * (payMethod.getNumberOfPayments() - 1)));
			date = calculatePaymentDate(payMethod.getDaysBetweenPayments(), payMethod.getPaymentDaysArray(), date);
			financeList.add(createFinance(invoice, date, payMethod, paymentPrice, manual));
		}
		if (insert) {
			insertFinances(financeList);
		}
		return financeList;
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
		newFinance.setBankAccount(finance.getBankAccount());
		newFinance.setBankAlias(finance.getBankAlias());
		newFinance.setBic(finance.getBic());
		newFinance.setFinanceStatus(FinanceStatus.PENDING);
		newFinance.setRemarks(null);
		newFinance.setSecurityLevel(finance.getSecurityLevel());
		newFinance.setScope(finance.getScope());
		newFinance.setManual(finance.isManual());
		newFinance.setAdvance(finance.isAdvance());
		newFinance.setPayroll(finance.isPayroll());
		newFinance.setPrepayment(finance.isPrepayment());
		return (Finance)financeBean.insert(newFinance);
	}

	private RegistryPayMethod obtainRPayMethod(Invoice invoice) throws ManagerBeanException {
		IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rPayMethodBean.getFieldName(IEntityAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), invoice.getRegistry().getId());
		for (ITransferObject ito : rPayMethodBean.getList(criteria)) {
			return (RegistryPayMethod)ito;
		}
		if (invoice.getType() != InvoiceType.SALES) {
			return obtainCompanyPayMethod(); 
		}
		return null;
	}

	private RegistryPayMethod obtainCompanyPayMethod() throws ManagerBeanException {
		IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
		Company company = (Company)companyBean.getList(null).get(0);
		IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rPayMethodBean.getFieldName(IEntityAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), company.getId());
		for (ITransferObject ito : rPayMethodBean.getList(criteria)) {
			return (RegistryPayMethod)ito;
		}
		return null;
	}

	private Finance createFinance(Invoice invoice, Date date, PayMethod payMethod, RegistryBank rBank, double totalPrice) {
		BankAccount bankAccount = (rBank==null) ? null : rBank.getBankAccount();
		String bankAlias = (rBank==null) ? null : rBank.getBankAlias();
		String bic = (rBank==null) ? null : rBank.getBic();
		return createFinance(invoice, date, payMethod, totalPrice, bankAccount, bankAlias, bic);
	}

	private Finance createFinance(Invoice invoice, Date date, IPayMethod pMethod, double totalPrice, boolean manual) {
		PayMethod payMethod = (pMethod==null) ? null : pMethod.getPayment();
		BankAccount bankAccount = (pMethod==null) ? null : pMethod.getBankAccount();
		String bankAlias = (pMethod==null) ? null : pMethod.getBankAlias();
		String bic = (pMethod==null) ? null : pMethod.getBic();
		Finance finance = createFinance(invoice, date, payMethod, totalPrice, bankAccount, bankAlias, bic);
		finance.setManual(manual);
		return finance;
	}

	public Finance createFinance(Invoice invoice, Date date, PayMethod payMethod, double totalPrice, BankAccount bankAccount, String bankAlias, String bic) {
		Finance finance = new Finance();
		finance.setPayment(!invoice.getType().equals(InvoiceType.SALES));
		finance.setRegistry(invoice.getRegistry());
		finance.setRegistryName(invoice.getRegistryName());
		finance.setRegistryDocument(invoice.getRegistryDocument());
		finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
		finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
		finance.setAmount(totalPrice);
		finance.setConcept(invoice.getDocumentNumber());
		finance.setInvoice(invoice);
		finance.setDueDate(date);
		finance.setPayMethod(payMethod);
		finance.setBankAccount(bankAccount);
		finance.setBankAlias(bankAlias);
		finance.setBic(bic);
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setSecurityLevel(invoice.getSecurityLevel());
		finance.setScope(invoice.getScope());
		return finance;
	}

	private void insertFinances(List<Finance> financeList) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		for (Finance finance : financeList) {
			financeBean.insert(finance);
		}
	}
	
	private Date calculatePaymentDate(int daysNumber, int[] paymentDaysArray, Date date) {
		Date paymentDate = DateUtils.addDays(date, daysNumber);
		if (paymentDaysArray.length > 0) {
			for (int i=0; i<paymentDaysArray.length; i++) {
				int daysInMonth = CommonUtil.daysInMonth(paymentDate);
				int day = (paymentDaysArray[i]>daysInMonth) ? daysInMonth : paymentDaysArray[i];
				if (DateUtils.getFragmentInDays(paymentDate, Calendar.MONTH) <= day) {
					return DateUtils.setDays(paymentDate, day);
				}
			}
			if (paymentDaysArray[0] != 0) {
				paymentDate = DateUtils.addMonths(paymentDate, 1);
				int daysInMonth = CommonUtil.daysInMonth(paymentDate);
				int day = (paymentDaysArray[0]>daysInMonth) ? daysInMonth : paymentDaysArray[0];
				return DateUtils.setDays(paymentDate, day);
			}
		}
		return paymentDate;
	}

}