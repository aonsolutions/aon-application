package com.code.ui.gbp.stats;

import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class CampaignComparerReport implements ICollectionProvider {

	private String campaign_1;

	private String campaign_2;

	public String getCampaign_1() {
		return campaign_1;
	}

	public void setCampaign_1(String campaign_1) {
		this.campaign_1 = campaign_1;
	}

	public String getCampaign_2() {
		return campaign_2;
	}

	public void setCampaign_2(String campaign_2) {
		this.campaign_2 = campaign_2;
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
		String subStmt_1 = "(SELECT c " +
				"FROM Campaign c " +
				"WHERE c.code = ? )";
		String subStmt_2 = "(SELECT COUNT(*) " +
				"FROM ProFormaInvoice inv " +
				"WHERE inv.supplier.supplierType.id = cs.supplier.supplierType.id " +
				"AND inv.campaign.code = ? )";
		String subStmt_3 = "(SELECT SUM(inv.amount) " +
				"FROM ProFormaInvoice inv " +
				"WHERE inv.supplier.supplierType.id = cs.supplier.supplierType.id " +
				"AND inv.campaign.code = ? )";
		String stmt = "SELECT " + "new com.code.ui.gbp.stats.CampaignComparer("
				+ "cs.supplier.supplierType.description," + subStmt_1+","+subStmt_1+","+subStmt_2+","+subStmt_3+","+subStmt_2+","+subStmt_3
				+" ) "
				+ "FROM CampaignSupplier cs WHERE ";
		StringBuilder sentence = new StringBuilder(stmt);
		sentence.append(" ( cs.campaign.code = ? OR cs.campaign.code = ? ) ");
		sentence.append(" GROUP BY cs.supplier.supplierType.id");

		Query query = s.createQuery(sentence.toString());
		query.setString(0, getCampaign_1());
		query.setString(1, getCampaign_2());
		query.setString(2, getCampaign_1());
		query.setString(3, getCampaign_1());
		query.setString(4, getCampaign_2());
		query.setString(5, getCampaign_2());
		query.setString(6, getCampaign_1());
		query.setString(7, getCampaign_2());
		List<SupplierEvolution> list = query.list();
		return list;
	}

	public void onInitialize(ActionEvent event) {
		setCampaign_1(null);
		setCampaign_2(null);
	}
	
	public static void main(String[] args) {
		CampaignComparerReport rep = new CampaignComparerReport();
		rep.setCampaign_1("614");
		rep.setCampaign_2("1215");
		System.out.println(rep.getCollection().size());
		System.out.println("->");		
	}

}
