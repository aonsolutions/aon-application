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
import com.code.gbp.Campaign;

public class InvoiceControlReport implements ICollectionProvider {

	private Campaign campaign;
	
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

	public Campaign getCampaign() {
		if (campaign==null) {
			campaign = new Campaign();
		}
		return campaign;
	}

	public void setCampaign(com.code.gbp.Campaign campaign) {
		this.campaign = campaign;
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
		String stmt = "SELECT " + "new com.code.ui.gbp.stats.InvoiceControl("
				+ "inv.campaign.code,inv.campaign.name,"
				+ "inv.supplier.supplierType.description,inv.supplier.name,inv.supplier.status,"
				+ "inv.concept,inv.number,inv.amount,inv.invoiceDate,inv.paymentDate"
				+ ") "
				+ "FROM ProFormaInvoice inv WHERE ";
		StringBuilder sentence = new StringBuilder(stmt);
		sentence.append(" inv.campaign.id = ? ");
		sentence.append(" AND inv.invoiceDate >= ? AND inv.invoiceDate <= ?");
		sentence.append(" ORDER BY inv.supplier.supplierType.id");

		Query query = s.createQuery(sentence.toString());
		query.setInteger(0, getCampaign().getId());
		query.setDate(1, getFromDate());
		query.setDate(2, getToDate());
		List<SupplierEvolution> list = query.list();
		return list;

	}

	public void onInitialize(ActionEvent event) {
		setCampaign(null);
		Date from = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(from);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, 0);
		setFromDate(c.getTime());
		setToDate(new Date());
	}
	
}
