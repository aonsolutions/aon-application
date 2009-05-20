package com.code.aon.ui.project.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.project.Task;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.AlarmController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.project.controller.TaskController;
import com.code.aon.ui.util.AonUtil;

public class TaskAlarmController extends AlarmController {
	
	private static final Logger LOGGER = Logger.getLogger(TaskAlarmController.class.getName());
	
	private static final String TASK_CONTROLLER_NAME = "task";
	
	@SuppressWarnings("unused")
    public void onSearch(MenuEvent event) {
        super.onSearch(null);
    }

	@Override
	@SuppressWarnings("unchecked")
	public void initializeModel() {
		ControllerEvent evt = new ControllerEvent(this);
		try {
			controllerListenerSupport.fireBeforeModelInitialized(evt);
			List list = null;
			list = search(0, getPageLimit());
		    model = new PageDataModel(this, getPageLimit());
		    model.setWrappedData(list);
		    ((PageDataModel)model).resize(list.size());
			controllerListenerSupport.fireAfterModelInitialized(evt);
		} catch (ControllerListenerException e) {
			LOGGER.severe(">>>> initializeModel " + e.getMessage());
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> initializeModel " + e.getMessage());
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);
		}	
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ITransferObject> search(int start, int count) throws ManagerBeanException {
		IManagerBean alarmBean = BeanManager.getManagerBean(Alarm.class);
		Criteria criteria = getCriteria();
		criteria.addEqualExpression(alarmBean.getFieldName(IGroupWareAlias.ALARM_STATUS), AlarmStatus.PENDING);
		criteria.addLessThanOrEqualExpression(alarmBean.getFieldName(IGroupWareAlias.ALARM_ALARM_DATE), new Date());
		TaskController taskController = (TaskController)AonUtil.getController(TASK_CONTROLLER_NAME);
		Expression taskExpr = null;
		Iterator iter = ((List)taskController.getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			Task task = (Task)iter.next();
			taskExpr = ExpressionUtilities.getOrExpression(taskExpr, ExpressionUtilities.getEqualExpression(alarmBean.getFieldName(IGroupWareAlias.ALARM_SOURCE_ID), task.getId()));
		}
		if(taskExpr != null){
			criteria.addExpression(taskExpr);
			return alarmBean.getList(criteria);
		}
		return new LinkedList<ITransferObject>();
	}
	
	@SuppressWarnings("unchecked")
	public String getCurrentAlarmCustomer() throws ManagerBeanException{
		String customer = "";
		Alarm alarm = (Alarm)this.getModel().getRowData();
		if(alarm.getSource().equals(AlarmSource.TASK)){
			IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taskBean.getFieldName(IProjectAlias.TASK_ID), alarm.getSourceId());
			Iterator iter = taskBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Task task = (Task)iter.next();
				customer = task.getDossier().getCustomer().getRegistry().getName() + " " + task.getDossier().getCustomer().getRegistry().getSurname();
			}
		}
		return customer;
	}
}