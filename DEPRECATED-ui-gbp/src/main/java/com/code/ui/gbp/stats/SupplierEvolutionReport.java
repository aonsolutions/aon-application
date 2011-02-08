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
import com.code.gbp.Supplier;

public class SupplierEvolutionReport implements ICollectionProvider {

	private Supplier supplier;
	private Date fromDate;
	private Date toDate;
	
	public Supplier getSupplier() {
		if (supplier==null) {
			supplier = new Supplier();
		}
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
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
		String subStmtTotal = "(SELECT SUM(inv.amount) FROM ProFormaInvoice inv WHERE inv.supplier.id = cs.supplier.id AND inv.campaign.startDate <= ? AND inv.campaign.endDate >= ?)";
		String stmt = "SELECT " + "new com.code.ui.gbp.stats.SupplierEvolution("
				+ "cs.supplier.id,cs.supplier.name,cs.supplier.status,cs.campaign.code,cs.campaign.name,SUM(off.price)," + subStmt +","+ subStmtTotal
				+ ") FROM CampaignSupplier cs,Offer off WHERE ";
		StringBuilder sentence = new StringBuilder(stmt);
		if (getSupplier() != null && getSupplier().getId() != null) {
			sentence.append("cs.supplier.id = ");
			sentence.append(getSupplier().getId());
			sentence.append(" AND ");
		}
		sentence.append(" cs.campaign.startDate <= ? AND cs.campaign.endDate >= ?");
		sentence.append(" AND cs.campaign.id = off.campaign.id ");
		sentence.append(" AND cs.supplier.id = off.supplier.id ");
		sentence.append(" GROUP BY cs.supplier.id,cs.supplier.name,cs.campaign.code,cs.campaign.name");
		sentence.append(" ORDER BY cs.supplier.name,cs.campaign.code,cs.campaign.name");

		Query query = s.createQuery(sentence.toString());
		query.setDate(0, getToDate());
		query.setDate(1, getFromDate());
		query.setDate(2, getToDate());
		query.setDate(3, getFromDate());
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
	}
	
	public static void main(String[] args) {
		SupplierEvolutionReport rep = new SupplierEvolutionReport();
		rep.setFromDate(new Date("01/01/08"));
		rep.setToDate(new Date("11/06/08"));
		Supplier supplier = new Supplier();
		supplier.setId(new Integer(332));
		rep.setSupplier(supplier);
		System.out.println(rep.getCollection().size());
		System.out.println("->");		
	}
}
