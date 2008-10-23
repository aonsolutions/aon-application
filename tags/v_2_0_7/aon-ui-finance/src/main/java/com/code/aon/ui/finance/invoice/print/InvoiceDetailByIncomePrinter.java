package com.code.aon.ui.finance.invoice.print;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class InvoiceDetailByIncomePrinter implements ICollectionProvider {
	
	private Integer id;
	
	public InvoiceDetailByIncomePrinter(Integer id) {
		this.id = id;
	}

	public Collection getCollection() {
		Session session = HibernateUtil.getSession();
        Query q = session.createQuery("SELECT " +
        		"new com.code.aon.ui.finance.invoice.print.InvoiceDetailByDeliveryCollectionProvider(invoiceDetail, income.series, income.number, income.issueTime) " +
        		"FROM InvoiceDetail invoiceDetail, IncomeDetail incomeDetail, Income income " + 
        		"WHERE invoiceDetail.deliveryDetail = incomeDetail.id " + 
        		"AND incomeDetail.income.id = income.id " +
        		"AND invoiceDetail.invoice.id = " + id
                );
        List results = q.list();
        TreeSet<InvoiceDetailByDeliveryCollectionProvider> treeSet = new TreeSet<InvoiceDetailByDeliveryCollectionProvider>(results);
		return treeSet;
	}
	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
}
