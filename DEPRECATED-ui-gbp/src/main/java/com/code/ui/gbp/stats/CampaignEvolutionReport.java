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
import com.code.gbp.Campaign;

public class CampaignEvolutionReport implements ICollectionProvider {

	private Campaign campaign;
	private Date fromDate;
	private Date toDate;
	
	public Campaign getCampaign() {
		if (campaign==null) {
			campaign = new Campaign();
		}
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
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
		String subStmt = "(SELECT SUM(proinv.amount) FROM ProFormaInvoice proinv WHERE proinv.campaign.id = inv.campaign.id)";
		String stmt = "SELECT " + "new com.code.ui.gbp.stats.CampaignEvolution(" +
				"inv.campaign,inv.supplier," + subStmt + ",COUNT( * ),SUM(inv.amount)"
				+ ") "
				+ " FROM ProFormaInvoice inv WHERE ";
		StringBuilder sentence = new StringBuilder(stmt);
		if (getCampaign() != null && getCampaign().getId()!= null) {
			sentence.append("inv.campaign.id = ");
			sentence.append(getCampaign().getId());
			sentence.append(" AND ");
		}
		sentence.append(" inv.campaign.startDate <= ? AND inv.campaign.endDate >= ?");
		sentence.append(" GROUP BY inv.campaign,inv.supplier");
		sentence.append(" ORDER BY inv.campaign.code,inv.supplier.supplierType.id,inv.supplier.name");

		Query query = s.createQuery(sentence.toString());
		query.setDate(0, getToDate());
		query.setDate(1, getFromDate());
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
		CampaignEvolutionReport c = new CampaignEvolutionReport();
		c.onInitialize(null);
		c.getCollection();
	}
}
