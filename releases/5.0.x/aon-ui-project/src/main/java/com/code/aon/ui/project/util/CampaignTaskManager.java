package com.code.aon.ui.project.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import com.code.aon.campaign.ActivityProcess;
import com.code.aon.campaign.Campaign;
import com.code.aon.campaign.CampaignDossier;
import com.code.aon.campaign.Process;
import com.code.aon.campaign.ProcessDetail;
import com.code.aon.campaign.ProcessDetailTransition;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.campaign.enumeration.CampaignStatus;
import com.code.aon.campaign.enumeration.DateReference;
import com.code.aon.campaign.enumeration.ProcessDetailStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.WorkGroup;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.project.Activity;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Dossier;
import com.code.aon.project.Task;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.TaskPeriod;
import com.code.aon.project.enumeration.TaskSource;
import com.code.aon.project.enumeration.TaskStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;

public class CampaignTaskManager {

	public void addCampaignTask(CampaignDossier campaignDossier, int position,
			ProcessDetailTransition pdt, Task previousTask) throws ManagerBeanException {
		ProcessDetail processDetail;
		if (pdt == null) {
			processDetail = getProcessDetail(campaignDossier.getCampaign().getProcess(), position);
		} else {
			processDetail = pdt.getNextProcessDetail();
		}
		ActivityType activityType = campaignDossier.getCampaign().getActivityType();
		if (processDetail != null) {
			Activity activity = null;
			if (activityType != null && activityType.getId() != null) {
				activity = getActivity(campaignDossier.getDossier(), activityType);
			}

			Task task = new Task();
			if (activityType != null) {
				task.setDescription(activityType.getDescription() + " - "
						+ processDetail.getDescription());
			} else {
				task.setDescription(processDetail.getDescription() + " ["
						+ campaignDossier.getCampaign().getDescription() + "]");
			}

			task.setStartDate(new Date());
			task.setDueDate(calculateDueDate(campaignDossier.getCampaign(), processDetail
					.getDateReference(), processDetail.getDays()));
			task.setStatus(TaskStatus.PENDING);
			task.setPercent(0);
			WorkGroup wg = processDetail.getWorkgroup(); 
			if (wg == null && activity != null) {
				wg = activity.getWorkgroup();
			}
			if (wg == null) {
				wg = campaignDossier.getCampaign().getWorkGroup(); 
			}
			task.setWorkGroup(wg);
			task.setSource(TaskSource.PROCESS);
			task.setDossier(campaignDossier.getDossier());
			task.setActivity(activity);
			task.setRepeatPeriod(TaskPeriod.NONE);

			if (previousTask != null) {
				task.setComments( previousTask.getComments() );
				task.setPriority( previousTask.getPriority() );
				User sender = previousTask.getSender();
				if (sender != null && sender.getId() != null) {
					task.setSender( previousTask.getSender() );
				}
			}
			if (processDetail.getPriority() == null) {
				if (task.getPriority() == null) {
					task.setPriority( Priority.NORMAL );	
				}	
			} else {
				task.setPriority( processDetail.getPriority());
			}
			
			task = addTask(task);

			ActivityProcess activityProcess = new ActivityProcess();
			activityProcess.setCampaign(campaignDossier.getCampaign());
			activityProcess.setActivity(activity);
			activityProcess.setProcessDetail(processDetail);
			activityProcess.setTask(task);
			addActivityProcess(activityProcess);

			if (processDetail.getAlertDays() > 0) {
				Alarm alarm = new Alarm();
				alarm.setDescription(task.getDescription());
				alarm.setAlarmDate(calculateAlertDate(task.getDueDate(), processDetail
						.getAlertDays()));
				alarm.setStatus(AlarmStatus.PENDING);
				alarm.setSource(AlarmSource.TASK);
				alarm.setSourceId(task.getId());
				alarm.setPriority(Priority.NONE);
				addAlarm(alarm);
			}
		} else {
			finishCampaignIfNeeded( campaignDossier.getCampaign() );
		}
	}

