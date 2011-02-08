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
import com.code.gbp.Area;

public class OfferControlReport implements ICollectionProvider {

	private Date fromDate;
	private Date toDate;
	private Area area;
	
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
	
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
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
		String subStmt_Inv = "(SELECT SUM(inv.amount) FROM ProFormaInvoice inv WHERE inv.campaign.id = c.id)";
		String subStmt_Off = "(SELECT SUM(off.price) FROM Offer off WHERE off.campaign.id = c.id)";
		String stmt = "SELECT " + "new com.code.ui.gbp.stats.OfferControl("
				+ "c.code,c.name,c.status,"+subStmt_Off+"," + subStmt_Inv 
				+ ") FROM Campaign c WHERE ";
		StringBuilder sentence = new StringBuilder(stmt);
		sentence.append(" c.startDate <= ? AND c.endDate >= ?");
		if (getArea()!=null && getArea().getId()!=null)
			sentence.append(" AND c.area.id = ?");
		sentence.append(" ORDER BY c.code,c.name");

		Query query = s.createQuery(sentence.toString());
		query.setDate(0, getToDate());
		query.setDate(1, getFromDate());
		if (getArea()!=null && getArea().getId()!=null)
			query.setInteger(2, getArea().getId());
		List<SupplierEvolution> list = query.list();
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
		setArea(new Area());
	}
	
}
