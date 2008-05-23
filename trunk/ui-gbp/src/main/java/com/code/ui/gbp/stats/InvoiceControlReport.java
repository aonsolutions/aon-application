package com.code.ui.gbp.stats;

import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class InvoiceControlReport implements ICollectionProvider {

	private String campaign;
	
	public String getCampaign() {
		return campaign;
	}

	public void setCampaign(String campaign) {
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
				+ "inv.supplier.supplierType.description,inv.supplier.name,"
				+ "inv.concept,inv.number,inv.amount,inv.invoiceDate,inv.paymentDate"
				+ ") "
				+ "FROM ProFormaInvoice inv WHERE ";
		StringBuilder sentence = new StringBuilder(stmt);
		sentence.append(" inv.campaign.code = ? ");
		sentence.append(" ORDER BY inv.supplier.supplierType.id");

		Query query = s.createQuery(sentence.toString());
		query.setString(0, getCampaign());
		List<SupplierEvolution> list = query.list();
		return list;

	}

	public void onInitialize(ActionEvent event) {
		setCampaign(null);
	}
	
	public static void main(String[] args) {
		InvoiceControlReport rep = new InvoiceControlReport();
		rep.setCampaign("614");
		System.out.println(rep.getCollection().size());
		System.out.println("->");		
	}

}
