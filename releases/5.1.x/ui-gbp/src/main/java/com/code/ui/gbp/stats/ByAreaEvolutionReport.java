package com.code.ui.gbp.stats;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class ByAreaEvolutionReport implements ICollectionProvider {

	private Date fromDate;
	private Date toDate;
	
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
			"WHERE inv.campaign.id = c.id)";
		String subStmt_2 = "(SELECT COUNT(*) " +
			"FROM Campaign campaign " +
			"WHERE campaign.area = c.area)";
		String stmt = "SELECT " + "new com.code.ui.gbp.stats.ByAreaEvolution("
				+ "c.area.description,c.area.areaGroup.description,c.area.budget,c.area.estimate," + subStmt_2 + ",c.area.budget,SUM(off.price)," + subStmt
				+ ") FROM Campaign c,Offer off WHERE ";
		StringBuilder sentence = new StringBuilder(stmt);
		//sentence.append(" c.startDate <= ? AND c.endDate >= ?");
		sentence.append(" off.offerDate <= ? AND off.offerDate >= ?");
		sentence.append(" AND c.id = off.campaign.id ");
		sentence.append(" AND c.area.status = 0 ");
		sentence.append(" GROUP BY c.area");

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
		ByAreaEvolutionReport c = new ByAreaEvolutionReport();
		c.onInitialize(null);
		c.getCollection();
		System.out.println("END");
	}
}
