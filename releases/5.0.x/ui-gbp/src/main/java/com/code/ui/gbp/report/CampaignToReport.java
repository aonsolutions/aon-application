package com.code.ui.gbp.report;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.Campaign;
import com.code.gbp.CampaignSupplier;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.report.CampaignReport;

public class CampaignToReport implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(CampaignToReport.class.getName());
	
	private static final String CAMPAIGN_CONTROLLER_NAME = "campaign";

	public Collection getCollection() {
		List<CampaignReport> list = new LinkedList<CampaignReport>();
		try{
			CampaignReport campaignReport = new CampaignReport();
			IController campaignController = AonUtil.getController(CAMPAIGN_CONTROLLER_NAME);
			Campaign campaign = (Campaign)campaignController.getTo();
			campaignReport.setCampaign(campaign);
			list.add(campaignReport);
			campaignReport.setSuppliers(obtainList(CampaignSupplier.class, IGBPAlias.CAMPAIGN_SUPPLIER_CAMPAIGN_ID, campaign.getId()));
		}catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error obtaining supplier report", e);
		}
		return list;
	}

	private List<ITransferObject> obtainList(Class pojoClass, String alias, Object data){
		try {
			IManagerBean bean = BeanManager.getManagerBean(pojoClass);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(alias), data);
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining list in report.", e);
		}
		return null;
	}
	
	public Collection getCollection(boolean arg0) throws ManagerBeanException {
		// TODO Auto-generated method stub
		return null;
	}

}
