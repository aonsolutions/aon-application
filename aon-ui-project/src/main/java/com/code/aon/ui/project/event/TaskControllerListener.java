package com.code.aon.ui.project.event;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.customer.Customer;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.project.Activity;
import com.code.aon.project.Dossier;
import com.code.aon.project.Task;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.TaskSource;
import com.code.aon.project.enumeration.TaskStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.project.controller.ITaskController;
import com.code.aon.ui.project.controller.RoleController;
import com.code.aon.ui.project.controller.TaskController;
import com.code.aon.ui.util.AonUtil;

public class TaskControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(TaskControllerListener.class.getName());
	private static final String ROLE_CONTROLLER = "role";

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		TaskController controller = (TaskController) event.getController();
		controller.resetChecks();
		try {
			if (!controller.isMonitor()) {
				User user = UserUtils.getInstance().getLoggedUser();

				Expression userExpr = ExpressionUtilities.getEqualExpression(
						TaskController.USER_ALIAS, user.getId());
				Expression workGroupExpr = UserUtils.obtainUserWorkGroupsExpr(user,
						TaskController.WORKGROUP_ALIAS);
				Expression groupExpr = ExpressionUtilities
						.getNullExpression(TaskController.USER_ALIAS);
				workGroupExpr = ExpressionUtilities.getAndExpression(workGroupExpr, groupExpr);
				controller.getCriteria().addExpression(
						ExpressionUtilities.getOrExpression(userExpr, workGroupExpr));
			}
			controller.completeCriteria();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error initializing Task Model", e);
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		TaskController controller = (TaskController) event.getController();
		Task task = (Task) controller.getTo();
		task.setStartDate(new Date());
		task.setStatus(TaskStatus.PENDING);
		task.setPercent(0);
		task.setSource(TaskSource.MANUAL);
		task.setPriority(Priority.NORMAL);

		controller.setCustomer(initializeCustomer());
		controller.setDossiers(null);
		controller.setActivities(null);
		controller.setUsers(new LinkedList<SelectItem>());
		controller.setRichEditor(false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		TaskController controller = (TaskController) event.getController();
		Task to = (Task) controller.getTo();
		if (!controller.isMyTask()) {
			to.setSource(TaskSource.ASSIGNED);
			to.setSender(UserUtils.getInstance().getLoggedUser());
		}

		try {
			if (to.getDossier() != null && to.getDossier().getId() != null) {
				IManagerBean dossierBean = BeanManager.getManagerBean(Dossier.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(dossierBean.getFieldName(IProjectAlias.DOSSIER_ID), to
						.getDossier().getId());
				List dossierList = dossierBean.getList(criteria);
				if (dossierList.size() > 0) {
					to.setDossier((Dossier) dossierList.get(0));
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining dossier from task with id= " + to.getId(), e);
		}

		try {
			if (to.getActivity() != null && to.getActivity().getId() != null) {
				IManagerBean activityBean = BeanManager.getManagerBean(Activity.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_ID),
						to.getActivity().getId());
				List activityList = activityBean.getList(criteria);
				if (activityList.size() > 0) {
					to.setActivity((Activity) activityList.get(0));
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER
					.log(Level.SEVERE, "Error obtaining activity from task with id= " + to.getId(),
							e);
		}

		Date startDate = ((Task) event.getController().getTo()).getStartDate();
		Date dueDate = ((Task) event.getController().getTo()).getDueDate();
		if (dueDate.compareTo(startDate) < 0) {
			throw new ControllerListenerException(
					"Fecha Inicio no puede ser posterior a Fecha Vencimiento.");
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		TaskController controller = (TaskController) event.getController();
		if (!controller.isFreeTask() && !controller.isMyTask()) {
			RoleController role = (RoleController) AonUtil.getRegisteredBean(ROLE_CONTROLLER);
			if (role != null && !role.isManagerRole() && !role.isMonitorRole()) {
				throw new ControllerListenerException(
						"No se puede Modificar la Tarea. Ha sido asumida por otro Usuario.");
			}
		}
	}

	@Override
	public void beforeBeanSelected(ControllerEvent event) throws ControllerListenerException {
		TaskController controller = (TaskController) event.getController();
		try {
			Task task = (Task) controller.getModel().getRowData();
			if (task != null && !controller.isMonitor() && !controller.isFreeTask(task)
					&& !controller.isMyTask(task)) {
				throw new ControllerListenerException(
						"No se puede Editar la Tarea. Ha sido asumida por otro Usuario.");
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Task from Task Model", e);
		}
		controller.setRichEditor(false);
	}

	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		TaskController taskController = (TaskController) event.getController();
		taskController.setCustomer(initializeCustomer());
		taskController.setRichEditor(false);
	}

	@Override
	public void beforeBeanReset(ControllerEvent event) throws ControllerListenerException {
		try {
			ITaskController monitorController = (ITaskController) event.getController();
			if (monitorController.getCustomer() != null
					&& monitorController.getCustomer().getId() != null) {
				monitorController.getCriteria().addEqualExpression(
						monitorController.getFieldName(IProjectAlias.TASK_CUSTOMER_ID),
						monitorController.getCustomer().getId());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error adding criteria beforeBeanReset", e);
		}
	}

	private Customer initializeCustomer() {
		Customer customer = new Customer();
		customer.setRegistry(new Registry());
		return customer;
	}
}