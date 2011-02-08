package com.code.ui.gbp.stats;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class SupplierTypeEvolutionReport implements ICollectionProvider {

	private String supplierType;
	private Date fromDate;
	private Date toDate;

	public String getSupplierType() {
		return supplierType;
	}

	public void setSupplierType(String supplierType) {
		this.supplierType = supplierType;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
		Session s = HibernateUtil.getSession();
		String subStmt = "(SELECT SUM(inv.amount) " +
			"FROM ProFormaInvoice inv " +
			"WHERE inv.supplier.supplierType.id = cs.supplier.supplierType.id " +
			"AND inv.campaign.id = cs.campaign.id)";
		String stmt = "SELECT " + "new com.code.ui.gbp.stats.SupplierTypeEvolution("
				+ "cs.supplier.supplierType.id,cs.supplier.supplierType.description,cs.campaign.code,cs.campaign.name,SUM(off.price)," + subStmt
				+ ") FROM CampaignSupplier cs,Offer off WHERE ";
		StringBuilder sentence = new StringBuilder(stmt);
		if (!StringUtils.isEmpty(getSupplierType())) {
			sentence.append("cs.supplier.supplierType.id = ");
			sentence.append(getSupplierType());
			sentence.append(" AND ");
		}
		sentence.append(" cs.campaign.startDate <= ? AND cs.campaign.endDate >= ?");
		sentence.append(" AND cs.campaign.id = off.campaign.id ");
		sentence.append(" AND cs.supplier.supplierType.id = off.supplier.supplierType.id ");
		sentence.append(" GROUP BY cs.supplier.supplierType.id,cs.supplier.supplierType.description,cs.campaign.name");

		Query query = s.createQuery(sentence.toString());
		query.setDate(0, getToDate());
		query.setDate(1, getFromDate());
		List<SupplierTypeEvolution> list = query.list();
		return list;

	}

	public void onInitialize(ActionEvent event) {
		Date from = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(from);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, 0);
		setFromDate(c.getTime());
		setToDate(new Date());
	}
	
	public static void main(String[] args) {
		SupplierTypeEvolutionReport ser = new SupplierTypeEvolutionReport();
		Date from = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(from);
		c.add(Calendar.YEAR, -1);
		ser.setFromDate(c.getTime());
		ser.setToDate(new Date());
		System.out.println(ser.getFromDate());
		System.out.println(ser.getToDate());
		ser.getCollection();
		System.out.println("->");		
	}
}
