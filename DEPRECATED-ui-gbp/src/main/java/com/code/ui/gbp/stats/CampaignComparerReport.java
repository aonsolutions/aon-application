package com.code.ui.gbp.stats;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.gbp.Campaign;

public class CampaignComparerReport implements ICollectionProvider {

	private Campaign campaign1;

	private Campaign campaign2;

	public Campaign getCampaign1() {
		if (campaign1 == null) {
			campaign1 = new Campaign();
		}
		return campaign1;
	}

	public void setCampaign1(Campaign campaign1) {
		this.campaign1 = campaign1;
	}

	public Campaign getCampaign2() {
		if (campaign2 == null) {
			campaign2 = new Campaign();
		}
		return campaign2;
	}

	public void setCampaign2(Campaign campaign2) {
		this.campaign2 = campaign2;
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
		
		String total_1 = "(SELECT SUM(inv.amount) " +
			"FROM ProFormaInvoice inv " +
			"WHERE inv.campaign.id = ?)";
		String total_2 = "(SELECT SUM(inv.amount) " +
			"FROM ProFormaInvoice inv " +
			"WHERE inv.campaign.id = ?)";
		String subStmt_1 = "(SELECT c " +
				"FROM Campaign c " +
				"WHERE c.id = ? )";
		String subStmt_2 = "(SELECT COUNT(*) " +
				"FROM ProFormaInvoice inv " +
				"WHERE inv.supplier.supplierType.id = cs.supplier.supplierType.id " +
				"AND inv.campaign.id = ? )";
		String subStmt_3 = "(SELECT SUM(inv.amount) " +
				"FROM ProFormaInvoice inv " +
				"WHERE inv.supplier.supplierType.id = cs.supplier.supplierType.id " +
				"AND inv.campaign.id = ? )";
		String subStmt_4 = "(SELECT SUM(off.price) " +
				"FROM Offer off " +
				"WHERE off.supplier.supplierType.id = cs.supplier.supplierType.id " +
				"AND off.campaign.id = ? )";
		String stmt = "SELECT " + "new com.code.ui.gbp.stats.CampaignComparer("
				+ "cs.supplier.supplierType.description," + subStmt_1+","+subStmt_1+","+subStmt_2+","+subStmt_3+","+subStmt_2+","+subStmt_3+","+subStmt_4+","+subStmt_4+","+total_1+","+total_2
				+" ) "
				+ "FROM CampaignSupplier cs WHERE ";
		StringBuilder sentence = new StringBuilder(stmt);
		sentence.append(" ( cs.campaign.id = ? OR cs.campaign.id = ? ) ");
		sentence.append(" GROUP BY cs.supplier.supplierType.id");

		Query query = s.createQuery(sentence.toString());
		query.setInteger(0, getCampaign1().getId());
		query.setInteger(1, getCampaign2().getId());
		query.setInteger(2, getCampaign1().getId());
		query.setInteger(3, getCampaign1().getId());
		query.setInteger(4, getCampaign2().getId());
		query.setInteger(5, getCampaign2().getId());
		query.setInteger(6, getCampaign1().getId());
		query.setInteger(7, getCampaign2().getId());
		query.setInteger(8, getCampaign1().getId());
		query.setInteger(9, getCampaign2().getId());
		query.setInteger(10, getCampaign1().getId());
		query.setInteger(11, getCampaign2().getId());
		List<CampaignComparer> list = query.list();
		return list;
	}

	public void onInitialize(ActionEvent event) {
		setCampaign1(null);
		setCampaign2(null);
	}

	public static void main(String[] args) {
		CampaignComparerReport rep = new CampaignComparerReport();
		Campaign c1 = new Campaign();
		c1.setId(1);
		rep.setCampaign1(c1);
		Campaign c2 = new Campaign();
		c2.setId(2);
		rep.setCampaign2(c2);
		rep.getCollection();
	}

}
