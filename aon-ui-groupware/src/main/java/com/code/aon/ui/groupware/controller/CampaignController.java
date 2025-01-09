package com.code.aon.ui.groupware.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.Campaign;
import com.code.aon.groupware.CampaignProject;
import com.code.aon.groupware.enumeration.CampaignStatus;
import com.code.aon.groupware.task.TaskManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.groupware.GroupwareUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CampaignController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CampaignController.class);
    
	private TaskManager taskManager;
	private GroupwareUtils groupwareUtils;

	private TaskManager getTaskManager() {
		if (taskManager == null) {
			taskManager = new TaskManager();
		}
		return taskManager;
	}
	
	public GroupwareUtils getGroupwareUtils() {
		if (groupwareUtils == null) {
			groupwareUtils = new GroupwareUtils();
		}
		return groupwareUtils;
	}
	
	public void onStart(ActionEvent event) {
		this.onEditSearch(event);
		this.onSearch(event);
	}

    public void onFinishCampaign(ActionEvent event) {
        Campaign campaign = (Campaign)this.getTo();
        finishCampaign(campaign);
        changeStatus(CampaignStatus.FINISHED);
    }

	private void finishCampaign(Campaign campaign) {
        try {
            IManagerBean bean = BeanManager.getManagerBean(CampaignProject.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CAMPAIGN_PROJECT_CAMPAIGN_ID), campaign.getId());
            List<ITransferObject> list = bean.getList(criteria);
            for (ITransferObject to:list) {
                CampaignProject cp = (CampaignProject) to;
                getTaskManager().finishCampaignTask(cp,getGroupwareUtils().getCurrentTaskHolder());
            }
        } catch (ManagerBeanException e) {
			String msg = "Error al finalizar la tarea. " + e.getMessage();
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
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
            IManagerBean bean = BeanManager.getManagerBean(CampaignProject.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CAMPAIGN_PROJECT_CAMPAIGN_ID), campaign.getId());
            List<ITransferObject> list = bean.getList(criteria);
            for(ITransferObject to : list ) {
                CampaignProject cp = (CampaignProject) to;
                getTaskManager().removeCampaignTask(cp,getGroupwareUtils().getCurrentTaskHolder());
            }
        } catch (ManagerBeanException e) {
            LOGGER.error("Error removing campaign with id=" + campaign.getId(), e);
        }
    }

    private void changeStatus(CampaignStatus status) {
        ((Campaign)this.getTo()).setStatus(status);
        accept(null);
    }
    
	public void onStartCampaign(ActionEvent event) {
        changeStatus(CampaignStatus.IN_PROGRESS);
    }
	
	public String campaignRemoveAction() {
		onRemoveCampaign(null);
		return null;
	}

}