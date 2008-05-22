package com.code.ui.gbp.stats;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class SupplierEvolutionReport implements ICollectionProvider {

	private String supplier;
	private Date fromDate;
	private Date toDate;

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
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
		String subStmt = "(SELECT SUM(inv.amount) FROM ProFormaInvoice inv WHERE inv.supplier.id = cs.supplier.id AND inv.campaign.id = cs.campaign.id)";
		String stmt = "SELECT " + "new com.code.ui.gbp.stats.SupplierEvolution("
				+ "cs.supplier.id,cs.supplier.name,cs.campaign.name,SUM(off.price)," + subStmt
				+ ") FROM CampaignSupplier cs,Offer off WHERE ";
		StringBuilder sentence = new StringBuilder(stmt);
		if (!StringUtils.isEmpty(getSupplier())) {
			sentence.append("cs.supplier.id = ");
			sentence.append(getSupplier());
			sentence.append(" AND ");
		}
		sentence.append(" cs.campaign.startDate <= ? AND cs.campaign.endDate >= ?");
		sentence.append(" AND cs.campaign.id = off.campaign.id ");
		sentence.append(" AND cs.supplier.id = off.supplier.id ");
		sentence.append(" GROUP BY cs.supplier.id,cs.supplier.name,cs.campaign.name");

		Query query = s.createQuery(sentence.toString());
		query.setDate(0, getToDate());
		query.setDate(1, getFromDate());
		List<SupplierEvolution> list = query.list();
		return list;

	}

	public static void main(String[] args) {
		SupplierEvolutionReport ser = new SupplierEvolutionReport();
		Date from = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(from);
		c.add(Calendar.YEAR, -1);
		ser.setFromDate(c.getTime());
		ser.setToDate(new Date());
		System.out.println(ser.getFromDate());
		System.out.println(ser.getToDate());
		ser.getCollection();
	}
}
