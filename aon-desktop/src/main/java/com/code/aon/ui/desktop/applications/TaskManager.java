package com.code.aon.ui.desktop.applications;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.project.enumeration.TaskStatus;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

public class TaskManager implements IServices, IDesktopConstants {

	private ApplicationsManager.App app;
	
	private String InProgressSelect = "SELECT count(id) as total FROM task" +
			" WHERE status = " + TaskStatus.IN_PROGRESS.ordinal() +
			" AND user = :userId";
	private String PendingSelect = "SELECT count(id) as total FROM task" +
			" WHERE status = " + TaskStatus.PENDING.ordinal() +
			" AND user = :userId";
	private String UserWorkgroupSelect = "SELECT count(task.id) as total FROM task, user_workgroup" +
			" WHERE task.user is null AND task.workgroup = user_workgroup.workgroup" +
			" AND user_workgroup.user = :userId";
	private String expiredSelect = "SELECT count(id) as total FROM task" +
		" WHERE status IN (" + TaskStatus.IN_PROGRESS.ordinal() + "," + TaskStatus.PENDING.ordinal() + ")" +
		" AND user = :userId" +
		" AND due_date < :dueDate";
	private Integer userId;

	public TaskManager() {
		ApplicationsManager apps = 
			(ApplicationsManager) AonUtil.getRegisteredBean( APPLICATIONS_CONTROLLER_NAME );
		app = apps.getApplication( "aon-task" );
		userId = UserUtils.getInstance().getLoggedUser().getId();
	}

	public List<TaskInfo> getTaskSummaryModel() {
		ArrayList<TaskInfo> l = new ArrayList<TaskInfo>();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		Session session = HibernateUtil.getSession(sessionFactoryName);
		Query query = session.createSQLQuery( InProgressSelect )
			.addScalar( "total", Hibernate.INTEGER ).setParameter( "userId", userId );
		String desc = TaskStatus.IN_PROGRESS.getName( AonUtil.getCurrentLocale() );
		Integer inprogress = (Integer) query.uniqueResult();
		query = session.createSQLQuery( PendingSelect )
			.addScalar( "total", Hibernate.INTEGER ).setParameter( "userId", userId );
		Integer pending = (Integer) query.uniqueResult();
		query = session.createSQLQuery( UserWorkgroupSelect )
			.addScalar( "total", Hibernate.INTEGER ).setParameter( "userId", userId );
		Integer group = (Integer) query.uniqueResult();
		l.add( new TaskInfo( desc, inprogress + "/" + (inprogress + pending + group) ) );

		query = session.createSQLQuery( expiredSelect )
			.addScalar( "total", Hibernate.INTEGER ).setParameter( "userId", userId ).setParameter( "dueDate", new Date() );
		l.add( new TaskInfo( "Vencidas", "" + query.uniqueResult() ) );
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
