package com.code.aon.ui.finance.invoice.print;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class InvoiceDetailByDeliveryPrinter implements ICollectionProvider {
	
	private Integer id;
	
	public InvoiceDetailByDeliveryPrinter(Integer id) {
		this.id = id;
	}

	public Collection getCollection() {
		Session session = HibernateUtil.getSession();
        Query q = session.createQuery("SELECT " +
        		"new com.code.aon.ui.finance.invoice.print.InvoiceDetailByDeliveryCollectionProvider(invoiceDetail, delivery.series, delivery.number, delivery.issueTime) " +
        		"FROM InvoiceDetail invoiceDetail, DeliveryDetail deliveryDetail, Delivery delivery " + 
        		"WHERE invoiceDetail.sourceId = deliveryDetail.id " + 
        		"AND deliveryDetail.delivery.id = delivery.id " +
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