	private void finishCampaignIfNeeded(Campaign campaign) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(CampaignDossier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICampaignAlias.CAMPAIGN_DOSSIER_CAMPAIGN_ID), campaign.getId());
		List<ITransferObject> list = bean.getList(criteria);
		boolean finished = true;
		for (ITransferObject to : list ) {
			CampaignDossier cd = (CampaignDossier) to;
			Task task  = getCurrentTask(cd);
			if (task != null) {
				finished = false;
				break;
			}
		}
		if (finished) {
			IManagerBean b  = BeanManager.getManagerBean(Campaign.class);	
			campaign.setStatus(CampaignStatus.FINISHED);
			b.update(campaign);
		}
		
	}

	public Task finishCampaignTask(CampaignDossier campaignDossier)
			throws ManagerBeanException {
		Task currentTask = getCurrentTask(campaignDossier);
		if (currentTask != null) {
			currentTask.setEndDate(new Date());
			currentTask.setStatus(TaskStatus.FINISHED);
			currentTask.setUser(UserUtils.getInstance().getLoggedUser());
			currentTask = updateTask(currentTask);

			finishTaskAlarm(currentTask);
		}
		return currentTask;
	}

	public void removeCampaignTask(CampaignDossier campaignDossier)
			throws ManagerBeanException {
		Task currentTask = getCurrentTask(campaignDossier);
		if (currentTask != null) {
			currentTask.setEndDate(new Date());
			currentTask.setStatus(TaskStatus.DELETED);
			currentTask.setUser(UserUtils.getInstance().getLoggedUser());
			updateTask(currentTask);

			finishTaskAlarm(currentTask);
		}
	}

	public void finishTaskAlarm(Task task) throws ManagerBeanException {
		Alarm taskAlarm = getTaskAlarm(task);
		if (taskAlarm != null) {
			taskAlarm.setStatus(AlarmStatus.FINISHED);
			taskAlarm.setUser(UserUtils.getInstance().getLoggedUser());
			updateAlarm(taskAlarm);
		}
	}

	@SuppressWarnings("unchecked")
	public Task getCurrentTask(CampaignDossier campaignDossier) throws ManagerBeanException {
		List activityProcesses = getActivityProcesses(campaignDossier);
		Iterator iterator = activityProcesses.iterator();
		while (iterator.hasNext()) {
			ActivityProcess activityProcess = (ActivityProcess) iterator.next();
			Task task = activityProcess.getTask();
			if (task.getStatus().equals(TaskStatus.PENDING)
					|| task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
				return task;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Task getPreviousTask(Task task) throws ManagerBeanException {
		ActivityProcess currentActivityProcess = getCurrentActivityProcess(task);
		CampaignDossier campaignDossier = new CampaignDossier();
		campaignDossier.setCampaign(currentActivityProcess.getCampaign());
		campaignDossier.setDossier(task.getDossier());

		List activityProcesses = getActivityProcesses(campaignDossier);
		Iterator iterator = activityProcesses.iterator();
		while (iterator.hasNext()) {
			ActivityProcess activityProcess = (ActivityProcess) iterator.next();
			if (currentActivityProcess.getId().intValue() > activityProcess.getId().intValue()) {
				return activityProcess.getTask();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public ProcessDetail getCurrentProcessDetail(CampaignDossier campaignDossier)
			throws ManagerBeanException {
		List activityProcesses = getActivityProcesses(campaignDossier);
		Iterator iterator = activityProcesses.iterator();
		while (iterator.hasNext()) {
			ActivityProcess activityProcess = (ActivityProcess) iterator.next();
			Task task = activityProcess.getTask();
			if (task.getStatus().equals(TaskStatus.PENDING)
					|| task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
				return activityProcess.getProcessDetail();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Integer getProcessDetailPosition(ProcessDetail processDetail)
			throws ManagerBeanException {
		if (processDetail != null && processDetail.getId() != null) {
			IManagerBean processDetailBean = BeanManager.getManagerBean(ProcessDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(processDetailBean
					.getFieldName(ICampaignAlias.PROCESS_DETAIL_ID), processDetail.getId());
			List processDetailList = processDetailBean.getList(criteria);
			if (processDetailList.size() > 0) {
				return new Integer(((ProcessDetail) processDetailList.get(0)).getPosition());
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public ActivityProcess getCurrentActivityProcess(Task task) throws ManagerBeanException {
		IManagerBean activityProcessBean = BeanManager.getManagerBean(ActivityProcess.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(activityProcessBean
				.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_TASK_ID), task.getId());
		Iterator iterator = activityProcessBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			ActivityProcess activityProcess = (ActivityProcess) iterator.next();
			return activityProcess;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List getActivityProcesses(CampaignDossier campaignDossier)
			throws ManagerBeanException {
		Campaign campaign = campaignDossier.getCampaign();
		Activity activity = null;
		if (campaign.getActivityType() != null) {
			activity = getActivity(campaignDossier.getDossier(), campaign.getActivityType());
		}

		IManagerBean activityProcessBean = BeanManager.getManagerBean(ActivityProcess.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(activityProcessBean
				.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_CAMPAIGN_ID), campaign.getId());
		if (activity != null) {
			criteria.addEqualExpression(activityProcessBean
					.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_ACTIVITY_ID), activity.getId());
		} else {
			criteria.addEqualExpression(activityProcessBean
					.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_TASK_DOSSIER_ID), campaignDossier
					.getDossier().getId());
		}
		criteria.addOrder(activityProcessBean.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_ID),
				false);
		return activityProcessBean.getList(criteria);
	}

	private ProcessDetail getProcessDetail(Process process, int position)
			throws ManagerBeanException {
		IManagerBean processDetailBean = BeanManager.getManagerBean(ProcessDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(processDetailBean
				.getFieldName(ICampaignAlias.PROCESS_DETAIL_PROCESS_ID), process.getId());
		criteria.addEqualExpression(processDetailBean
				.getFieldName(ICampaignAlias.PROCESS_DETAIL_STATUS), ProcessDetailStatus.ACTIVE);
		criteria.addOrder(processDetailBean.getFieldName(ICampaignAlias.PROCESS_DETAIL_POSITION));
		criteria.addGreaterThanOrEqualExpression(processDetailBean.getFieldName(ICampaignAlias.PROCESS_DETAIL_POSITION), position);
		List<ITransferObject>  list = processDetailBean.getList(criteria);
		if (list.size() > 0) {
			return (ProcessDetail) list.get(0);
		}
		return null;
	}

	private Activity getActivity(Dossier dossier, ActivityType activityType)
			throws ManagerBeanException {
		IManagerBean activityBean = BeanManager.getManagerBean(Activity.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_DOSSIER_ID),
				dossier.getId());
		criteria.addEqualExpression(activityBean
				.getFieldName(IProjectAlias.ACTIVITY_ACTIVITY_TYPE_ID), activityType.getId());
		return (Activity) activityBean.getList(criteria).get(0);
	}

//	private ActivityType getActivityType(ActivityType activityType)
//			throws ManagerBeanException {
//		if (activityType != null) {
//			IManagerBean activityTypeBean = BeanManager.getManagerBean(ActivityType.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(activityTypeBean
//					.getFieldName(IProjectAlias.ACTIVITY_TYPE_ID), activityType.getId());
//			return (ActivityType) activityTypeBean.getList(criteria).get(0);
//		}
//		return null;
//	}

	@SuppressWarnings("unchecked")
	private Alarm getTaskAlarm(Task task) throws ManagerBeanException {
		IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(alarmBean.getFieldName(IGroupWareAlias.ALARM_STATUS),
				AlarmStatus.PENDING);
		criteria.addEqualExpression(alarmBean.getFieldName(IGroupWareAlias.ALARM_SOURCE),
				AlarmSource.TASK);
		criteria.addEqualExpression(alarmBean.getFieldName(IGroupWareAlias.ALARM_SOURCE_ID), task
				.getId());
		List alarmList = alarmBean.getList(criteria);
		if (alarmList.size() > 0) {
			return (Alarm) alarmList.get(0);
		}
		return null;
	}

	/*
	 * CALCULATE METHODS
	 */

	public Date calculateDueDate(Campaign campaign, DateReference reference, int days) {
		Date dueDate = new Date();
		if (reference.equals(DateReference.FROM_START_DATE)) {
			dueDate = campaign.getStartDate();
		} else if (reference.equals(DateReference.BEFORE_END_DATE)) {
			dueDate = campaign.getEndDate();
			days = 0 - days;
		}

		Calendar dueCalendar = new GregorianCalendar();
		dueCalendar.setTime(dueDate);
		dueCalendar.add(Calendar.DATE, days);
		return dueCalendar.getTime();
	}

	private Date calculateAlertDate(Date dueDate, int alertDays) {
		Calendar alertCalendar = new GregorianCalendar();
		alertCalendar.setTime(dueDate);
		alertCalendar.add(Calendar.DATE, 0 - alertDays);
		return alertCalendar.getTime();
	}

	/*
	 * ADD METHODS
	 */

	private Task addTask(Task task) throws ManagerBeanException {
		IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
		return (Task) taskBean.insert(task);
	}

	private ActivityProcess addActivityProcess(ActivityProcess activityProcess)
			throws ManagerBeanException {
		IManagerBean activityProcessBean = BeanManager.getManagerBean(ActivityProcess.class);
		return (ActivityProcess) activityProcessBean.insert(activityProcess);
	}

	private Alarm addAlarm(Alarm alarm) throws ManagerBeanException {
		IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
		return (Alarm) alarmBean.insert(alarm);
	}

	/*
	 * UPDATE METHODS
	 */

	private Task updateTask(Task task) throws ManagerBeanException {
		IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
		return (Task) taskBean.update(task);
	}

	private Alarm updateAlarm(Alarm alarm) throws ManagerBeanException {
		IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
		return (Alarm) alarmBean.update(alarm);
	}

	@SuppressWarnings("unchecked")
	public boolean hasTransitions(Task task) throws ManagerBeanException {
		ActivityProcess ap = getCurrentActivityProcess(task);
		CampaignDossier cd = new CampaignDossier();
		cd.setCampaign(ap.getCampaign());
		cd.setDossier(task.getDossier());
		ProcessDetail pd = getCurrentProcessDetail(cd);
		IManagerBean bean = BeanManager.getManagerBean(ProcessDetailTransition.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean
				.getFieldName(ICampaignAlias.PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL_ID), pd
				.getId());
		List list = bean.getList(criteria);
		return (list != null && list.size()>0);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcessDetailTransition> getTransitions(Task task) throws ManagerBeanException {
		ActivityProcess ap = getCurrentActivityProcess(task);
		CampaignDossier cd = new CampaignDossier();
		cd.setCampaign(ap.getCampaign());
		cd.setDossier(task.getDossier());
		ProcessDetail pd = getCurrentProcessDetail(cd);
		IManagerBean bean = BeanManager.getManagerBean(ProcessDetailTransition.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean
				.getFieldName(ICampaignAlias.PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL_ID), pd
				.getId());
		List list = bean.getList(criteria);
		return list;
	}
		

}
