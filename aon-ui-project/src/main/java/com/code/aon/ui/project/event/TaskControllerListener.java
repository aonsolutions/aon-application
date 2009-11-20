package com.code.aon.ui.project.event;

import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.customer.Customer;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.project.Task;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.TaskSource;
import com.code.aon.project.enumeration.TaskStatus;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.role.RoleManager;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.project.controller.ITaskController;
import com.code.aon.ui.project.controller.TaskController;
import com.code.aon.ui.util.AonUtil;

public class TaskControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(TaskControllerListener.class.getName());

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
		controller.setAllMembers(false);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		TaskController controller = (TaskController) event.getController();
		Task task = (Task) controller.getTo();
		controller.prepareForInsert(task);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		TaskController controller = (TaskController) event.getController();
		if (!controller.isFreeTask() && !controller.isMyTask()) {
			RoleManager role = AonUtil.getRoleManager();
			if (role != null && !role.isAdmin() && !role.isTaskMonitor()) {
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
			if (task.getDossier() != null) {
//				controller.setCustomer(task.getDossier().getCustomer());
				controller.loadDossiers(task.getDossier().getCustomer().getId());
				controller.loadActivities(task.getDossier().getId());
			} else {
				controller.setDossiers(null);
				controller.setActivities(null);
			}
			controller.loadUsers(task.getWorkGroup().getId());
			controller.setRichEditor(false);
			controller.setAllMembers(false);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Task from Task Model", e);
		}
	}

	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		TaskController taskController = (TaskController) event.getController();
		taskController.setCustomer(initializeCustomer());
		taskController.setRichEditor(false);
		taskController.setAllMembers(false);
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