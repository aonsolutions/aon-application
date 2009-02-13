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
import com.code.aon.ui.project.controller.TaskController;

public class TaskControllerListener extends ControllerAdapter {

    private static final Logger LOGGER = Logger.getLogger(TaskControllerListener.class.getName());

    @Override
    public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
        TaskController controller = (TaskController)event.getController();
        controller.resetChecks();
        try {
            User user = UserUtils.getInstance().getLoggedUser();

            Expression userExpr = ExpressionUtilities.getEqualExpression(controller.getFieldName(IProjectAlias.TASK_USER_ID), user.getId());
            if (controller.getMyStatusExpression() != null) {
                userExpr = ExpressionUtilities.getAndExpression(userExpr, controller.getMyStatusExpression());
            }

            Expression workGroupExpr = UserUtils.obtainUserWorkGroupsExpr(user, controller.getFieldName(IProjectAlias.TASK_WORK_GROUP_ID));
            Expression groupExpr = ExpressionUtilities.getNullExpression(controller.getFieldName(IProjectAlias.TASK_USER_ID));
            workGroupExpr = ExpressionUtilities.getAndExpression(workGroupExpr, groupExpr);
            if (controller.getMyStatusExpression() != null) {
                workGroupExpr = ExpressionUtilities.getAndExpression(workGroupExpr, controller.getMyStatusExpression());
            }

            controller.getCriteria().addExpression(ExpressionUtilities.getOrExpression(userExpr, workGroupExpr));
            controller.getCriteria().addOrder(controller.getManagerBean().getFieldName(IProjectAlias.TASK_USER_ID), false);
            controller.getCriteria().addOrder(controller.getManagerBean().getFieldName(IProjectAlias.TASK_STATUS), false);
            controller.getCriteria().addOrder(controller.getManagerBean().getFieldName(IProjectAlias.TASK_PRIORITY), false);
            controller.getCriteria().addOrder(controller.getManagerBean().getFieldName(IProjectAlias.TASK_DUE_DATE));
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

        controller.setCustomer(initializeCustomer());
        controller.setDossiers(new LinkedList<SelectItem>());
        controller.setActivities(new LinkedList<SelectItem>());
        controller.setUsers(new LinkedList<SelectItem>());
    }

	@Override
	@SuppressWarnings("unchecked")
    public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
    	TaskController controller = (TaskController)event.getController();
    	Task to = (Task)controller.getTo();
        if (!controller.isMyTask()) {
            to.setSource(TaskSource.ASSIGNED);
            to.setSender(UserUtils.getInstance().getLoggedUser());
        }

        try { 
            if (to.getDossier() != null && to.getDossier().getId() != null) {
                IManagerBean dossierBean = BeanManager.getManagerBean(Dossier.class);
                Criteria criteria = new Criteria();
                criteria.addEqualExpression(dossierBean.getFieldName(IProjectAlias.DOSSIER_ID), to.getDossier().getId());
                List dossierList = dossierBean.getList(criteria);
                if (dossierList.size() > 0) {
                    to.setDossier((Dossier)dossierList.get(0));
                }
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining dossier from task with id= " + to.getId(), e);
        }

        try {
            if (to.getActivity() != null && to.getActivity().getId() != null) {
                IManagerBean activityBean = BeanManager.getManagerBean(Activity.class);
                Criteria criteria = new Criteria();
                criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_ID), to.getActivity().getId());
                List activityList = activityBean.getList(criteria);
                if (activityList.size() > 0) {
                    to.setActivity((Activity)activityList.get(0));
                }
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining activity from task with id= " + to.getId(), e);
        }

        Date startDate = ((Task)event.getController().getTo()).getStartDate();
        Date dueDate = ((Task)event.getController().getTo()).getDueDate();
        if (dueDate.compareTo(startDate) < 0) {
            throw new ControllerListenerException("Fecha Inicio no puede ser posterior a Fecha Vencimiento.");
        }
    }

    @Override
    public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        TaskController controller = (TaskController)event.getController();
        if (!controller.isFreeTask() && !controller.isMyTask()) {
            throw new ControllerListenerException("No se puede Modificar la Tarea. Ha sido asumida por otro Usuario.");
        }
    }

    @Override
    public void beforeBeanSelected(ControllerEvent event) throws ControllerListenerException {
        TaskController controller = (TaskController)event.getController();
        try {
            Task task = (Task)controller.getModel().getRowData();
            if (task != null && !controller.isFreeTask(task) && !controller.isMyTask(task)) {
                throw new ControllerListenerException("No se puede Editar la Tarea. Ha sido asumida por otro Usuario.");
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining Task from Task Model", e);
        }
    }

    @Override
    public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
    	ITaskController taskController = (ITaskController)event.getController();
    	taskController.setCustomer(initializeCustomer());
    }
    
	@Override
	public void beforeBeanReset(ControllerEvent event) throws ControllerListenerException {
		try {
			ITaskController monitorController = (ITaskController)event.getController();
			if(monitorController.getCustomer() != null && monitorController.getCustomer().getId() != null){
				monitorController.getCriteria().addEqualExpression(monitorController.getFieldName(IProjectAlias.TASK_CUSTOMER_ID), monitorController.getCustomer().getId());
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