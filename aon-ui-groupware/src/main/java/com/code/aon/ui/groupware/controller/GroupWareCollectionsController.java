package com.code.aon.ui.groupware.controller;



import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.groupware.CampaignType;
import com.code.aon.groupware.CostProfile;
import com.code.aon.groupware.FavoriteCategory;
import com.code.aon.groupware.JobType;
import com.code.aon.groupware.Process;
import com.code.aon.groupware.ProcessDetail;
import com.code.aon.groupware.ProcessTransitionType;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.groupware.TaskHolderWorkgroup;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.CampaignStatus;
import com.code.aon.groupware.enumeration.DailyTrackingReportType;
import com.code.aon.groupware.enumeration.DateReference;
import com.code.aon.groupware.enumeration.DelayTime;
import com.code.aon.groupware.enumeration.NoticeStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.groupware.enumeration.TaskHolderType;
import com.code.aon.groupware.enumeration.TaskPeriod;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.esferalia.aon.entity.IEntityAlias;

public class GroupWareCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<SelectItem> dateReferences;
	private List<SelectItem> alarmStatuses;
	private List<SelectItem> alarmSources;
	private List<SelectItem> noticeStatuses;
	private List<SelectItem> noticeTypes;
	private List<SelectItem> priorities;
	private List<SelectItem> delayTimes;
	private List<SelectItem> taskHolders;
	private List<SelectItem> taskHolderTypes;
	private List<SelectItem> taskPeriods;
	private List<SelectItem> dailyTrackingReportTypes;
	private LinkedList<SelectItem> campaignStatuses;
	

	public FavoriteCategory getFavoriteCategory() {
		return null;
	}

	public void setFavoriteCategory(FavoriteCategory favoriteCategory) {
	}

	public List<SelectItem> getDateReferences()  {
		if (dateReferences == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			dateReferences = new LinkedList<SelectItem>();
			for (DateReference dateReference : DateReference.values()) {
				String name = dateReference.getName(locale);
				SelectItem item = new SelectItem(dateReference, name);
				dateReferences.add(item);
			}
		}
		return dateReferences;
	}

	public List<SelectItem> getAlarmStatus() {
		if (alarmStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			alarmStatuses = new LinkedList<SelectItem>();
			for (AlarmStatus alarmStatus : AlarmStatus.values()) {
				String name = alarmStatus.getName(locale);
				SelectItem item = new SelectItem(alarmStatus, name);
				alarmStatuses.add(item);
			}
		}
		return alarmStatuses;
	}

	public List<SelectItem> getAlarmSources() {
		if (alarmSources == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			alarmSources = new LinkedList<SelectItem>();
			for (AlarmSource alarmSource : AlarmSource.values()) {
				String name = alarmSource.getName(locale);
				SelectItem item = new SelectItem(alarmSource, name);
				alarmSources.add(item);
			}
		}
		return alarmSources;
	}

	public List<SelectItem> getNoticeStatuses() {
		if (noticeStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			noticeStatuses = new LinkedList<SelectItem>();
			for (NoticeStatus noticeStatus : NoticeStatus.values()) {
				String name = noticeStatus.getName(locale);
				SelectItem item = new SelectItem(noticeStatus, name);
				noticeStatuses.add(item);
			}
		}
		return noticeStatuses;
	}

	public List<SelectItem> getNoticeTypes() {
		if (noticeTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			noticeTypes = new LinkedList<SelectItem>();
			for (NoticeType noticeType : NoticeType.values()) {
				String name = noticeType.getName(locale);
				SelectItem item = new SelectItem(noticeType, name);
				noticeTypes.add(item);
			}
		}
		return noticeTypes;
	}

	public List<SelectItem> getPriorities() {
		if (priorities == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			priorities = new LinkedList<SelectItem>();
			for (Priority priority : Priority.values()) {
				String name = priority.getName(locale);
				SelectItem item = new SelectItem(priority, name);
				priorities.add(item);
			}
		}
		return priorities;
	}

	public List<SelectItem> getDelayTimes() {
		if (delayTimes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			delayTimes = new LinkedList<SelectItem>();
			for (DelayTime delay : DelayTime.values()) {
				String name = delay.getName(locale);
				SelectItem item = new SelectItem(delay, name);
				delayTimes.add(item);
			}
		}
		return delayTimes;
	}

	public List<SelectItem> getFavoriteCategories() throws ManagerBeanException {
		List<SelectItem> favoriteCategoriesList = new LinkedList<SelectItem>();
		IManagerBean favoriteCategoriesBean = BeanManager.getManagerBean(FavoriteCategory.class);
		User user = UserUtils.getInstance().getLoggedUser();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(favoriteCategoriesBean.getFieldName(IEntityAlias.FAVORITE_CATEGORY_USER_ID),
				user.getId());
		criteria.addOrder(favoriteCategoriesBean.getFieldName(IEntityAlias.FAVORITE_CATEGORY_DESCRIPTION));
		List<ITransferObject> list = favoriteCategoriesBean.getList(criteria);
		for (ITransferObject to: list) {
			FavoriteCategory category = (FavoriteCategory) to;
			SelectItem item = new SelectItem(category, category.getDescription());
			favoriteCategoriesList.add(item);
		}
		return favoriteCategoriesList;
	}

	public TaskHolder getTaskHolder() {
		return null;
	}
	public void setTaskHolder(TaskHolder taskHolder) {
	}

	public List<SelectItem> getTaskHolders() throws ManagerBeanException {
		if (taskHolders == null) {
			taskHolders = new LinkedList<SelectItem>();
			IManagerBean bean = BeanManager.getManagerBean(TaskHolder.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TASK_HOLDER_ACTIVE), true);
			List<ITransferObject> list = bean.getList(criteria);
			for (ITransferObject to:list) {
				TaskHolder taskHolder = (TaskHolder) to;
				SelectItem item = new SelectItem(taskHolder, taskHolder.getRegistry().getFullName());
				taskHolders.add(item);
			}
		}
		return taskHolders;
	}
	
	public List<SelectItem> getTaskHolderWorkgroups( WorkGroup workGroup) throws ManagerBeanException {
		List<SelectItem> taskHolderWorkgroups = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(TaskHolderWorkgroup.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TASK_HOLDER_WORKGROUP_WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
		if (workGroup != null) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TASK_HOLDER_WORKGROUP_WORK_GROUP_ID), workGroup.getId());
		}
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to:list) {
			TaskHolderWorkgroup taskHolderWorkgroup = (TaskHolderWorkgroup) to;
			SelectItem item = new SelectItem(taskHolderWorkgroup.getTaskHolder(), taskHolderWorkgroup.getTaskHolder().getRegistry().getFullName());
			taskHolderWorkgroups.add(item);
		}
		return taskHolderWorkgroups;
	}

	public List<SelectItem> getTaskHolderWorkgroups( TaskHolder taskHolder) throws ManagerBeanException {
		List<SelectItem> taskHolderWorkgroups = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(TaskHolderWorkgroup.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TASK_HOLDER_WORKGROUP_WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
		if (taskHolder != null && taskHolder.getId() != null) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TASK_HOLDER_WORKGROUP_TASK_HOLDER_ID), taskHolder.getId());
		}
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to:list) {
			TaskHolderWorkgroup taskHolderWorkgroup = (TaskHolderWorkgroup) to;
			SelectItem item = new SelectItem(taskHolderWorkgroup.getWorkGroup(), taskHolderWorkgroup.getWorkGroup().getDescription());
			taskHolderWorkgroups.add(item);
		}
		return taskHolderWorkgroups;
	}

	public List<SelectItem> getJobTypes() throws ManagerBeanException {
		List<SelectItem> jobTypeList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(JobType.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(IEntityAlias.JOB_TYPE_DESCRIPTION));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to:list) {
			JobType type = (JobType) to;
			SelectItem item = new SelectItem(type, type.getDescription());
			jobTypeList.add(item);
		}
		return jobTypeList;
	}

	public List<SelectItem> getDailyTrackingReportTypes() {
		if (dailyTrackingReportTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			dailyTrackingReportTypes = new LinkedList<SelectItem>();
			for (DailyTrackingReportType type : DailyTrackingReportType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				dailyTrackingReportTypes.add(item);
			}
		}
		return dailyTrackingReportTypes;
	}

	public List<SelectItem> getProcessTransitionTypes() throws ManagerBeanException {
		List<SelectItem> processList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(ProcessTransitionType.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROCESS_TRANSITION_TYPE_DESCRIPTION));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to:list) {
			ProcessTransitionType ptt = (ProcessTransitionType) to;
			SelectItem item = new SelectItem(ptt, ptt.getDescription());
			processList.add(item);
		}
		return processList;
	}
	
    public List<SelectItem> getTaskPeriods() {
		if (taskPeriods == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			taskPeriods = new LinkedList<SelectItem>();
			for (TaskPeriod taskPeriod: TaskPeriod.values()) {
				String name = taskPeriod.getName(locale);
				SelectItem item = new SelectItem(taskPeriod, name);
				taskPeriods.add(item);
			}
		}
        return taskPeriods;
    }

	public List<SelectItem> getProcesses() throws ManagerBeanException {
		List<SelectItem> processList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Process.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROCESS_DESCRIPTION));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to:list) {
			Process process = (Process) to;
			SelectItem item = new SelectItem(process, process.getDescription());
			processList.add(item);
		}
		return processList;
	}

	public List<SelectItem> getCampaignTypes() throws ManagerBeanException {
		List<SelectItem> campaignTypeList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(CampaignType.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(IEntityAlias.CAMPAIGN_TYPE_DESCRIPTION));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to:list) {
			CampaignType campaignType = (CampaignType) to;
			SelectItem item = new SelectItem(campaignType, campaignType.getDescription());
			campaignTypeList.add(item);
		}
		return campaignTypeList;
	}
	
    public List<SelectItem> getCampaignStatuses() {
    	if (campaignStatuses == null) {
	        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    	campaignStatuses = new LinkedList<SelectItem>();
	    	for (CampaignStatus campaignStatus: CampaignStatus.values()) {
	            String name = campaignStatus.getName(locale);
	            SelectItem item = new SelectItem(campaignStatus, name);
	            campaignStatuses.add(item);
	        }
	    }
        return campaignStatuses;
    }

	public List<SelectItem> getProcessDetails(Process process) throws ManagerBeanException {
		List<SelectItem> processDetailList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(ProcessDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_DETAIL_PROCESS_ID), process.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_DETAIL_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROCESS_DETAIL_POSITION));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to:list) {
			ProcessDetail processDetail = (ProcessDetail) to;
			SelectItem item = new SelectItem(processDetail, processDetail.getDescription());
			processDetailList.add(item);
		}
		return processDetailList;
	}
	
	public List<SelectItem> getCostProfiles() throws ManagerBeanException {
		List<SelectItem> profileList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(CostProfile.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(IEntityAlias.COST_PROFILE_DESCRIPTION));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to:list) {
			CostProfile profile = (CostProfile) to;
			SelectItem item = new SelectItem(profile, profile.getDescription());
			profileList.add(item);
		}
		return profileList;
	}
		
	public List<SelectItem> getTaskHolderTypes() {
		if (taskHolderTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			taskHolderTypes = new LinkedList<SelectItem>();
			for (TaskHolderType taskHolderType : TaskHolderType.values()) {
				String name = taskHolderType.getName(locale);
				SelectItem item = new SelectItem(taskHolderType, name);
				taskHolderTypes.add(item);
			}
		}
		return taskHolderTypes;
	}
	
}