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
import com.code.aon.campaign.enumeration.ProcessDetailStatus;
import com.code.aon.campaign.enumeration.ProcessStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

public class CampaignCollectionsController {
	
	private LinkedList<SelectItem> campaignStatusesList;
	private LinkedList<SelectItem> campaignTypesList;
	private LinkedList<SelectItem> dateReferencesList;
	private LinkedList<SelectItem> processStatusesList;
	private LinkedList<SelectItem> processDetailStatusesList;
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getProcesses() throws ManagerBeanException {
		List<SelectItem> processList = new LinkedList<SelectItem>();
		IManagerBean processBean = BeanManager.getManagerBean(Process.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(processBean.getFieldName(ICampaignAlias.PROCESS_STATUS), ProcessStatus.ACTIVE);
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

	public List<SelectItem> getDateReferences()  {
		if (dateReferencesList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			dateReferencesList = new LinkedList<SelectItem>();
			DateReference[] dateReferences = DateReference.values();
			for (DateReference dateReference : dateReferences) {
				String name = dateReference.getName(locale);
				SelectItem item = new SelectItem(dateReference, name);
				dateReferencesList.add(item);
			}
		}
		return dateReferencesList;
	}

    public List<SelectItem> getCampaignTypes() {
    	if (campaignTypesList == null) {
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            campaignTypesList = new LinkedList<SelectItem>();
            CampaignType[] campaignTypes = CampaignType.values();
            for (CampaignType campaignType: campaignTypes) {
                String name = campaignType.getName(locale);
                SelectItem item = new SelectItem(campaignType, name);
                campaignTypesList.add(item);
            }
    	}
        return campaignTypesList;
    }
    
    public List<SelectItem> getCampaignStatus() {
    	if (campaignStatusesList == null) {
	        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    	campaignStatusesList = new LinkedList<SelectItem>();
	        CampaignStatus[] campaignStatuses = CampaignStatus.values();
	        for (CampaignStatus campaignStatus:campaignStatuses) {
	            String name = campaignStatus.getName(locale);
	            SelectItem item = new SelectItem(campaignStatus, name);
	            campaignStatusesList.add(item);
	        }
	    }
        return campaignStatusesList;
    }
    
    public List<SelectItem> getProcessStatus() {
    	if (processStatusesList == null) {
	        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	        processStatusesList = new LinkedList<SelectItem>();
	        ProcessStatus[] processStatuses = ProcessStatus.values();
	        for (ProcessStatus processStatus:processStatuses) {
	            String name = processStatus.getName(locale);
	            SelectItem item = new SelectItem(processStatus, name);
	            processStatusesList.add(item);
	        }
	    }
        return processStatusesList;
    }
    
    public List<SelectItem> getProcessDetailStatus() {
    	if (processDetailStatusesList == null) {
	        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	        processDetailStatusesList = new LinkedList<SelectItem>();
	        ProcessDetailStatus[] processDetailStatuses = ProcessDetailStatus.values();
	        for (ProcessDetailStatus processDetailStatus:processDetailStatuses) {
	            String name = processDetailStatus.getName(locale);
	            SelectItem item = new SelectItem(processDetailStatus, name);
	            processDetailStatusesList.add(item);
	        }
	    }
        return processDetailStatusesList;
    }    
    
}