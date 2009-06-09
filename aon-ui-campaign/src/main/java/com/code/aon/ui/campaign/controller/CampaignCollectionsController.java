package com.code.aon.ui.campaign.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.campaign.Process;
import com.code.aon.campaign.ProcessTransitionType;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.campaign.enumeration.CampaignStatus;
import com.code.aon.campaign.enumeration.CampaignType;
import com.code.aon.campaign.enumeration.DateReference;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

public class CampaignCollectionsController {

	@SuppressWarnings("unchecked")
	public List<SelectItem> getProcesses() throws ManagerBeanException {
		List<SelectItem> processList = new LinkedList<SelectItem>();
		IManagerBean processBean = BeanManager.getManagerBean(Process.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(processBean.getFieldName(ICampaignAlias.PROCESS_DESCRIPTION));
		Iterator iter = processBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Process process = (Process)iter.next();
			SelectItem item = new SelectItem(process, process.getDescription());
			processList.add(item);
		}
		return processList;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getProcessTransitionTypes() throws ManagerBeanException {
		List<SelectItem> processList = new LinkedList<SelectItem>();
		IManagerBean processBean = BeanManager.getManagerBean(ProcessTransitionType.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(processBean.getFieldName(ICampaignAlias.PROCESS_TRANSITION_TYPE_DESCRIPTION));
		Iterator iter = processBean.getList(criteria).iterator();
		while(iter.hasNext()){
			ProcessTransitionType ptt = (ProcessTransitionType) iter.next();
			SelectItem item = new SelectItem(ptt, ptt.getDescription());
			processList.add(item);
		}
		return processList;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getProcessEntities() throws ManagerBeanException {
		List<SelectItem> processList = new LinkedList<SelectItem>();
		IManagerBean processBean = BeanManager.getManagerBean(Process.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(processBean.getFieldName(ICampaignAlias.PROCESS_DESCRIPTION));
		Iterator iter = processBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Process process = (Process)iter.next();
			SelectItem item = new SelectItem(process, process.getDescription());
			processList.add(item);
		}
		return processList;
	}

	public List<SelectItem> getDateReferences()  {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> dateReferenceList = new LinkedList<SelectItem>();
		DateReference[] dateReferences = DateReference.values();
		for (int i = 0; i < dateReferences.length; i++) {
			DateReference dateReference = dateReferences[i];
			String name = dateReference.getName(locale);
			SelectItem item = new SelectItem(dateReference, name);
			dateReferenceList.add(item);
		}
		return dateReferenceList;
	}

    public List<SelectItem> getCampaignTypes() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> campaignTypeList = new LinkedList<SelectItem>();
        CampaignType[] campaignTypes = CampaignType.values();
        for (int i = 0; i < campaignTypes.length; i++) {
            CampaignType campaignType = campaignTypes[i];
            String name = campaignType.getName(locale);
            SelectItem item = new SelectItem(campaignType, name);
            campaignTypeList.add(item);
        }
        return campaignTypeList;
    }
    
    public List<SelectItem> getCampaignStatus() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> campaignStatusList = new LinkedList<SelectItem>();
        CampaignStatus[] campaignStatuses = CampaignStatus.values();
        for (int i = 0; i < campaignStatuses.length; i++) {
            CampaignStatus campaignStatus = campaignStatuses[i];
            String name = campaignStatus.getName(locale);
            SelectItem item = new SelectItem(campaignStatus, name);
            campaignStatusList.add(item);
        }
        return campaignStatusList;
    }
}