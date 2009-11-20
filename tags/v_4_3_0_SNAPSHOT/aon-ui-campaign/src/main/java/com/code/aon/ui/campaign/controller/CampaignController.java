package com.code.aon.ui.campaign.controller;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

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
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.project.util.CampaignTaskManager;

public class CampaignController extends BasicController {

    private static final Logger LOGGER = Logger.getLogger(CampaignController.class.getName());
	private CampaignTaskManager campaignTaskManager;
	private boolean statusPending = true;
	private boolean statusInProgress = true;
	private boolean statusFinished = false;
	private boolean statusDeleted = false;
	
	private CampaignTaskManager getCampaignTaskManager() {
		if (campaignTaskManager == null) {
			campaignTaskManager = new CampaignTaskManager();
		}
		return campaignTaskManager;
	}
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		setStatusPending(true);
		setStatusInProgress(true);
		setStatusFinished(false);
		setStatusDeleted(false);
	}
    public void onSearch(ActionEvent event) {
        try {
            String alias = getManagerBean().getFieldName(ICampaignAlias.CAMPAIGN_STATUS);
			if (isStatusDeleted() || isStatusFinished() || isStatusInProgress()
					|| isStatusPending()) {
				Expression[] exps = { null, null, null, null };
				int count = 0;
				int inCaseCount1 = -1;
				if (isStatusDeleted()) {
					exps[0] = ExpressionUtilities.getEqualExpression(alias,CampaignStatus.DELETED);
					count++;
					inCaseCount1 = 0;
				}
				if (isStatusFinished()) {
					exps[1] = ExpressionUtilities.getEqualExpression(alias,CampaignStatus.FINISHED);
					count++;
					inCaseCount1 = 1;
				}
				if (isStatusInProgress()) {
					exps[2] = ExpressionUtilities.getEqualExpression(alias,CampaignStatus.IN_PROGRESS);
					count++;
					inCaseCount1 = 2;
				}
				if (isStatusPending()) {
					exps[3] = ExpressionUtilities.getEqualExpression(alias,CampaignStatus.PENDING);
					count++;
					inCaseCount1 = 3;
				}
				Expression expToAdd = null;
				if (count == 1) {
					expToAdd = exps[inCaseCount1];
				} else {
					// Si count > 1 hay que hacer una OR Expression
					boolean ready = false;
					for (int i = 0; i < exps.length; i++) {
						if (exps[i] != null) {
							if (!ready) {
								expToAdd = exps[i];
								ready = true;
							} else {
								expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exps[i]);
							}
						}
					}
				}
				getCriteria().addExpression(expToAdd);
			}
            super.onSearch(null);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error initializing Campaign Model", e);
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

	public void onStartCampaign(ActionEvent event) {
        Campaign campaign = (Campaign)this.getTo();
        if (campaign.getType().equals(CampaignType.AUTOMATIC)) {
            startCampaign(campaign);
        }
        changeStatus(CampaignStatus.IN_PROGRESS);
    }

    @SuppressWarnings("unchecked")
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

                getCampaignTaskManager().addCampaignTask(campaignDossier, 0, null, null);
            }
            CampaignDossierController campaignDossierController = (CampaignDossierController)FormUtil.getController("campaignDossier");
            campaignDossierController.setSortColumn(null);
            campaignDossierController.onSearch(null);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error creating campaign dossier in campaign with id=" + campaign.getId(), e);
        }
    }

    public void onFinishCampaign(ActionEvent event) {
        Campaign campaign = (Campaign)this.getTo();
        finishCampaign(campaign);
        changeStatus(CampaignStatus.FINISHED);
    }

	@SuppressWarnings("unchecked")
	private void finishCampaign(Campaign campaign) {
        try {
            IManagerBean campaignDossierBean = BeanManager.getManagerBean(CampaignDossier.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(campaignDossierBean.getFieldName(ICampaignAlias.CAMPAIGN_DOSSIER_CAMPAIGN_ID), campaign.getId());
            List campaignDossierList = campaignDossierBean.getList(criteria);
            Iterator iterator = campaignDossierList.iterator();
            while (iterator.hasNext()) {
                CampaignDossier campaignDossier = (CampaignDossier)iterator.next();
                getCampaignTaskManager().finishCampaignTask(campaignDossier);
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

    @SuppressWarnings("unchecked")
	private void removeCampaign(Campaign campaign) {
        try {
            IManagerBean campaignDossierBean = BeanManager.getManagerBean(CampaignDossier.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(campaignDossierBean.getFieldName(ICampaignAlias.CAMPAIGN_DOSSIER_CAMPAIGN_ID), campaign.getId());
            List campaignDossierList = campaignDossierBean.getList(criteria);
            Iterator iterator = campaignDossierList.iterator();
            while (iterator.hasNext()) {
                CampaignDossier campaignDossier = (CampaignDossier)iterator.next();
                getCampaignTaskManager().removeCampaignTask(campaignDossier);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error removing campaign with id=" + campaign.getId(), e);
        }
    }

    private void changeStatus(CampaignStatus status) {
        ((Campaign)this.getTo()).setStatus(status);
        accept(null);
    }

	public boolean isStatusPending() {
		return statusPending;
	}

	public void setStatusPending(boolean statusPending) {
		this.statusPending = statusPending;
	}

	public boolean isStatusInProgress() {
		return statusInProgress;
	}

	public void setStatusInProgress(boolean statusInProgress) {
		this.statusInProgress = statusInProgress;
	}

	public boolean isStatusFinished() {
		return statusFinished;
	}

	public void setStatusFinished(boolean statusFinished) {
		this.statusFinished = statusFinished;
	}

	public boolean isStatusDeleted() {
		return statusDeleted;
	}

	public void setStatusDeleted(boolean statusDeleted) {
		this.statusDeleted = statusDeleted;
	}
}