package com.code.aon.ui.campaign.controller;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.campaign.Campaign;
import com.code.aon.campaign.CampaignDossier;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.campaign.enumeration.CampaignStatus;
import com.code.aon.campaign.enumeration.CampaignType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.Activity;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.DossierStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.project.util.CampaignTaskManager;
import com.code.aon.ui.util.AonUtil;

public class CampaignController extends BasicController {

    private static final Logger LOGGER = Logger.getLogger(CampaignController.class.getName());

    @SuppressWarnings("unused")
    public void onSearch(MenuEvent event) {
        try {
            String alias = getManagerBean().getFieldName(ICampaignAlias.CAMPAIGN_STATUS);
            Expression pendingExpression = ExpressionUtilities.getEqualExpression(alias, CampaignStatus.PENDING);
            Expression inProgressExpression = ExpressionUtilities.getEqualExpression(alias, CampaignStatus.IN_PROGRESS);
            Expression expression = ExpressionUtilities.getOrExpression(pendingExpression, inProgressExpression);

            Criteria criteria = new Criteria();
            criteria.addExpression(expression);
            setCriteria(criteria);

            super.onSearch(null);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error initializing Campaign Model", e);
        }
    }

    public void addStartDateFromExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                IManagerBean campaignBean = BeanManager.getManagerBean(Campaign.class);
                getCriteria().addGreaterThanOrEqualExpression(campaignBean.getFieldName(ICampaignAlias.CAMPAIGN_START_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding FROM start date expression", e);
            }
        }
    }
    
    public void addStartDateToExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                IManagerBean campaignBean = BeanManager.getManagerBean(Campaign.class);
                getCriteria().addLessThanOrEqualExpression(campaignBean.getFieldName(ICampaignAlias.CAMPAIGN_START_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding TO start date expression", e);
            }
        }
    }

    public void addEndDateFromExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                IManagerBean campaignBean = BeanManager.getManagerBean(Campaign.class);
                getCriteria().addGreaterThanOrEqualExpression(campaignBean.getFieldName(ICampaignAlias.CAMPAIGN_END_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding FROM end date expression", e);
            }
        }
    }
    
    public void addEndDateToExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                IManagerBean campaignBean = BeanManager.getManagerBean(Campaign.class);
                getCriteria().addLessThanOrEqualExpression(campaignBean.getFieldName(ICampaignAlias.CAMPAIGN_END_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding TO end date expression", e);
            }
        }
    }

    public void addEqualExpression(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
        if (event.getNewValue() != null && !event.getNewValue().equals(new Integer(Integer.MAX_VALUE))) {
            Object value = event.getNewValue();
            Criteria criteria = getCriteria();
            criteria.addExpression(getFieldName(event.getComponent().getId()), value.toString());
            setCriteria(criteria);
        }
    }

    public boolean isPending() {
        return ((Campaign)this.getTo()).getStatus().equals(CampaignStatus.PENDING);
    }

    public boolean isInProgress() {
        return ((Campaign)this.getTo()).getStatus().equals(CampaignStatus.IN_PROGRESS);
    }

    public boolean isFinished() {
        return ((Campaign)this.getTo()).getStatus().equals(CampaignStatus.FINISHED);
    }

    public boolean isDeleted() {
        return ((Campaign)this.getTo()).getStatus().equals(CampaignStatus.DELETED);
    }

    @SuppressWarnings("unused")
    public void onStartCampaign(ActionEvent event) {
        Campaign campaign = (Campaign)this.getTo();
        if (campaign.getType().equals(CampaignType.AUTOMATIC)) {
            startCampaign(campaign);
        }
        changeStatus(CampaignStatus.IN_PROGRESS);
    }

    private void startCampaign(Campaign campaign) {
        try {
            IManagerBean campaignDossierBean = BeanManager.getManagerBean(CampaignDossier.class);

            IManagerBean activityBean = BeanManager.getManagerBean(Activity.class);
            Integer activityType = campaign.getActivityType().getId();
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_ACTIVITY_TYPE_ID), activityType);
            criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_DOSSIER_STATUS), DossierStatus.ACTIVE);
            Iterator iterator = activityBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                Activity activity = (Activity)iterator.next();

                CampaignDossier campaignDossier = new CampaignDossier();
                campaignDossier.setCampaign(campaign);
                campaignDossier.setDossier(activity.getDossier());
                campaignDossier = (CampaignDossier)campaignDossierBean.insert(campaignDossier);

                CampaignTaskManager.addCampaignTask(campaignDossier, 0);
            }
            CampaignDossierController campaignDossierController = (CampaignDossierController)AonUtil.getController("campaignDossier");
            campaignDossierController.setSortColumn(null);
            campaignDossierController.onSearch(null);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error creating campaign dossier in campaign with id=" + campaign.getId(), e);
        }
    }

    @SuppressWarnings("unused")
    public void onFinishCampaign(ActionEvent event) {
        Campaign campaign = (Campaign)this.getTo();
        finishCampaign(campaign);
        changeStatus(CampaignStatus.FINISHED);
    }

    private void finishCampaign(Campaign campaign) {
        try {
            IManagerBean campaignDossierBean = BeanManager.getManagerBean(CampaignDossier.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(campaignDossierBean.getFieldName(ICampaignAlias.CAMPAIGN_DOSSIER_CAMPAIGN_ID), campaign.getId());
            List campaignDossierList = campaignDossierBean.getList(criteria);
            Iterator iterator = campaignDossierList.iterator();
            while (iterator.hasNext()) {
                CampaignDossier campaignDossier = (CampaignDossier)iterator.next();
                CampaignTaskManager.finishCampaignTask(campaignDossier);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error finishing campaign with id=" + campaign.getId(), e);
        }
    }

    public void onRemoveCampaign(ActionEvent event) {
        Campaign campaign = (Campaign)this.getTo();
        if (campaign.getStatus().equals(CampaignStatus.PENDING)) {
            super.onRemove(event);
        } else {
            removeCampaign(campaign);
            changeStatus(CampaignStatus.DELETED);
        }
    }

    private void removeCampaign(Campaign campaign) {
        try {
            IManagerBean campaignDossierBean = BeanManager.getManagerBean(CampaignDossier.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(campaignDossierBean.getFieldName(ICampaignAlias.CAMPAIGN_DOSSIER_CAMPAIGN_ID), campaign.getId());
            List campaignDossierList = campaignDossierBean.getList(criteria);
            Iterator iterator = campaignDossierList.iterator();
            while (iterator.hasNext()) {
                CampaignDossier campaignDossier = (CampaignDossier)iterator.next();
                CampaignTaskManager.removeCampaignTask(campaignDossier);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error removing campaign with id=" + campaign.getId(), e);
        }
    }

    private void changeStatus(CampaignStatus status) {
        ((Campaign)this.getTo()).setStatus(status);
        accept(null);
    }

}