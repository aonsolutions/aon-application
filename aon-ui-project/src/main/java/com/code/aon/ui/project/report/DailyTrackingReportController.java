package com.code.aon.ui.project.report;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.User;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.customer.Customer;
import com.code.aon.project.Activity;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Dossier;
import com.code.aon.project.DossierType;
import com.code.aon.project.JobType;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.DossierStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.report.OutputFormat;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class DailyTrackingReportController implements ICollectionProvider {

	private final static Logger LOGGER = LoggerFactory.getLogger(DailyTrackingReportController.class);
	
	private static final String EXCEL = "dailyTrackingExcel";
	private static final String BY_CUSTOMER = "dailyTrackingByCustomer";
	private static final String BY_USER = "dailyTrackingByUser";
	private static final String BY_JOB_TYPE = "dailyTrackingByJobType";
	private static final String BY_DOSSIER = "dailyTrackingByDossier";
	private static final String BY_DOSSIER_TYPE = "dailyTrackingByDossierType";
	private static final String BY_DATE = "dailyTrackingByDate";
	private static final String GRAPHIC_BY_USER = "dailyTrackingGraphicByUser";
	private static final String GRAPHIC_BY_DOSSIER = "dailyTrackingGraphicByDossier";
	private static final String GRAPHIC_BY_CUSTOMER = "dailyTrackingGraphicByCustomer";
	private static final String GRAPHIC_BY_JOB_TYPE = "dailyTrackingGraphicByJobType";
	private static final String AND = " AND ";

	private static final String USER_GRAPHIC = "SELECT new com.code.aon.ui.project.report.DailyTrackingSummaryReport("
			+ " usr.id as userId, usr.name as userName, sum(dt.trackingDuration) as duration)"
			+ " FROM DailyTracking dt inner join dt.user as usr";
	private static final String DOSSIER_GRAPHIC = "SELECT new com.code.aon.ui.project.report.DailyTrackingSummaryReport("
			+ " dss.id as dossierId, dss.number as dossierNumber, sum(dt.trackingDuration) as duration)"
			+ " FROM DailyTracking dt left outer join dt.dossier as dss";

	private static final String JOB_TYPE_GRAPHIC = "SELECT new com.code.aon.ui.project.report.DailyTrackingSummaryReport("
			+ " job.id as jobId, job.description as jobName, sum(dt.trackingDuration) as duration)"
			+ " FROM DailyTracking dt inner join dt.jobType as job";

	private static final String CUSTOMER_GRAPHIC = "SELECT new com.code.aon.ui.project.report.DailyTrackingSummaryReport("
			+ " cus.id as customerId, cus.registry.name as customerName, sum(dt.trackingDuration) as duration)"
			+ " FROM DailyTracking dt left outer join dt.customer as cus "
			+ " left outer join dt.customer.registry as reg";

	private static final String SENTENCE = "SELECT "
			+ "new com.code.aon.ui.project.report.DailyTrackingReport(  dt.id as id"
			+ ", usr.id as userId, usr.name as userName, dt.trackingDate as date"
			+ ", dt.trackingDuration as duration, job.id as jobId"
			+ ", job.description as jobDescription, cus.id as customerId"
			+ ", reg.name as customerName, dss.id as dossierId"
			+ ", dss.number as dossierNumber, act.id as activityId"
			+ ", aty.description as activityName, dty.id as dossierTypeId"
			+ ", dty.description as dossierTypeDescription, dt.comments)"
			+ " FROM DailyTracking dt inner join dt.user as usr inner join dt.jobType as job"
			+ " left outer join dt.customer as cus"
			+ " left outer join dt.customer.registry as reg "
			+ " left outer join dt.dossier as dss "
			+ " left outer join dt.dossier.dossierType as dty "
			+ " left outer join dt.activity as act"
			+ " left outer join dt.activity.activityType as aty";

	private List<SelectItem> dossiers;
	private List<SelectItem> dossierTypes;
	private List<SelectItem> allDossiers;
	private List<SelectItem> activities;
	private List<SelectItem> activityTypes;
	private List<SelectItem> users;

	private Customer customer;
	private WorkGroup workgroup;
	private User user;
	private Dossier dossier;
	private Activity activity;
	private Date fromDate;
	private Date toDate;
	private JobType jobType;
	private DossierType dossierType;
	private ActivityType activityType;

	private boolean dossierTypeDisabled;
	private boolean activityTypeDisabled;

	private String reportKey;

	public List<SelectItem> getDossiers() {
		return dossiers;
	}

	public void setDossiers(List<SelectItem> dossiers) {
		this.dossiers = dossiers;
	}

	public List<SelectItem> getActivities() {
		if (activities == null) {
			loadActivities(null);
		}
		return activities;
	}

	public void setActivities(List<SelectItem> activities) {
		this.activities = activities;
	}

	public List<SelectItem> getUsers() {
		if (users == null) {
			loadUsers(null);
		}
		return users;
	}

	public void setUsers(List<SelectItem> users) {
		this.users = users;
	}

	public WorkGroup getWorkgroup() {
		return workgroup;
	}

	public void setWorkgroup(WorkGroup workgroup) {
		this.workgroup = workgroup;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Dossier getDossier() {
		return dossier;
	}

	public void setDossier(Dossier dossier) {
		this.dossier = dossier;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public JobType getJobType() {
		return jobType;
	}

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getReportKey() {
		return reportKey;
	}

	public void setReportKey(String reportKey) {
		this.reportKey = reportKey;
	}

	public DossierType getDossierType() {
		return dossierType;
	}

	public void setDossierType(DossierType dossierType) {
		this.dossierType = dossierType;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public boolean isDossierTypeDisabled() {
		return dossierTypeDisabled;
	}

	public void setDossierTypeDisabled(boolean dossierTypeDisabled) {
		this.dossierTypeDisabled = dossierTypeDisabled;
	}

	public boolean isActivityTypeDisabled() {
		return activityTypeDisabled;
	}

	public void setActivityTypeDisabled(boolean activityTypeDisabled) {
		this.activityTypeDisabled = activityTypeDisabled;
	}

	public List<SelectItem> getDossierTypes() {
		if (dossierTypes == null) {
			loadDossierTypes();
		}
		return dossierTypes;
	}

	public void setDossierTypes(List<SelectItem> dossierTypes) {
		this.dossierTypes = dossierTypes;
	}

	public List<SelectItem> getActivityTypes() {
		if (activityTypes == null) {
			loadActivityTypes(null);
		}
		return activityTypes;
	}

	public void setActivityTypes(List<SelectItem> activityTypes) {
		this.activityTypes = activityTypes;
	}

	public void onResetReportSearch(ActionEvent event) {
		initializeSearchParameters();
	}

	@SuppressWarnings("unchecked")
	private void loadDossierTypes() {
		try {
			dossierTypes = new LinkedList<SelectItem>();
			IManagerBean dossierTypeBean = BeanManager.getManagerBean(DossierType.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(dossierTypeBean.getFieldName(IProjectAlias.DOSSIER_TYPE_DESCRIPTION));
			Iterator iter = dossierTypeBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				DossierType type = (DossierType) iter.next();
				SelectItem item = new SelectItem(type, type.getDescription());
				dossierTypes.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error loading dossier types", e);
		}
	}

	@SuppressWarnings("unchecked")
	private void loadActivityTypes(Integer dossierType) {
		try {
			activityTypes = new LinkedList<SelectItem>();
			IManagerBean activityTypeBean = BeanManager.getManagerBean(ActivityType.class);
			Criteria criteria = new Criteria();
			if (dossierType != null) {
				Expression e1 = ExpressionUtilities.getEqualExpression(activityTypeBean
						.getFieldName(IProjectAlias.ACTIVITY_TYPE_DOSSIER_TYPE_ID), dossierType);
				Expression e2 = ExpressionUtilities.getNullExpression(activityTypeBean
						.getFieldName(IProjectAlias.ACTIVITY_TYPE_DOSSIER_TYPE_ID));
				criteria.addExpression(ExpressionUtilities.getOrExpression(e1, e2));
			}
			criteria.addOrder(activityTypeBean
					.getFieldName(IProjectAlias.ACTIVITY_TYPE_DOSSIER_TYPE_ID));
			criteria.addOrder(activityTypeBean
					.getFieldName(IProjectAlias.ACTIVITY_TYPE_DESCRIPTION));
			Iterator iter = activityTypeBean.getList(criteria).iterator();
			Integer d = null;
			boolean first = true;
			List temp = new LinkedList<SelectItem>();
			SelectItemGroup group = null;
			while (iter.hasNext()) {
				ActivityType at = (ActivityType) iter.next();
				DossierType dt = at.getDossierType();
				if (dt != null) {
					if (d == null || !d.equals(dt.getId())) {
						if (group != null) {
							group.setSelectItems((SelectItem[]) temp.toArray(new SelectItem[temp.size()]));
						}
						temp = new LinkedList<SelectItem>();
						group = new SelectItemGroup(dt.getDescription());
						activityTypes.add(group);
						d = dt.getId();
					}
				} else {
					if (first) {
						group = new SelectItemGroup("------------------");
						activityTypes.add(group);
						first = false;
					}
				}
				SelectItem item = new SelectItem(at, at.getDescription());
				temp.add(item);
			}
			if (group != null) {
				group.setSelectItems((SelectItem[]) temp.toArray(new SelectItem[temp.size()]));
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error loading activity types", e);
		}
	}

	public void reloadDossiers(LookupChangeEvent event) {
		if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
			Object newValue = event.getNewValue();
			if (newValue instanceof Customer) {
				Customer c = (Customer) newValue;
				loadDossiers(c.getId(), null);
			}
		} else {
			loadDossiers(null, null);
		}
		setActivities(null);
	}

	public void workgroupChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			loadUsers(((WorkGroup) event.getNewValue()).getId());
		} else {
			setUsers(null);
		}
	}

	public void dossierTypeChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			DossierType dossierType = (DossierType) event.getNewValue();
			loadActivityTypes(dossierType.getId());
			loadDossiers(null, dossierType.getId());
		} else {
			loadActivityTypes(null);
			loadDossiers(null, null);
		}
	}

	public void activityChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Activity a = (Activity) event.getNewValue();
			setActivityType(a.getActivityType());
			setActivityTypeDisabled(true);
		} else {
			setActivityTypeDisabled(false);
		}
	}

	public void activityTypeChanged(ValueChangeEvent event) {
		setDossierTypeDisabled(getDossier() != null);
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			ActivityType at = (ActivityType) event.getNewValue();
			DossierType dt = at.getDossierType();
			if (dt != null) {
				DossierType old = getDossierType();
				setDossierType(dt);
				setDossierTypeDisabled(true);
				ValueChangeEvent ev = new ValueChangeEvent(event.getComponent(), old,
						getDossierType());
				dossierTypeChanged(ev);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadDossiers(Integer customerId, Integer dossierTypeId) {
		dossiers = new LinkedList<SelectItem>();
		try {
			IManagerBean managerBean = BeanManager.getManagerBean(Dossier.class);
			Criteria criteria = new Criteria();
			if (customerId == null) {
				if (getCustomer() != null && getCustomer().getId() != null) {
					customerId = getCustomer().getId();
				}
			}
			if (customerId != null) {
				criteria.addEqualExpression(managerBean
						.getFieldName(IProjectAlias.DOSSIER_CUSTOMER_ID), customerId);
			}

			if (dossierTypeId == null) {
				if (getDossierType() != null) {
					dossierTypeId = getDossierType().getId();
				}
			}
			if (dossierTypeId != null) {
				criteria.addEqualExpression(managerBean
						.getFieldName(IProjectAlias.DOSSIER_DOSSIER_TYPE_ID), dossierTypeId);
			}
			criteria.addEqualExpression(managerBean.getFieldName(IProjectAlias.DOSSIER_STATUS),
					DossierStatus.ACTIVE);
			criteria.addOrder(managerBean.getFieldName(IProjectAlias.DOSSIER_NUMBER));
			Iterator iterator = managerBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				Dossier dossier = (Dossier) iterator.next();
				SelectItem item;
				if (customerId == null) {
					StringBuilder sb = new StringBuilder(dossier.getNumber());
					sb.append(" (");
					sb.append(dossier.getCustomer().getRegistry().getAlias());
					sb.append(")");
					item = new SelectItem(dossier, sb.toString());
				} else {
					item = new SelectItem(dossier, dossier.getNumber());
				}
				dossiers.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error loading dossiers related with customer with id= "
					+ customerId.toString(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadAllDossiers() {
		allDossiers = new LinkedList<SelectItem>();
		try {
			IManagerBean managerBean = BeanManager.getManagerBean(Dossier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(managerBean.getFieldName(IProjectAlias.DOSSIER_STATUS),
					DossierStatus.ACTIVE);
			criteria.addOrder(managerBean.getFieldName(IProjectAlias.DOSSIER_NUMBER));
			criteria.addOrder(managerBean.getFieldName(IProjectAlias.DOSSIER_CUSTOMER_ID));
			Iterator iterator = managerBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				Dossier dossier = (Dossier) iterator.next();
				StringBuilder sb = new StringBuilder(dossier.getNumber());
				sb.append(" (");
				sb.append(dossier.getCustomer().getRegistry().getAlias());
				sb.append(")");
				SelectItem item = new SelectItem(dossier, sb.toString());
				allDossiers.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error loading all dossiers!", e);
		}
	}

	public void dossierChange(ValueChangeEvent event) {
		setDossierTypeDisabled(getActivityType() != null);
		if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
			Dossier d = (Dossier) event.getNewValue();
			if (d != null) {
				if (getCustomer() == null || getCustomer().getId() == null
						|| !d.getId().equals(getCustomer().getId())) {
					setCustomer(d.getCustomer());
					LookupChangeEvent ev = new LookupChangeEvent(event.getComponent(), d
							.getCustomer().getId());
					reloadDossiers(ev);
				}

				if (getDossierType() == null) {
					DossierType dt = d.getDossierType();
					if (dt != null) {
						DossierType old = getDossierType();
						setDossierType(dt);
						setDossierTypeDisabled(true);
						ValueChangeEvent ev = new ValueChangeEvent(event.getComponent(), old,
								getDossierType());
						dossierTypeChanged(ev);
					} else {
						setDossierTypeDisabled(getActivityType() != null);
					}
				}

			}
			loadActivities(d.getId());
		} else {
			loadActivities(null);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadActivities(Integer dossierId) {
		activities = new LinkedList<SelectItem>();
		try {
			IManagerBean managerBean = BeanManager.getManagerBean(Activity.class);
			Criteria criteria = new Criteria();
			if (dossierId != null) {
				criteria.addEqualExpression(managerBean
						.getFieldName(IProjectAlias.ACTIVITY_DOSSIER_ID), dossierId);
			} else {
				criteria.addNullExpression(managerBean
						.getFieldName(IProjectAlias.ACTIVITY_DOSSIER_ID));
			}
			Iterator iterator = managerBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				Activity activity = (Activity) iterator.next();
				SelectItem item = new SelectItem(activity, activity.getActivityType()
						.getDescription());
				activities.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error loading activities related with dossier with id= "
					+ dossierId.toString(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadUsers(Integer workgroupId) {
		users = new LinkedList<SelectItem>();
		try {
			Criteria criteria = new Criteria();
			if (workgroupId != null) {
				IManagerBean userWorkGroupBean = BeanManager.getManagerBean(UserWorkGroup.class);
				criteria.addEqualExpression(userWorkGroupBean
						.getFieldName(IConfigAlias.USER_WORK_GROUP_WORK_GROUP_ID), workgroupId);
				criteria.addOrder(userWorkGroupBean
						.getFieldName(IConfigAlias.USER_WORK_GROUP_USER_NAME));
				Iterator iter = userWorkGroupBean.getList(criteria).iterator();
				while (iter.hasNext()) {
					UserWorkGroup userWorkGroup = (UserWorkGroup) iter.next();
					boolean added = false;
					for (SelectItem it: users) {
						if (it.getValue().equals(userWorkGroup.getUser())) {
							added = true;
							break;
						}
					}
					if (!added) {
						SelectItem item = new SelectItem(userWorkGroup.getUser(), userWorkGroup
								.getUser().getName());
						users.add(item);
					}
				}
			} else {
				IManagerBean userBean = BeanManager.getManagerBean(User.class);
				criteria.addOrder(userBean.getFieldName(IConfigAlias.USER_NAME));
				Iterator iter = userBean.getList(criteria).iterator();
				while (iter.hasNext()) {
					User user = (User) iter.next();
					SelectItem item = new SelectItem(user, user.getName());
					users.add(item);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error loading users related with workgroup with id= "
					+ workgroupId, e);
		}
	}

	private void initializeSearchParameters() {
		setCustomer(new Customer());
		setWorkgroup(null);
		setUser(null);
		setDossier(null);
		setActivity(null);
		setDossierType(null);
		setActivityType(null);
		setJobType(null);
		setFromDate(null);
		setToDate(null);
		setDossiers(null);
		setActivities(null);
		setUsers(null);
		setActivityTypes(null);
		setDossierTypes(null);
		setDossierTypeDisabled(false);
		setActivityTypeDisabled(false);
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<SelectItem> getAllDossiers() {
		if (allDossiers == null) {
			loadAllDossiers();
		}
		return allDossiers;
	}

	public void setAllDossiers(List<SelectItem> allDossiers) {
		this.allDossiers = allDossiers;
	}

	public List<SelectItem> getAvailableDossiers() {
		if ((getCustomer() != null && getCustomer().getId() != null)
				|| (getDossierType() != null)) {
			return getDossiers();
		}
		return getAllDossiers();
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		StringBuilder sentence = new StringBuilder(getSentence());
		sentence.append(getWhere());
		sentence.append(getOrderBy());
		Session session = HibernateUtil.getSession( HibernateUtil.getSessionFactoryName() );
		Query query = session.createQuery(sentence.toString());
		setParameters(query);
		return query.list();
	}

	private Object getWhere() {
		StringBuilder where = new StringBuilder();
		if (getFromDate() != null) {
			where.append("dt.trackingDate >= ?");
		}
		if (getToDate() != null) {
			if (where.length() > 0) {
				where.append(AND);
			}
			where.append("dt.trackingDate <= ?");
		}
		if (getUser() != null) {
			if (where.length() > 0) {
				where.append(AND);
			}
			where.append("dt.user.id = ?");
		}

		if (getDossier() != null) {
			if (where.length() > 0) {
				where.append(AND);
			}
			where.append("dt.dossier.id = ?");
		}

		if (getActivity() != null) {
			if (where.length() > 0) {
				where.append(AND);
			}
			where.append("dt.activity.id = ?");
		}

		if (getCustomer() != null && getCustomer().getId()!=null) {
			if (where.length() > 0) {
				where.append(AND);
			}
			where.append("dt.customer.id = ?");
		}

		if (getJobType() != null) {
			if (where.length() > 0) {
				where.append(AND);
			}
			where.append("dt.jobType.id = ?");
		}

		if (getDossierType() != null) {
			if (where.length() > 0) {
				where.append(AND);
			}
			where.append("dt.dossier.dossierType.id = ?");
		}

		if (getActivityType() != null) {
			if (where.length() > 0) {
				where.append(AND);
			}
			where.append("dt.activity.activityType.id = ?");
		}

		if (where.length() > 0) {
			where.insert(0, " WHERE ");
		}
		return where.toString();
	}

	private void setParameters(Query query) {
		int i = 0;
		if (getFromDate() != null) {
			query.setDate(i, getFromDate());
			i++;
		}
		if (getToDate() != null) {
			query.setDate(i, getToDate());
			i++;
		}
		if (getUser() != null) {
			query.setInteger(i, getUser().getId());
			i++;
		}
		if (getDossier() != null) {
			query.setInteger(i, getDossier().getId());
			i++;
		}
		if (getActivity() != null) {
			query.setInteger(i, getActivity().getId());
			i++;
		}
		if (getCustomer() != null && getCustomer().getId() != null) {
			query.setInteger(i, getCustomer().getId());
			i++;
		}
		if (getJobType() != null) {
			query.setInteger(i, getJobType().getId());
			i++;
		}
		if (getDossierType() != null) {
			query.setInteger(i, getDossierType().getId());
			i++;
		}
		if (getActivityType() != null) {
			query.setInteger(i, getActivityType().getId());
			i++;
		}
	}

	private String getOrderBy() {
		if (BY_CUSTOMER.equals(getReportKey())) {
			return " ORDER BY cus.id,dt.trackingDate ";
		} else if (BY_DATE.equals(getReportKey())) {
			return " ORDER BY dt.trackingDate,usr.id ";
		} else if (BY_USER.equals(getReportKey())) {
			return " ORDER BY usr.id,dt.trackingDate ";
		} else if (BY_DOSSIER.equals(getReportKey())) {
			return " ORDER BY dss.id,dt.trackingDate ";
		} else if (BY_DOSSIER_TYPE.equals(getReportKey())) {
			return " ORDER BY dty.id,dt.trackingDate ";
		} else if (BY_JOB_TYPE.equals(getReportKey())) {
			return " ORDER BY job.id,dt.trackingDate ";
		} else if (GRAPHIC_BY_USER.equals(getReportKey())) {
			return " GROUP BY usr.id,usr.name ORDER BY usr.name";
		} else if (GRAPHIC_BY_DOSSIER.equals(getReportKey())) {
			return " GROUP BY dss.id,dss.number ORDER BY dss.number";
		} else if (GRAPHIC_BY_CUSTOMER.equals(getReportKey())) {
			return " GROUP BY cus.id,cus.registry.name ORDER BY cus.registry.name";
		} else if (GRAPHIC_BY_JOB_TYPE.equals(getReportKey())) {
			return " GROUP BY job.id,job.description ORDER BY job.description";
		}
		return "";
	}

	private OutputFormat getOutputFormat() {
		if (EXCEL.equals(getReportKey())) {
			return OutputFormat.XLS;
		}
		return OutputFormat.PDF;
	}

	private String getSentence() {
		StringBuilder sentence = new StringBuilder();
		if (GRAPHIC_BY_USER.equals(getReportKey())) {
			sentence.append(USER_GRAPHIC);
		} else if (GRAPHIC_BY_CUSTOMER.equals(getReportKey())) {
			sentence.append(CUSTOMER_GRAPHIC);
		} else if (GRAPHIC_BY_DOSSIER.equals(getReportKey())) {
			sentence.append(DOSSIER_GRAPHIC);
		} else if (GRAPHIC_BY_JOB_TYPE.equals(getReportKey())) {
			sentence.append(JOB_TYPE_GRAPHIC);
		} else {
			sentence.append(SENTENCE);
		}
		return sentence.toString();
	}

	public String onReport() {
		ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
		manager.setReportKey(getReportKey());
		manager.setOutputFormat(getOutputFormat() == null ? OutputFormat.PDF : getOutputFormat());
		String outcome = manager.onExecute();
		return outcome;
	}

}
