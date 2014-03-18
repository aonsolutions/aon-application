package com.code.aon.groupware.task;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.config.User;
import com.code.aon.config.WorkGroup;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.Campaign;
import com.code.aon.groupware.CampaignProject;
import com.code.aon.groupware.ProcessDetail;
import com.code.aon.groupware.ProcessDetailTransition;
import com.code.aon.groupware.ProcessTask;
import com.code.aon.groupware.Task;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.CampaignStatus;
import com.code.aon.groupware.enumeration.DateReference;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.groupware.enumeration.TaskPeriod;
import com.code.aon.groupware.enumeration.TaskSource;
import com.code.aon.groupware.enumeration.TaskStatus;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class TaskManager implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TaskManager.class);
	
	private IManagerBean bean;
	
	private IManagerBean getManagerBean() throws ManagerBeanException {
		if (bean == null) {
			bean = BeanManager.getManagerBean(Task.class); 
		}
		return bean; 
	}
		
	public Task startTask(TaskHolder taskHolder, Task task) throws ManagerBeanException {
		Task dbTask = (Task) getManagerBean().get(task.getId());
		if (dbTask.isMine(taskHolder)) {
			dbTask.setStatus(TaskStatus.IN_PROGRESS);
		}
		return updateTask(dbTask);
	}
	public Task stopTask(TaskHolder taskHolder, Task task) throws ManagerBeanException {
		Task dbTask = (Task) getManagerBean().get(task.getId());
		if (dbTask.isMine(taskHolder)) {
			dbTask.setStatus(TaskStatus.PENDING);
		}
		return updateTask(dbTask);
	}
	public Task reopenTask(TaskHolder taskHolder, Task task) throws ManagerBeanException {
		Task dbTask = (Task) getManagerBean().get(task.getId());
		if (dbTask.isMine(taskHolder)) {
			dbTask.setStatus(TaskStatus.PENDING);
		}
		return updateTask(dbTask);
	}
	public Task assumeTask(TaskHolder taskHolder,Task task) throws ManagerBeanException {
		Task dbTask = (Task) getManagerBean().get(task.getId());
		if (dbTask.isUnassigned()) {
			dbTask.setTaskHolder(taskHolder);
			dbTask.setStatus(TaskStatus.IN_PROGRESS);
			return updateTask(dbTask);
		} else {
			String msg = "No se puede Asumir la Tarea. Ha sido asumida por el usuario " + dbTask.getTaskHolder().getRegistry().getFullName();
			LOGGER.error(msg);
			throw new ManagerBeanException(msg);
		}
	}

	public Task releaseTask(TaskHolder taskHolder, Task task) throws ManagerBeanException {
		Task dbTask = (Task) getManagerBean().get(task.getId());
		if (dbTask.isMine(taskHolder)) {
			dbTask.setTaskHolder(null);
			return updateTask(dbTask);
		} else {
			String msg = "No se puede Liberar la Tarea. Pertenece al usuario " + dbTask.getTaskHolder().getRegistry().getFullName();
			LOGGER.error(msg);
			throw new ManagerBeanException(msg);
		}
	}
	
	public boolean hasTransitions(Task task) throws ManagerBeanException {
		ProcessTask ap = getCurrentProcessTask(task);
		ProcessDetail pd = ap.getProcessDetail();
		IManagerBean bean = BeanManager.getManagerBean(ProcessDetailTransition.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean
				.getFieldName(IEntityAlias.PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL_ID), pd.getId());
		criteria.setSkipDomainFilter(true);
		List<?> list = bean.getList(criteria);
		return (list != null && list.size()>0);
	}

	@SuppressWarnings("unchecked")
	public List<ProcessDetailTransition> getTransitions(Task task) throws ManagerBeanException {
		ProcessTask processsTask = getCurrentProcessTask(task);
		ProcessDetail pd = processsTask.getProcessDetail();
		IManagerBean bean = BeanManager.getManagerBean(ProcessDetailTransition.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_DETAIL_TRANSITION_PROCESS_DETAIL_ID), pd.getId());
		criteria.setSkipDomainFilter(true);
		List<?> list = bean.getList(criteria);
		return (List<ProcessDetailTransition>) list;
	}
	
	private Task updateTask(Task task) throws ManagerBeanException {
		// inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName( Task.class.getName() );
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
				task = (Task) getManagerBean().update(task);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
				return task;
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				String msg = "Error al modificar la tarea. (" + e.getMessage() + ")";
				LOGGER.error(msg, e);
				throw new ManagerBeanException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	private ProcessTask getCurrentProcessTask(Task task) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProcessTask.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_TASK_TASK_ID), task.getId());
		criteria.setSkipDomainFilter(true);
		Iterator<?> iterator = bean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			ProcessTask processTask = (ProcessTask) iterator.next();
			return processTask;
		}
		return null;
	}

	public Task finishTask(TaskHolder taskHolder, Task task, ProcessDetailTransition pdt) throws ManagerBeanException {
		if (task.isMine(taskHolder)|| task.isUnassigned()) {
			// inicio transaccion
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName( Task.class.getName() );
			try {
				try {
					HibernateUtil.setBeginTransaction(false);
					HibernateUtil.setCloseSession(false);
					HibernateUtil.beginTransaction(sessionName);
					// operaciones de la transaccion
					task.setEndDate(new Date());
					task.setStatus(TaskStatus.FINISHED);
					task.setTaskHolder(taskHolder);
					task = (Task) getManagerBean().update(task);
					if (task.isSourceProcess()) {
						finishTaskAlarm(task,taskHolder.getUser());
						createNextTask(task,pdt);
					} else {
						if (task.isRepeatable()) {
							repeatTask(task);
						}
						insertRelatedAlarm(task);
					}
					// FIN operaciones de la transaccion
					HibernateUtil.getSession(sessionName).flush();
					HibernateUtil.commitTransaction(sessionName);
					return task;
				} catch (Exception e) {
					try {
						HibernateUtil.rollbackTransaction(sessionName);
						LOGGER.info("Transaction rollback");
					} catch (DAOException daoe) {
						String msg = "Unable to rollback transaction!";
						LOGGER.error(msg, e);
					}
					String msg = "Error al modificar la tarea. (" + e.getMessage() + ")";
					LOGGER.error(msg, e);
					throw new ManagerBeanException(msg);
				} finally {
					HibernateUtil.closeSession(sessionName);
				}
			} finally {
				HibernateUtil.setCloseSession(mustCloseSession);
				HibernateUtil.setBeginTransaction(mustBeginTransaction);
			}
		} else {
			String msg = "No se puede Finalizar la Tarea. Ha sido asumida por otro Usuario.";
			LOGGER.error(msg);
			throw new ManagerBeanException(msg);
		}
	}
	
	private Alarm finishTaskAlarm(Task task,User user) throws ManagerBeanException {
		Alarm taskAlarm = getTaskAlarm(task);
		if (taskAlarm != null) {
			taskAlarm.setStatus(AlarmStatus.FINISHED);
			taskAlarm.setUser(user);
			IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
			return (Alarm) alarmBean.update(taskAlarm);
		}
		return null;
	}
	
	private Alarm getTaskAlarm(Task task) throws ManagerBeanException {
		IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(alarmBean.getFieldName(IEntityAlias.ALARM_STATUS),AlarmStatus.PENDING);
		criteria.addEqualExpression(alarmBean.getFieldName(IEntityAlias.ALARM_SOURCE),AlarmSource.TASK);
		criteria.addEqualExpression(alarmBean.getFieldName(IEntityAlias.ALARM_SOURCE_ID), task.getId());
		List<?> alarmList = alarmBean.getList(criteria);
		if (alarmList.size() > 0) {
			return (Alarm) alarmList.get(0);
		}
		return null;
	}
	
	private void createNextTask(Task task,ProcessDetailTransition pdt) throws ManagerBeanException {
		ProcessTask processTask = getCurrentProcessTask(task);
		if (processTask != null) {
			ProcessDetail processDetail;
			if (pdt != null && pdt.getId() != null) {
				processDetail = pdt.getNextProcessDetail();
			} else {
				processDetail = getNextProcessDetail(processTask.getProcessDetail());
			}
			addProcessTask(processTask.getTask(),processTask.getCampaign(),processDetail,null);
		}
	}
	
	public void addProcessTask(Task previousTask,Campaign campaign,ProcessDetail processDetail, CampaignProject campaignProject) throws ManagerBeanException {
		if (processDetail != null) {
			IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
			IManagerBean processTaskBean = BeanManager.getManagerBean(ProcessTask.class);
			Task task = new Task();
			StringBuffer buf = new StringBuffer();
			buf.append(processDetail.getDescription());
			if ( campaign != null) {
				buf.append(" [");
				buf.append(campaign.getDescription());
				buf.append("]");
			}
			task.setDescription(buf.toString());
			task.setStartDate(new Date());
			task.setDueDate(calculateDueDate(campaign,processDetail,task.getStartDate()));
			task.setStatus(TaskStatus.PENDING);
			task.setPercent(0);
			WorkGroup wg = processDetail.getWorkgroup(); 
			if (wg == null && campaign != null) {
				wg = campaign.getWorkGroup(); 
			}
			task.setWorkGroup(wg);
			task.setSource(TaskSource.PROCESS);
			Project project = campaignProject != null?campaignProject.getProject():previousTask.getProject();
			task.setProject(project);
			task.setRegistry(project.getRegistry());
			task.setActivityType(previousTask==null?null:previousTask.getActivityType());
			task.setRepeatPeriod(TaskPeriod.NONE);
			task.setComments( previousTask==null?null:previousTask.getComments() );
			task.setSender( previousTask==null?null:previousTask.getSender() );
			task.setPriority( processDetail.getPriority());
			if (task.getPriority() == null) {
				task.setPriority( Priority.NORMAL );	
			}	
			task = (Task) taskBean.insert(task);

			ProcessTask newProcessTask = new ProcessTask();
			newProcessTask.setCampaign(campaign);
			newProcessTask.setProcessDetail(processDetail);
			newProcessTask.setTask(task);
			processTaskBean.insert(newProcessTask);

		} else {
			if ( campaign != null) {
				finishCampaignIfNeeded( campaign );	
			}
			
		}
	}

	private ProcessDetail getNextProcessDetail(ProcessDetail processDetail) throws ManagerBeanException {
		IManagerBean processDetailBean = BeanManager.getManagerBean(ProcessDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(processDetailBean.getFieldName(IEntityAlias.PROCESS_DETAIL_PROCESS_ID), processDetail.getProcess().getId());
		criteria.addEqualExpression(processDetailBean.getFieldName(IEntityAlias.PROCESS_DETAIL_ACTIVE), true);
		criteria.addGreaterThanExpression(processDetailBean.getFieldName(IEntityAlias.PROCESS_DETAIL_POSITION), processDetail.getPosition());
		criteria.addOrder(processDetailBean.getFieldName(IEntityAlias.PROCESS_DETAIL_POSITION));
		criteria.setSkipDomainFilter(true);
		List<ITransferObject>  list = processDetailBean.getList(criteria);
		if (list.size() > 0) {
			return (ProcessDetail) list.get(0);
		}
		return null;
	}
	
	private Date calculateDueDate(Campaign campaign,ProcessDetail processDetail,Date startDate) {
		DateReference reference = processDetail.getDateReference();
		int days = 	processDetail.getDays();
		Date dueDate = startDate;
		if (reference != null && campaign != null) {
			if (reference == DateReference.FROM_START_DATE) {
				dueDate = campaign.getStartDate();
			} else if (reference == DateReference.BEFORE_END_DATE) {
				dueDate = campaign.getEndDate();
				days = 0 - days;
			}
		}
		Calendar dueCalendar = new GregorianCalendar();
		dueCalendar.setTime(dueDate);
		dueCalendar.add(Calendar.DATE, days);
		dueDate = dueCalendar.getTime();
		return dueDate;
	}
	
	private void finishCampaignIfNeeded(Campaign campaign) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProcessTask.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_TASK_CAMPAIGN_ID), campaign.getId());
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_TASK_TASK_STATUS), TaskStatus.DELETED);
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_TASK_TASK_STATUS), TaskStatus.FINISHED);
		List<ITransferObject> list = bean.getList(criteria);
		if (list != null && list.size() > 0) {
			// La campaña todavía tiene tareas activas.
		} else {
			IManagerBean campaignBean  = BeanManager.getManagerBean(Campaign.class);	
			campaign.setStatus(CampaignStatus.FINISHED);
			campaignBean.update(campaign);
		}
	}
	private Task repeatTask(Task task) throws ManagerBeanException {
		Task newTask = new Task();
		newTask.setActivityType(task.getActivityType());
		newTask.setComments(task.getComments());
		newTask.setDescription(task.getDescription());
		newTask.setProject(task.getProject());
		newTask.setRegistry(task.getRegistry());
		newTask.setPercent(0);
		newTask.setPriority(task.getPriority());
		newTask.setSender(task.getSender());
		newTask.setSource(task.getSource());
		newTask.setRepeatPeriod(task.getRepeatPeriod());
		newTask.setStatus(TaskStatus.PENDING);
		newTask.setTaskHolder(task.getTaskHolder());
		newTask.setWorkGroup(task.getWorkGroup());
		newTask.setDomain(task.getDomain());
		Date startDate = task.getStartDate();
		Date dueDate = task.getDueDate();
		// Se truncan las horas, minutos, segundos, porque lo que nos interesa
		// averiguar es cuánto plazo tenía esta tarea en dias.
		startDate = DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
		dueDate = DateUtils.truncate(dueDate, Calendar.DAY_OF_MONTH);
		// Se calculan los dias de diferencia.
		int days = (int) (dueDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
		// Se suma el periodo indicado.
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.add(task.getRepeatPeriod().getField(), task.getRepeatPeriod().getValue());
		Date newStartDate = c.getTime();
		// Se asigna la nueva fecha.
		newTask.setStartDate(newStartDate);
		// Se calcula y asigna la nueva fecha de vencimiento.
		c = Calendar.getInstance();
		c.setTime(newStartDate);
		c.add(Calendar.DAY_OF_MONTH, days);
		newTask.setDueDate(c.getTime());
		IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
		return (Task) taskBean.insert(newTask);
	}

	private void insertRelatedAlarm(Task task) throws ManagerBeanException {
		if (task.getSender() != null) {
			Integer senderId = task.getSender().getId();
			Integer userId = (task.getTaskHolder() != null) ? task.getTaskHolder().getId() : null;
			if (!userId.equals(senderId)) {
				IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
				Alarm alarm = new Alarm();
				alarm.setAlarmDate(task.getEndDate());
				alarm.setUser(task.getSender().getUser());
				alarm.setSource(AlarmSource.NOTICE);
				alarm.setSourceId(task.getId());
				alarm.setStatus(AlarmStatus.PENDING);
				alarm.setPriority(Priority.NONE);
				String d = task.getProject() == null?"":(task.getProject().getName() + " - ");
				alarm.setDescription("Task " + AlarmStatus.FINISHED + ": " + d  + task.getDescription());
				alarmBean.insert(alarm);
			}
		}
	}

	public Task finishCampaignTask(CampaignProject cp, TaskHolder taskHolder) throws ManagerBeanException {
		Task currentTask = getCurrentTask(cp);
		if (currentTask != null) {
			currentTask.setEndDate(new Date());
			currentTask.setStatus(TaskStatus.FINISHED);
			currentTask.setTaskHolder( taskHolder );
			currentTask = (Task) getManagerBean().update(currentTask);
			finishTaskAlarm(currentTask, taskHolder.getUser() );
		}
		return currentTask;
	}

	public Task getCurrentTask(CampaignProject cp) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProcessTask.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_TASK_CAMPAIGN_ID), cp.getCampaign().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_TASK_TASK_PROJECT_ID), cp.getProject().getId());
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_TASK_TASK_STATUS), TaskStatus.DELETED);
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_TASK_TASK_STATUS), TaskStatus.FINISHED);
		List<ITransferObject> list = bean.getList(criteria);
		if (list != null && list.size() > 0) {
			ProcessTask processTask = (ProcessTask) list.get(0);
			return processTask.getTask();
		}
		return null;
	}

	public void removeCampaignTask(CampaignProject cp, TaskHolder taskHolder) throws ManagerBeanException {
		Task currentTask = getCurrentTask(cp);
		if (currentTask != null) {
			currentTask.setEndDate(new Date());
			currentTask.setStatus(TaskStatus.DELETED);
			currentTask.setTaskHolder(taskHolder);
			updateTask(currentTask);
			finishTaskAlarm(currentTask, taskHolder.getUser() );
		}
	}
	
	public ProcessTask getCurrentProcessTask(CampaignProject cp) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProcessTask.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_TASK_CAMPAIGN_ID), cp.getCampaign().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_TASK_TASK_PROJECT_ID), cp.getProject().getId());
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_TASK_TASK_STATUS), TaskStatus.DELETED);
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_TASK_TASK_STATUS), TaskStatus.FINISHED);
		List<ITransferObject> list = bean.getList(criteria);
		if (list != null && list.size() > 0) {
			ProcessTask processTask = (ProcessTask) list.get(0);
			return processTask;
		}
		return null;
	}
}
