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
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.form.FormUtil;

public class FeeCheckingPrinter implements ICollectionProvider, IFinanceConstants {
	
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

	@SuppressWarnings("unchecked")
	private List<ITransferObject> obtainNoPaymethodList() {
		String select = "SELECT customer " +
						"FROM Customer customer " +
						"WHERE customer.id NOT IN(" +
							"SELECT rPayMethod.registry.id " +
							"FROM RegistryPayMethod rPayMethod) " +
						obtainPrintCondition() +
						"ORDER BY customer.registry.surname, customer.registry.name";
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery(select);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	private List<ITransferObject> obtainNegotiableNoBankAccountList() {
		String select = "SELECT customer " +
						"FROM Customer customer, RegistryPayMethod rPayMethod " +
						"WHERE customer.id = rPayMethod.registry.id " +
						"AND rPayMethod.payment.type = " + PayMethodType.NEGOTIABLE_DOCUMENT.ordinal() + " " + 
						"AND customer.id NOT IN(" +
							"SELECT rBank.registry.id " +
							"FROM RegistryBank rBank) " +
						obtainPrintCondition() +
						"ORDER BY customer.registry.surname, customer.registry.name";
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery(select);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	private List<ITransferObject> obtainNoNegotiableBankAccountList() {
		String select = "SELECT customer " +
						"FROM Customer customer, RegistryBank rBank, RegistryPayMethod rPayMethod " +
						"WHERE customer.id = rBank.registry.id " +
						"AND customer.id = rPayMethod.registry.id " +
						"AND rPayMethod.payment.type <> " + PayMethodType.NEGOTIABLE_DOCUMENT.ordinal() + " " +
						obtainPrintCondition() +
						"ORDER BY customer.registry.surname, customer.registry.name";
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
		return condition;
	}
}