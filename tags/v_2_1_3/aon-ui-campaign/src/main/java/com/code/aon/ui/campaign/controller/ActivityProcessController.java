package com.code.aon.ui.campaign.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.campaign.ActivityProcess;
import com.code.aon.campaign.CampaignDossier;
import com.code.aon.campaign.Process;
import com.code.aon.campaign.ProcessDetail;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.project.util.CampaignTaskManager;
import com.code.aon.ui.util.AonUtil;

public class ActivityProcessController extends BasicController {

    private static final Logger LOGGER = Logger.getLogger(ActivityProcessController.class.getName());

    private CampaignDossier campaignDossier;
    private ProcessDetail processDetail;

    public CampaignDossier getCampaignDossier() {
        return campaignDossier;
    }

    public void setCampaignDossier(CampaignDossier campaignDossier) {
        this.campaignDossier = campaignDossier;
    }

    public ProcessDetail getProcessDetail() {
        return processDetail;
    }

    public void setProcessDetail(ProcessDetail processDetail) {
        this.processDetail = (processDetail == null) ? new ProcessDetail() : processDetail;
    }

    public List getProcessDetailList() throws ManagerBeanException {
        List<SelectItem> processDetailList = new LinkedList<SelectItem>();
        IManagerBean processDetailBean = BeanManager.getManagerBean(ProcessDetail.class);
        Process process = getCampaignDossier().getCampaign().getProcess();
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(processDetailBean.getFieldName(ICampaignAlias.PROCESS_DETAIL_PROCESS_ID), process.getId());
        Iterator iter = processDetailBean.getList(criteria).iterator();
        while(iter.hasNext()){
            ProcessDetail processDetail = (ProcessDetail)iter.next();
            SelectItem item = new SelectItem(processDetail.getId(), processDetail.getDescription());
            processDetailList.add(item);
        }
        return processDetailList;
    }

    @SuppressWarnings("unused")
    public void onSearchTasks(ActionEvent event) {
        try {
            CampaignDossier campaignDossier = (CampaignDossier)AonUtil.getController("campaignDossier").getModel().getRowData();
            setCampaignDossier(campaignDossier);
            setProcessDetail(CampaignTaskManager.getCurrentProcessDetail(campaignDossier));
            searchTasks();
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining campaign dossier", e);
        }
    }

    private void searchTasks() {
        Integer campaignId = campaignDossier.getCampaign().getId();
        Integer dossierId = campaignDossier.getDossier().getId();
        try {
            IManagerBean activityProcessBean = BeanManager.getManagerBean(ActivityProcess.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(activityProcessBean.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_CAMPAIGN_ID), campaignId);
            criteria.addEqualExpression(activityProcessBean.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_DOSSIER_ID), dossierId);
            criteria.addOrder(activityProcessBean.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_TASK_ID));
            setCriteria(criteria);
            onSearch(null);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining campaign tasks in campaign with id=" + campaignId, e);
        }
    }

    @SuppressWarnings("unused")
    public void onChangeProcessDetail(ActionEvent event) {
        try {
            CampaignTaskManager.finishCampaignTask(getCampaignDossier());

            Integer position = CampaignTaskManager.getProcessDetailPosition(getProcessDetail());
            if (position != null) {
                CampaignTaskManager.addCampaignTask(getCampaignDossier(), position.intValue());
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error changing process detail to dossier number=" + getCampaignDossier().getDossier().getNumber(), e);
        }
    }

}