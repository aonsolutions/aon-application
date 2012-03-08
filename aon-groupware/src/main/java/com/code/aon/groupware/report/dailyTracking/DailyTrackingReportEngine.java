package com.code.aon.groupware.report.dailyTracking;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.groupware.enumeration.DailyTrackingReportType;

public class DailyTrackingReportEngine {

	private static final String AND = " AND ";
	
	private static final String USER_GRAPHIC = "SELECT new com.code.aon.groupware.report.dailyTracking.DailyTrackingSummaryReport("
		+ " thd.id as userId, thr.name as userName, sum(dt.trackingDuration) as duration, sum(dt.cost) as cost)"
		+ " FROM DailyTracking dt " 
		+ " inner join dt.taskHolder as thd"
		+ " left outer join dt.taskHolder.registry as thr ";
	
	private static final String PROJECT_GRAPHIC = "SELECT new com.code.aon.groupware.report.dailyTracking.DailyTrackingSummaryReport("
			+ " prj.id as projectId, prj.name as projectName, sum(dt.trackingDuration) as duration, sum(dt.cost) as cost)"
			+ " FROM DailyTracking dt " 
			+ " left outer join dt.project as prj";
	
	private static final String JOB_TYPE_GRAPHIC = "SELECT new com.code.aon.groupware.report.dailyTracking.DailyTrackingSummaryReport("
			+ " job.id as jobId, job.description as jobName, sum(dt.trackingDuration) as duration, sum(dt.cost) as cost)"
			+ " FROM DailyTracking dt " 
			+ " inner join dt.jobType as job";
	
	private static final String CUSTOMER_GRAPHIC = "SELECT new com.code.aon.groupware.report.dailyTracking.DailyTrackingSummaryReport("
			+ " reg.id as registryId, reg.name as registryName, sum(dt.trackingDuration) as duration, sum(dt.cost) as cost))"
			+ " FROM DailyTracking dt " 
			+ " left outer join dt.registry as reg";
	
	private static final String SENTENCE = "SELECT "
			+ "new com.code.aon.groupware.report.dailyTracking.DailyTrackingReport(  dt.id as id"
			+ ", thd.id as userId, thr.name as userName, dt.trackingDate as date"
			+ ", dt.trackingDuration as duration, dt.cost as cost, job.id as jobId"
			+ ", job.description as jobDescription, reg.id as registryId"
			+ ", reg.name as registryName, prj.id as projectId"
			+ ", prj.name as projectName, aty.id as activityTypeId"
			+ ", aty.description as activityTypeName, prt.id as projectTypeId"
			+ ", prt.description as projectTypeDescription, dt.comments)"
			+ " FROM DailyTracking dt " 
			+ " inner join dt.taskHolder as thd "
			+ " left outer join dt.taskHolder.registry as thr "
			+ " inner join dt.jobType as job"
			+ " left outer join dt.registry as reg"
			+ " left outer join dt.project as prj "
			+ " left outer join dt.project.projectType as prt "
			+ " left outer join dt.activityType as aty";

	private String getSentence( DailyTrackingReportType reportType ) {
		StringBuilder sentence = new StringBuilder();
		if (reportType == DailyTrackingReportType.GRAPHIC_BY_USER) {
			sentence.append(USER_GRAPHIC);
		} else if (reportType == DailyTrackingReportType.GRAPHIC_BY_CUSTOMER) {
			sentence.append(CUSTOMER_GRAPHIC);
		} else if (reportType == DailyTrackingReportType.GRAPHIC_BY_PROJECT) {
			sentence.append(PROJECT_GRAPHIC);
		} else if (reportType == DailyTrackingReportType.GRAPHIC_BY_JOB_TYPE) {
			sentence.append(JOB_TYPE_GRAPHIC);
		} else {
			sentence.append(SENTENCE);
		}
		return sentence.toString();
	}
	
