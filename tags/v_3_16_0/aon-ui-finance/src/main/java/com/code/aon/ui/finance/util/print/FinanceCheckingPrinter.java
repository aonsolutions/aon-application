package com.code.aon.ui.finance.util.print;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.print.CheckingTo;
import com.code.aon.ui.form.FormUtil;


public class FinanceCheckingPrinter implements ICollectionProvider {

	private static final String FINANCE_PRINTER_CONTROLLER = "financePrint";

	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		CheckingTo checkingTo = new CheckingTo();
		checkingTo.setNoPaymethodList(obtainNoPaymethodList());
		checkingTo.setNegotiableNoBankAccountList(obtainNegotiableNoBankAccountList());
		checkingTo.setNoNegotiableBankAccountList(obtainNoNegotiableBankAccountList());
		List list = new ArrayList();
		list.add(checkingTo);
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
	@SuppressWarnings({"unchecked", "unused"})
	private List<ITransferObject> obtainNoPaymethodList() {
		String select = "SELECT finance " +
						"FROM Finance finance, Customer customer " +
						"WHERE finance.registry.id = customer.registry.id " +
						"AND finance.payment = 0 " +
						"AND finance.payMethod.id IS NULL " +
						obtainPrintCondition() +
						"ORDER BY finance.registry.surname, finance.registry.name";
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery(select);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	private List<ITransferObject> obtainNegotiableNoBankAccountList() {
		String select = "SELECT finance " +
						"FROM Finance finance, Customer customer " +
						"WHERE finance.registry.id = customer.registry.id " +
						"AND finance.payment = 0 " +
						"AND finance.payMethod.type = " + PayMethodType.NEGOTIABLE_DOCUMENT.ordinal() + " " +
						"AND (finance.bankAccount IS NULL OR finance.bankAccount = '') " +
						obtainPrintCondition() +
						"ORDER BY finance.registry.surname, finance.registry.name";
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery(select);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	private List<ITransferObject> obtainNoNegotiableBankAccountList() {
		String select = "SELECT finance " +
						"FROM Finance finance, Customer customer " +
						"WHERE finance.registry.id = customer.registry.id " +
						"AND finance.payment = 0 " +
						"AND finance.payMethod.type <> " + PayMethodType.NEGOTIABLE_DOCUMENT.ordinal() + " " +
						"AND finance.bankAccount IS NOT NULL " +
						"AND finance.bankAccount <> '' " +
						obtainPrintCondition() +
						"ORDER BY finance.registry.surname, finance.registry.name";
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery(select);
		return query.list();
	}

	private String obtainPrintCondition() { 
		FinancePrinter printer = (FinancePrinter)FormUtil.getController(FINANCE_PRINTER_CONTROLLER);
		String condition = "";
		if (printer.getCustomerStatus() != null) {
			condition += "AND customer.status = " + printer.getCustomerStatus().ordinal() + " ";
		}
		if (printer.getFinanceStatus() != null) {
			condition += "AND finance.financeStatus = " + printer.getFinanceStatus().ordinal() + " ";
		}
		return condition;
	}
}