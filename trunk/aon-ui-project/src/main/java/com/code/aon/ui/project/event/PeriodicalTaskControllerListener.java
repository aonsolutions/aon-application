package com.code.aon.ui.project.event;

import java.util.Iterator;
import java.util.LinkedList;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.project.Activity;
import com.code.aon.project.Dossier;
import com.code.aon.project.PeriodicalTask;
import com.code.aon.project.Task;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.TaskSource;
import com.code.aon.project.enumeration.TaskStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.project.controller.PeriodicalTaskController;

public class PeriodicalTaskControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			User user = UserUtils.getInstance().getLoggedUser();
			PeriodicalTaskController periodTaskController = (PeriodicalTaskController) event.getController();
			Criteria criteria = periodTaskController.getCriteria();
			criteria.addEqualExpression(periodTaskController.getFieldName(IProjectAlias.PERIODICAL_TASK_OWNER_ID) , user.getId());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before Model initialized", e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			PeriodicalTaskController periodicalTaskController = (PeriodicalTaskController)event.getController();
			PeriodicalTask periodTask = (PeriodicalTask)periodicalTaskController.getTo();
			User user = UserUtils.getInstance().getLoggedUser();
			Task task = periodTask.getTask(); 
			periodTask.setOwner(user);
			periodTask.setNextDate(periodicalTaskController.addPeriodToDate(periodTask, periodTask.getTask().getStartDate()));
			task.setSender(user);
			task.setSource(TaskSource.PERIODICAL);
			task.setStatus(TaskStatus.PENDING);
			ensureTask(task);
			IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
			periodTask.setTask((Task)taskBean.insert(task));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error inserting related Task", e);
		}
	}
	
	private void ensureTask(Task task) {
		if (task.getDossier() != null && task.getDossier().getId() == null) {
			task.setDossier(null);
		}
		if (task.getActivity() != null && task.getActivity().getId() == null) {
			task.setActivity(null);
		}
		if (task.getUser() != null && task.getUser().getId() == null) {
			task.setUser(null);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		PeriodicalTask periodTask = (PeriodicalTask)event.getController().getTo();
		if(periodTask.getTask().getDossier() != null && periodTask.getTask().getDossier().getId() != null){
			periodTask.getTask().setDossier(obtainDossier(periodTask.getTask().getDossier().getId()));
		}
		if(periodTask.getTask().getActivity() != null && periodTask.getTask().getActivity().getId() != null){
			periodTask.getTask().setActivity(obtainActivity(periodTask.getTask().getActivity().getId()));
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			PeriodicalTaskController periodicalTaskController = (PeriodicalTaskController)event.getController();
			PeriodicalTask periodTask = (PeriodicalTask)periodicalTaskController.getTo();
			periodTask.setNextDate(periodicalTaskController.addPeriodToDate(periodTask, periodTask.getTask().getStartDate()));
			Task task = periodTask.getTask(); 
			ensureTask(task);
			IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
			periodTask.setTask((Task)taskBean.update(task));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error updating related Task", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Dossier obtainDossier(Integer id) throws ControllerListenerException {
		try {
			IManagerBean dossierBean = BeanManager.getManagerBean(Dossier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(dossierBean.getFieldName(IProjectAlias.DOSSIER_ID), id);
			Iterator iter = dossierBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Dossier)iter.next();
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error obtaining dossier with id= " + id, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Activity obtainActivity(Integer id) throws ControllerListenerException {
		try {
			IManagerBean activityBean = BeanManager.getManagerBean(Activity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_ID), id);
			Iterator iter = activityBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Activity)iter.next();
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error obtaining activity with id= " + id, e);
		}
		return null;
	}
	
    @Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
    	PeriodicalTaskController controller = (PeriodicalTaskController) event.getController();

        controller.setDossiers(new LinkedList<SelectItem>());
        controller.setActivities(new LinkedList<SelectItem>());
    }
	
}