package com.code.aon.ui.groupware.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.Task;
import com.code.aon.ui.common.role.RoleManager;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.TaskController;
import com.code.aon.ui.util.AonUtil;

public class TaskControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		TaskController taskController = (TaskController) event.getController();
		taskController.setAllMembers(false);
		try {
			taskController.loadProjects(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar la lista de proyectos";
			AonUtil.addErrorMessage(msg);
			throw new ControllerListenerException(msg);
		}
		try {
			taskController.loadActivityTypes(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar la lista de tipos de actividades";
			AonUtil.addErrorMessage(msg);
			throw new ControllerListenerException(msg);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		TaskController taskController = (TaskController) event.getController();
		taskController.setAllMembers(false);
		Task task = (Task) taskController.getTo();
		try {
			taskController.loadProjects(task.getRegistry() == null?null:task.getRegistry().getId());
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar la lista de proyectos";
			AonUtil.addErrorMessage(msg);
			throw new ControllerListenerException(msg);
		}
		try {
			taskController.loadActivityTypes(task.getProject() != null && task.getProject().getProjectType() != null? task.getProject().getProjectType().getId() : null);
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar la lista de tipos de actividades";
			AonUtil.addErrorMessage(msg);
			throw new ControllerListenerException(msg);
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		TaskController controller = (TaskController) event.getController();
		Task task = (Task) controller.getTo();
		if (!task.isUnassigned() && !controller.isMyTask()) {
			RoleManager role = AonUtil.getRoleManager();
			if (role != null && !role.isAdmin() && !role.isTaskMonitor()) {
				throw new ControllerListenerException(
						"No se puede Modificar la Tarea. Ha sido asumida por otro Usuario.");
			}
		}
	}
	
}