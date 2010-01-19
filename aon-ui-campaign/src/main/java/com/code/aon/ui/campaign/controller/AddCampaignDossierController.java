package com.code.aon.ui.campaign.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.campaign.Campaign;
import com.code.aon.campaign.CampaignDossier;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.Activity;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.DossierStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.project.util.CampaignTaskManager;

public class AddCampaignDossierController extends BasicController {

    private static final Logger LOGGER = Logger.getLogger(AddCampaignDossierController.class.getName());

    private Criteria mainCriteria;
    private String sortColumn;
    private boolean ascending;
    private ArrayList<Activity> checks = new ArrayList<Activity>();
	private CampaignTaskManager campaignTaskManager;
	
	private CampaignTaskManager getCampaignTaskManager() {
		if (campaignTaskManager == null) {
			campaignTaskManager = new CampaignTaskManager();
		}
		return campaignTaskManager;
	}

    public Criteria getMainCriteria() {
        return mainCriteria;
    }

    public void setMainCriteria(Expression expression) {
        this.mainCriteria = new Criteria();
        this.mainCriteria.addExpression(expression);
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(String sortColumn) {
        this.ascending = (sortColumn.equals(getSortColumn())) ? !isAscending() : true;
    }

    public boolean getRowChecked() {
        Activity to = (Activity)model.getRowData();
        return checks.contains(to);
    }

    public void setRowChecked(boolean rowChecked) {
        if (rowChecked) {
            Activity to = (Activity)model.getRowData();
            if (!checks.contains(to)) {
                checks.add(to);
            }
        } else {
            Activity to = (Activity)model.getRowData();
            if (checks.contains(to)) {
                checks.remove(to);
            }
        }
    }

    public void rowSelected(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            setRowChecked(((Boolean)event.getNewValue()).booleanValue());
        }
    }

    public void onSearchDossiers(ActionEvent event) {
        Campaign campaign = (Campaign)FormUtil.getController("campaign").getTo();
        searchDossiers(campaign);
    }

    @SuppressWarnings("unchecked")
	private void searchDossiers(Campaign campaign) {
        try {
            IManagerBean activityBean = BeanManager.getManagerBean(Activity.class);
            String alias = activityBean.getFieldName(IProjectAlias.ACTIVITY_DOSSIER_ID);
            Expression dossierExpression = null;
            IManagerBean campaignDossierBean = BeanManager.getManagerBean(CampaignDossier.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(campaignDossierBean.getFieldName(ICampaignAlias.CAMPAIGN_DOSSIER_CAMPAIGN_ID), campaign.getId());
            Iterator iterator = campaignDossierBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                CampaignDossier campaignDossier = (CampaignDossier)iterator.next();
                Expression expression = ExpressionUtilities.getNotEqualExpression(alias, campaignDossier.getDossier().getId());
                dossierExpression = ExpressionUtilities.getAndExpression(dossierExpression, expression);
            }

            Integer activityType = campaign.getActivityType().getId();
            criteria = new Criteria();
            criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_ACTIVITY_TYPE_ID), activityType);
            criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_DOSSIER_STATUS), DossierStatus.ACTIVE);
            if (dossierExpression != null) {
                criteria.addExpression(dossierExpression);
            }
            setMainCriteria(criteria.getExpression());
            setSortColumn(null);

            criteria.addOrder(activityBean.getFieldName(IProjectAlias.ACTIVITY_CUSTOMER_NAME));
            criteria.addOrder(activityBean.getFieldName(IProjectAlias.ACTIVITY_CUSTOMER_SURNAME));
            criteria.addOrder(activityBean.getFieldName(IProjectAlias.ACTIVITY_CUSTOMER_DOCUMENT));
            criteria.addOrder(activityBean.getFieldName(IProjectAlias.ACTIVITY_DOSSIER_NUMBER));
            setCriteria(criteria);
            onSearch(null);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining campaign dossiers in campaign with id=" + campaign.getId(), e);
        }
    }

    @SuppressWarnings("unchecked" )
    public void onAddCampaignDossiers(ActionEvent event) {
        Campaign campaign = (Campaign)FormUtil.getController("campaign").getTo();
        try {
            IManagerBean campaignDossierBean = BeanManager.getManagerBean(CampaignDossier.class);
            Iterator iterator = checks.iterator();
            while (iterator.hasNext()) {
                Activity activity = (Activity)iterator.next();

                CampaignDossier campaignDossier = new CampaignDossier();
                campaignDossier.setCampaign(campaign);
                campaignDossier.setDossier(activity.getDossier());
                campaignDossier = (CampaignDossier)campaignDossierBean.insert(campaignDossier);

                getCampaignTaskManager().addCampaignTask(campaignDossier, 0, null,null);
            }
            CampaignDossierController campaignDossierController = (CampaignDossierController)FormUtil.getController("campaignDossier");
            campaignDossierController.onSearch(null);

            checks = new ArrayList<Activity>();
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error creating campaign dossier in campaign with id=" + campaign.getId(), e);
        }
    }

    public void sort(ActionEvent event) {
        LOGGER.log(Level.SEVERE, "Error sorting campaign dossier list");
    }

}