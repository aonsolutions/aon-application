package com.code.aon.ui.finance.util.print;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.print.CheckingTo;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.util.AonUtil;


public class FinanceCheckingPrinter implements ICollectionProvider, IFinanceConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
		String select = "SELECT finance " +
						"FROM Finance finance, Customer customer " +
						" WHERE " + DomainManager.getSQLWhereClause("finance.domain") +				
						" AND finance.registry.id = customer.registry.id " +
						" AND finance.payment = 0 " +
						" AND finance.payMethod.id IS NULL " +
						obtainPrintCondition() +
						" ORDER BY finance.dueDate, finance.concept";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	private List<ITransferObject> obtainNegotiableNoBankAccountList() {
		String select = "SELECT finance " +
						"FROM Finance finance, Customer customer " +
						" WHERE " + DomainManager.getSQLWhereClause("finance.domain") +
						" AND finance.registry.id = customer.registry.id " +
						" AND finance.payment = 0 " +
						" AND finance.payMethod.type = " + PayMethodType.NEGOTIABLE_DOCUMENT.ordinal() + " " +
						" AND (finance.bankAccount IS NULL OR finance.bankAccount = '') " +
						obtainPrintCondition() +
						" ORDER BY finance.dueDate, finance.concept";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	private List<ITransferObject> obtainNoNegotiableBankAccountList() {
		String select = "SELECT finance " +
						"FROM Finance finance, Customer customer " +
						" WHERE " + DomainManager.getSQLWhereClause("finance.domain") +
						" AND finance.registry.id = customer.registry.id " +
						" AND finance.payment = 0 " +
						" AND finance.payMethod.type <> " + PayMethodType.NEGOTIABLE_DOCUMENT.ordinal() + " " +
						" AND finance.bankAccount IS NOT NULL " +
						" AND finance.bankAccount <> '' " +
						obtainPrintCondition() +
						" ORDER BY finance.dueDate, finance.concept";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		return query.list();
	}

	private String obtainPrintCondition() { 
		FinancePrinter printer = (FinancePrinter) AonUtil.getRegisteredBean(FINANCE_PRINTER_CONTROLLER);
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