	private Object getWhere( DailyTrackingReportParams params ) {
		StringBuilder where = new StringBuilder();
		where.append(" WHERE " + DomainManager.getSQLWhereClause("dt.domain"));
		if (params.getFromDate() != null) {
			where.append(" AND dt.trackingDate >= ?");
		}
		if (params.getToDate() != null) {
			where.append(" AND dt.trackingDate <= ?");
		}
		if (params.getTaskHolder() != null) {
			where.append(" AND dt.taskHolder.id = ?");
		}
		if (params.getProject() != null) {
			where.append(" AND dt.project.id = ?");
		}
		if (params.getActivityType() != null) {
			where.append(" AND dt.activityType.id = ?");
		}
		if (params.getRegistry() != null && params.getRegistry().getId()!=null) {
			where.append(" AND dt.registry.id = ?");
		}
		if (params.getJobType() != null) {
			where.append(" AND dt.jobType.id = ?");
		}
		if (params.getProjectType() != null) {
			where.append(" AND dt.project.projectType.id = ?");
		}
		return where.toString();
	}

	private void setParameters(Query query, DailyTrackingReportParams params ) {
		int i = 0;
		if (params.getFromDate() != null) {
			query.setDate(i, params.getFromDate());
			i++;
		}
		if (params.getToDate() != null) {
			query.setDate(i, params.getToDate());
			i++;
		}
		if (params.getTaskHolder() != null) {
			query.setInteger(i, params.getTaskHolder().getId());
			i++;
		}
		if (params.getProject() != null) {
			query.setInteger(i, params.getProject().getId());
			i++;
		}
		if (params.getActivityType() != null) {
			query.setInteger(i, params.getActivityType().getId());
			i++;
		}
		if (params.getRegistry() != null && params.getRegistry().getId() != null) {
			query.setInteger(i, params.getRegistry().getId());
			i++;
		}
		if (params.getJobType() != null) {
			query.setInteger(i, params.getJobType().getId());
			i++;
		}
		if (params.getProjectType() != null) {
			query.setInteger(i, params.getProjectType().getId());
			i++;
		}
	}

	private String getOrderBy( DailyTrackingReportType reportType) {
		
		if (reportType == DailyTrackingReportType.BY_CUSTOMER) {
			return " ORDER BY reg.id,dt.trackingDate ";
		} else if (reportType == DailyTrackingReportType.JOBS_BY_CUSTOMER) {
			return " ORDER BY reg.id,thd.id,job.id";
		} else if (reportType == DailyTrackingReportType.BY_DATE) {
			return " ORDER BY dt.trackingDate,thd.id ";
		} else if (reportType == DailyTrackingReportType.BY_USER) {
			return " ORDER BY thd.id,dt.trackingDate ";
		} else if (reportType == DailyTrackingReportType.BY_PROJECT) {
			return " ORDER BY prj.id,dt.trackingDate ";
		} else if (reportType == DailyTrackingReportType.BY_PROJECT_TYPE) {
			return " ORDER BY prt.id,dt.trackingDate ";
		} else if (reportType == DailyTrackingReportType.BY_JOB_TYPE) {
			return " ORDER BY job.id,dt.trackingDate ";
		} else if (reportType == DailyTrackingReportType.GRAPHIC_BY_USER) {
			return " GROUP BY thd.id,thr.name ORDER BY sum(dt.trackingDuration) desc";
		} else if (reportType == DailyTrackingReportType.GRAPHIC_BY_PROJECT) {
			return " GROUP BY prj.id,prj.name ORDER BY sum(dt.trackingDuration) desc";
		} else if (reportType == DailyTrackingReportType.GRAPHIC_BY_CUSTOMER) {
			return " GROUP BY reg.id,reg.name ORDER BY sum(dt.trackingDuration) desc";
		} else if (reportType == DailyTrackingReportType.GRAPHIC_BY_JOB_TYPE) {
			return " GROUP BY job.id,job.description ORDER BY sum(dt.trackingDuration) desc";
		}
		return "";
	}
	
	public Collection<?> getReportCollection(DailyTrackingReportParams params) {
		StringBuilder sentence = new StringBuilder(getSentence(params.getReportType()));
		sentence.append(getWhere(params));
		sentence.append(getOrderBy(params.getReportType()));
		Session session = HibernateUtil.getSession( HibernateUtil.getSessionFactoryName() );
		Query query = session.createQuery(sentence.toString());
		setParameters(query,params);
		return query.list();
	}
	
}
