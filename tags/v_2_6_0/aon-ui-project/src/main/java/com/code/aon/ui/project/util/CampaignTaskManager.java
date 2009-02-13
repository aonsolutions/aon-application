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
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.campaign.enumeration.DateReference;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
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
import com.code.aon.project.enumeration.TaskSource;
import com.code.aon.project.enumeration.TaskStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;

public class CampaignTaskManager {

    /*
        PUBLIC METHODS
     */

    public static void addCampaignTask(CampaignDossier campaignDossier, int position) throws ManagerBeanException {
        ProcessDetail processDetail = getProcessDetail(campaignDossier.getCampaign().getProcess(), position);
        ActivityType activityType = getActivityType(campaignDossier.getCampaign().getActivityType());
        if (processDetail != null) {
            Activity activity = getActivity(campaignDossier.getDossier(), activityType);

            Task task = new Task();
            task.setDescription(activityType.getDescription() + " - " + processDetail.getDescription());
            task.setStartDate(new Date());
            task.setDueDate(calculateDueDate(campaignDossier.getCampaign(), processDetail.getDateReference(), processDetail.getDays()));
            task.setPriority(Priority.NONE);
            task.setStatus(TaskStatus.PENDING);
            task.setPercent(0);
            task.setWorkGroup(processDetail.getWorkgroup() != null ? processDetail.getWorkgroup() : activity.getWorkgroup());
            task.setSource(TaskSource.AON_CONSULTANT);
            task.setDossier(campaignDossier.getDossier());
            task.setActivity(activity);
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
                alarm.setAlarmDate(calculateAlertDate(task.getDueDate(), processDetail.getAlertDays()));
                alarm.setStatus(AlarmStatus.PENDING);
                alarm.setSource(AlarmSource.TASK);
                alarm.setSourceId(task.getId());
                alarm.setPriority(Priority.NONE);
                addAlarm(alarm);
            }
        }
    }

    public static void finishCampaignTask(CampaignDossier campaignDossier) throws ManagerBeanException {
        Task currentTask = CampaignTaskManager.getCurrentTask(campaignDossier);
        if (currentTask != null) {
            currentTask.setEndDate(new Date());
            currentTask.setStatus(TaskStatus.FINISHED);
            currentTask.setUser(UserUtils.getInstance().getLoggedUser());
            updateTask(currentTask);

            finishTaskAlarm(currentTask);
        }
    }

    public static void removeCampaignTask(CampaignDossier campaignDossier) throws ManagerBeanException {
        Task currentTask = CampaignTaskManager.getCurrentTask(campaignDossier);
        if (currentTask != null) {
            currentTask.setEndDate(new Date());
            currentTask.setStatus(TaskStatus.DELETED);
            currentTask.setUser(UserUtils.getInstance().getLoggedUser());
            updateTask(currentTask);

            finishTaskAlarm(currentTask);
        }
    }

    public static void finishTaskAlarm(Task task) throws ManagerBeanException {
        Alarm taskAlarm = CampaignTaskManager.getTaskAlarm(task);
        if (taskAlarm != null) {
            taskAlarm.setStatus(AlarmStatus.FINISHED);
            taskAlarm.setUser(UserUtils.getInstance().getLoggedUser());
            updateAlarm(taskAlarm);
        }
    }

    @SuppressWarnings("unchecked")
    public static Task getCurrentTask(CampaignDossier campaignDossier) throws ManagerBeanException {
        List activityProcesses = getActivityProcesses(campaignDossier);
        Iterator iterator = activityProcesses.iterator();
        while (iterator.hasNext()) {
            ActivityProcess activityProcess = (ActivityProcess)iterator.next();
            Task task = activityProcess.getTask();
            if (task.getStatus().equals(TaskStatus.PENDING) || task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                return task;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Task getPreviousTask(Task task) throws ManagerBeanException {
        ActivityProcess currentActivityProcess = getCurrentActivityProcess(task);
        CampaignDossier campaignDossier = new CampaignDossier();
        campaignDossier.setCampaign(currentActivityProcess.getCampaign());
        campaignDossier.setDossier(task.getDossier());

        List activityProcesses = getActivityProcesses(campaignDossier);
        Iterator iterator = activityProcesses.iterator();
        while (iterator.hasNext()) {
            ActivityProcess activityProcess = (ActivityProcess)iterator.next();
            if (currentActivityProcess.getId().intValue() > activityProcess.getId().intValue()) {
                return activityProcess.getTask();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static ProcessDetail getCurrentProcessDetail(CampaignDossier campaignDossier) throws ManagerBeanException {
        List activityProcesses = getActivityProcesses(campaignDossier);
        Iterator iterator = activityProcesses.iterator();
        while (iterator.hasNext()) {
            ActivityProcess activityProcess = (ActivityProcess)iterator.next();
            Task task = activityProcess.getTask();
            if (task.getStatus().equals(TaskStatus.PENDING) || task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                return activityProcess.getProcessDetail();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Integer getProcessDetailPosition(ProcessDetail processDetail) throws ManagerBeanException {
        if (processDetail != null && processDetail.getId() != null) {
            IManagerBean processDetailBean = BeanManager.getManagerBean(ProcessDetail.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(processDetailBean.getFieldName(ICampaignAlias.PROCESS_DETAIL_ID), processDetail.getId());
            List processDetailList = processDetailBean.getList(criteria);
            if (processDetailList.size() > 0) {
                return new Integer(((ProcessDetail)processDetailList.get(0)).getPosition());
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static ActivityProcess getCurrentActivityProcess(Task task) throws ManagerBeanException {
        IManagerBean activityProcessBean = BeanManager.getManagerBean(ActivityProcess.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(activityProcessBean.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_TASK_ID), task.getId());
        Iterator iterator = activityProcessBean.getList(criteria).iterator();
        if (iterator.hasNext()) {
            ActivityProcess activityProcess = (ActivityProcess)iterator.next();
            return activityProcess;
        }
        return null;
    }

    /*
        GETTER METHODS
     */

    @SuppressWarnings("unchecked")
    private static List getActivityProcesses(CampaignDossier campaignDossier) throws ManagerBeanException {
        Campaign campaign = campaignDossier.getCampaign();
        Activity activity = getActivity(campaignDossier.getDossier(), campaign.getActivityType());

        IManagerBean activityProcessBean = BeanManager.getManagerBean(ActivityProcess.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(activityProcessBean.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_CAMPAIGN_ID), campaign.getId());
        criteria.addEqualExpression(activityProcessBean.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_ACTIVITY_ID), activity.getId());
        criteria.addOrder(activityProcessBean.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_ID), false);
        return activityProcessBean.getList(criteria);
    }

    @SuppressWarnings("unchecked")
    private static ProcessDetail getProcessDetail(Process process, int position) throws ManagerBeanException {
        IManagerBean processDetailBean = BeanManager.getManagerBean(ProcessDetail.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(processDetailBean.getFieldName(ICampaignAlias.PROCESS_DETAIL_PROCESS_ID), process.getId());
        criteria.addOrder(processDetailBean.getFieldName(ICampaignAlias.PROCESS_DETAIL_POSITION));
        List processDetailList = processDetailBean.getList(criteria);
        if (processDetailList.size() > position) {
            return (ProcessDetail)processDetailList.get(position);
        }
        return null;
    }

    private static Activity getActivity(Dossier dossier, ActivityType activityType) throws ManagerBeanException {
        IManagerBean activityBean = BeanManager.getManagerBean(Activity.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_DOSSIER_ID), dossier.getId());
        criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_ACTIVITY_TYPE_ID), activityType.getId());
        return (Activity)activityBean.getList(criteria).get(0);
    }

    private static ActivityType getActivityType(ActivityType activityType) throws ManagerBeanException {
        IManagerBean activityTypeBean = BeanManager.getManagerBean(ActivityType.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(activityTypeBean.getFieldName(IProjectAlias.ACTIVITY_TYPE_ID), activityType.getId());
        return (ActivityType)activityTypeBean.getList(criteria).get(0);
    }

    @SuppressWarnings("unchecked")
    private static Alarm getTaskAlarm(Task task) throws ManagerBeanException {
        IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(alarmBean.getFieldName(IGroupWareAlias.ALARM_STATUS), AlarmStatus.PENDING);
        criteria.addEqualExpression(alarmBean.getFieldName(IGroupWareAlias.ALARM_SOURCE), AlarmSource.TASK);
        criteria.addEqualExpression(alarmBean.getFieldName(IGroupWareAlias.ALARM_SOURCE_ID), task.getId());
        List alarmList = alarmBean.getList(criteria);
        if (alarmList.size() > 0) {
            return (Alarm)alarmList.get(0);
        }
        return null;
    }

    /*
        CALCULATE METHODS
     */

    private static Date calculateDueDate(Campaign campaign, DateReference reference, int days) {
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

    private static Date calculateAlertDate(Date dueDate, int alertDays) {
        Calendar alertCalendar = new GregorianCalendar();
        alertCalendar.setTime(dueDate);
        alertCalendar.add(Calendar.DATE, 0 - alertDays);
        return alertCalendar.getTime();
    }

    /*
        ADD METHODS
     */

    private static Task addTask(Task task) throws ManagerBeanException {
        IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
        return (Task)taskBean.insert(task);
    }

    private static ActivityProcess addActivityProcess(ActivityProcess activityProcess) throws ManagerBeanException {
        IManagerBean activityProcessBean = BeanManager.getManagerBean(ActivityProcess.class);
        return (ActivityProcess)activityProcessBean.insert(activityProcess);
    }

    private static Alarm addAlarm(Alarm alarm) throws ManagerBeanException {
        IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
        return (Alarm)alarmBean.insert(alarm);
    }

    /*
        UPDATE METHODS
     */

    private static Task updateTask(Task task) throws ManagerBeanException {
        IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
        return (Task)taskBean.update(task);
    }

    private static Alarm updateAlarm(Alarm alarm) throws ManagerBeanException {
        IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
        return (Alarm)alarmBean.update(alarm);
    }

}
