package com.code.aon.ui.desktop.applications;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.project.Task;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.TaskStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

public class TaskManager implements IServices, IDesktopConstants {

	private ApplicationsManager.App app;
	
	private Integer userId;

	public TaskManager() {
		ApplicationsManager apps = 
			(ApplicationsManager) AonUtil.getRegisteredBean( APPLICATIONS_CONTROLLER_NAME );
		app = apps.getApplication( "aon-task" );
		userId = UserUtils.getInstance().getLoggedUser().getId();
	}

	private List<Integer> getUserWorkgroups() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(UserWorkGroup.class);
		Criteria criteria = new Criteria();
		String user = bean.getFieldName(IConfigAlias.USER_WORK_GROUP_USER_ID);
		criteria.addEqualExpression(user, userId);
		List<Integer> result = new LinkedList<Integer>();
		for( ITransferObject to : bean.getList(criteria) ) {
			UserWorkGroup uwg = (UserWorkGroup) to;
			result.add( uwg.getUser().getId() );
		}
		return result;
	}
	
	private int getUserWorkgroupCount() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Task.class);
		Criteria criteria = new Criteria();
		String user = bean.getFieldName(IProjectAlias.TASK_USER_ID);
		criteria.addNullExpression(user);
		String workGroup = bean.getFieldName(IProjectAlias.TASK_WORK_GROUP_ID);
		Expression expression = null;
		for( Integer id : getUserWorkgroups() ) {
			if ( expression == null ) {
				expression = ExpressionUtilities.getEqualExpression(workGroup, id);				
			} else {
				Expression exp  = ExpressionUtilities.getEqualExpression(workGroup, id);
				expression = ExpressionUtilities.getOrExpression(expression, exp);
			}
		}
		if ( expression != null ) {
			criteria.addExpression(expression);
		}
		return bean.getCount(criteria);
	}
	
	private int getExpiredCount() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Task.class);
		Criteria criteria = new Criteria();
		String user = bean.getFieldName(IProjectAlias.TASK_USER_ID);
		criteria.addEqualExpression(user, userId);
		String statusAlias = bean.getFieldName(IProjectAlias.TASK_STATUS);
		Expression exp1  = ExpressionUtilities.getEqualExpression(statusAlias, TaskStatus.IN_PROGRESS);
		Expression exp2  = ExpressionUtilities.getEqualExpression(statusAlias, TaskStatus.PENDING);
		criteria.addExpression( ExpressionUtilities.getOrExpression(exp1, exp2) );
		String dueDate = bean.getFieldName(IProjectAlias.TASK_DUE_DATE);
		criteria.addLessThanExpression(dueDate, new Date());
		return bean.getCount(criteria);
	}

	private int getTaskCount( TaskStatus status ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Task.class);
		Criteria criteria = new Criteria();
		String user = bean.getFieldName(IProjectAlias.TASK_USER_ID);
		criteria.addEqualExpression(user, userId);
		String statusAlias = bean.getFieldName(IProjectAlias.TASK_STATUS);
		criteria.addEqualExpression(statusAlias, status);
		return bean.getCount(criteria);
	}
	
	public List<TaskInfo> getTaskSummaryModel() throws ManagerBeanException {
		ArrayList<TaskInfo> l = new ArrayList<TaskInfo>();
		int inprogress = getTaskCount( TaskStatus.IN_PROGRESS );
		int pending = getTaskCount( TaskStatus.PENDING );
		int group = getUserWorkgroupCount();
		String desc = TaskStatus.IN_PROGRESS.getName( AonUtil.getCurrentLocale() );
		l.add( new TaskInfo( desc, inprogress + "/" + (inprogress + pending + group) ) );

		String dues = AonUtil.getMessage( "appBundle", "desktop_task_dues" );
		l.add( new TaskInfo( dues, "" + getExpiredCount() ) );
		return l;
	}

//************************************** IServices methods implementation ***********************************
	public boolean isExecutable() {
		return app != null && app.isExecutable();
	}

	public boolean isInfobarEnabled() {
		return app != null && app.isInfobarEnabled();
	}

	public boolean isSidebarEnabled() {
		return app != null && app.isSidebarEnabled();
	}

	public boolean isToolbarEnabled() {
		return app != null && app.isToolbarEnabled();
	}
//********************************** End of IServices methods implementation ********************************

	public class TaskInfo {

		String description, total;

		public TaskInfo(String description, String total) {
			this.description = description;
			this.total = total;
		}

		public String getDescription() {
			return description;
		}

		public String getTotal() {
			return total;
		}

	}
}
