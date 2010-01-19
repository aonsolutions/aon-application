package com.code.aon.ui.project.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.project.Activity;
import com.code.aon.project.DailyTracking;
import com.code.aon.project.Dossier;
import com.code.aon.project.JobType;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.DossierStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;

public class DailyTrackingController extends BasicController {

	private static final Logger LOGGER = Logger.getLogger(DailyTrackingController.class.getName());

	private List<SelectItem> dossiers = new LinkedList<SelectItem>();
	private List<SelectItem> allDossiers;
	private List<SelectItem> activities = new LinkedList<SelectItem>();
	private List<SelectItem> users;

	private Customer customer;

	private User user;
	private Date trackingDateFrom;
	private Date trackingDateTo;
	private JobType trackingJobType;

	private boolean monitor;

	public boolean isMonitor() {
		return monitor;
	}

	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}

	public void onSwicthMonitor(ActionEvent event) {
		onRefresh(event);
	}

	public List<SelectItem> getDossiers() {
		return dossiers;
	}

	public void setDossiers(List<SelectItem> dossiers) {
		this.dossiers = dossiers;
	}

	public List<SelectItem> getActivities() {
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void onRefresh(ActionEvent event) {
		super.onSearch(event);
	}

	public void onLaunch(ActionEvent event) {
		super.onReset(event);
		setDossiers(new LinkedList<SelectItem>());
		setAllDossiers(null);
		setActivities(new LinkedList<SelectItem>());
		setUsers(null);
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		setCustomer(new Customer());
		setTrackingDateFrom(null);
		setTrackingDateTo(null);
		setTrackingJobType(null);
		setUser(null);
	}

	public void customerPojoChange(LookupChangeEvent event) {
		if (event.getNewValue() != null) {
			Customer customer = (Customer) event.getNewValue();
			loadDossiers(customer.getId());
			loadCustomer(customer.getId());
		} else {
			dossiers = new LinkedList<SelectItem>();
		}
		activities = new LinkedList<SelectItem>();
	}

	public void workgroupChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			WorkGroup wg = (WorkGroup)  event.getNewValue();
			loadUsers(wg.getId());
		} else {
			users = new LinkedList<SelectItem>();
		}
	}

	@SuppressWarnings("unchecked")
	public void loadDossiers(Integer customerId) {
		dossiers = new LinkedList<SelectItem>();
		try {
			IManagerBean managerBean = BeanManager.getManagerBean(Dossier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					managerBean.getFieldName(IProjectAlias.DOSSIER_CUSTOMER_ID), customerId);
			criteria.addEqualExpression(managerBean.getFieldName(IProjectAlias.DOSSIER_STATUS),
					DossierStatus.ACTIVE);
			criteria.addOrder(managerBean.getFieldName(IProjectAlias.DOSSIER_NUMBER));
			Iterator iterator = managerBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				Dossier dossier = (Dossier) iterator.next();
				SelectItem item = new SelectItem(dossier, dossier.getNumber());
				dossiers.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading dossiers related with customer with id= "
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
			LOGGER.log(Level.SEVERE, "Error loading all dossiers!", e);
		}
	}

	@SuppressWarnings("unchecked")
	private void loadCustomer(Integer id) {
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), id);
			Iterator iter = customerBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				Customer customer = (Customer) iter.next();
				((DailyTracking) this.getTo()).setCustomer(customer);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading customer with id= " + id, e);
		}
	}

	public void dossierChange(ValueChangeEvent event) {
		if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
			DailyTracking dt = (DailyTracking) getTo();
			Dossier d = (Dossier) event.getNewValue();
			if (dt.getCustomer() == null || dt.getCustomer().getId() == null
					|| !d.getId().equals(dt.getCustomer().getId())) {
				dt.setCustomer(d.getCustomer());
				LookupChangeEvent ev = new LookupChangeEvent(event.getComponent(), d.getCustomer());
				customerPojoChange(ev);
			}
			loadActivities(d.getId());
		} else {
			activities = new LinkedList<SelectItem>();
		}
	}

	@SuppressWarnings("unchecked")
	public void loadActivities(Integer dossierId) {
		activities = new LinkedList<SelectItem>();
		try {
			IManagerBean managerBean = BeanManager.getManagerBean(Activity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					managerBean.getFieldName(IProjectAlias.ACTIVITY_DOSSIER_ID), dossierId);
			Iterator iterator = managerBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				Activity activity = (Activity) iterator.next();
				SelectItem item = new SelectItem(activity, activity.getActivityType()
						.getDescription());
				activities.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading activities related with dossier with id= "
					+ dossierId.toString(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadUsers(Integer workgroupId) {
		users = new LinkedList<SelectItem>();
		try {
			IManagerBean userWorkGroupBean = BeanManager.getManagerBean(UserWorkGroup.class);
			Criteria criteria = new Criteria();
			if (workgroupId != null) {
				criteria.addEqualExpression(userWorkGroupBean
						.getFieldName(IConfigAlias.USER_WORK_GROUP_WORK_GROUP_ID), workgroupId);
			}
			criteria.addOrder(userWorkGroupBean
					.getFieldName(IConfigAlias.USER_WORK_GROUP_USER_NAME));
			Iterator iter = userWorkGroupBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				UserWorkGroup userWorkGroup = (UserWorkGroup) iter.next();
				boolean added = false;
				for (SelectItem it:users) {
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
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading users related with workgroup with id= "
					+ workgroupId, e);
		}
	}

	public void addTrackingDateFromExpression(ValueChangeEvent event) {
		if (event.getNewValue() != null && getTrackingDateFrom() != null) {
			try {
				IManagerBean dailyTrackingBean = BeanManager.getManagerBean(DailyTracking.class);
				getCriteria().addGreaterThanOrEqualExpression(
						dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_TRACKING_DATE),
						event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM date expression", e);
			}
		}
	}

	public void addTrackingDateToExpression(ValueChangeEvent event) {
		if (event.getNewValue() != null && getTrackingDateTo() != null) {
			try {
				IManagerBean dailyTrackingBean = BeanManager.getManagerBean(DailyTracking.class);
				getCriteria().addLessThanOrEqualExpression(
						dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_TRACKING_DATE),
						event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO date expression", e);
			}
		}
	}

	/*
	 * public void addEqualExpression(ValueChangeEvent event) throws
	 * ManagerBeanException, ExpressionException { if (event.getNewValue() !=
	 * null && !"".equals(event.getNewValue())) { Object value =
	 * event.getNewValue(); Criteria criteria = getCriteria();
	 * criteria.addExpression(getFieldName(event.getComponent().getId()),
	 * value.toString()); setCriteria(criteria); } }
	 * 
	 * public void addCustomerExpression(ValueChangeEvent event) { if
	 * (event.getNewValue() != null &&
	 * !"".equals(event.getNewValue().toString().trim())) { try { IManagerBean
	 * dailyTrackingBean = BeanManager.getManagerBean(DailyTracking.class);
	 * getCriteria().addEqualExpression(
	 * dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_CUSTOMER_ID),
	 * new Integer(event.getNewValue().toString())); } catch
	 * (ManagerBeanException e) { LOGGER.log(Level.SEVERE,
	 * "Error adding customer Expression", e); } } }
	 * 
	 * public void addCustomerPojoExpression(ValueChangeEvent event) { if
	 * (event.getNewValue() != null && !event.getNewValue().equals("")) { try {
	 * Customer c = (Customer) event.getNewValue();
	 * getCriteria().addEqualExpression(
	 * getFieldName(IProjectAlias.DAILY_TRACKING_CUSTOMER_ID), new
	 * Integer(c.getId().toString())); } catch (ManagerBeanException e) {
	 * LOGGER.log(Level.SEVERE, "Error adding customer expression", e); } } }
	 */

	public void reloadDossiers(ValueChangeEvent event) {
		if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
			Object newValue = event.getNewValue();
			if (newValue instanceof Customer) {
				Customer c = (Customer) newValue;
				loadDossiers(c.getId());
			} else {
				// customerId
				loadDossiers(new Integer(newValue.toString()));
			}
		} else {
			dossiers = new LinkedList<SelectItem>();
		}
		activities = new LinkedList<SelectItem>();
	}

	/*
	 * @SuppressWarnings("unchecked") private Expression
	 * obtainWorkGroupExpression(Integer workgroupId) { try { IManagerBean
	 * userWorkGroupBean = BeanManager.getManagerBean(UserWorkGroup.class);
	 * IManagerBean dailyTrackingBean =
	 * BeanManager.getManagerBean(DailyTracking.class); Criteria criteria = new
	 * Criteria(); criteria.addEqualExpression(userWorkGroupBean
	 * .getFieldName(IConfigAlias.USER_WORK_GROUP_WORK_GROUP_ID), workgroupId);
	 * Iterator iter = userWorkGroupBean.getList(criteria).iterator();
	 * Expression exp = null; while (iter.hasNext()) { UserWorkGroup
	 * userWorkGroup = (UserWorkGroup) iter.next();
	 * ExpressionUtilities.getOrExpression(exp,
	 * ExpressionUtilities.getEqualExpression(
	 * dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_USER_ID),
	 * userWorkGroup.getUser().getId())); } return exp; } catch
	 * (ManagerBeanException e) { LOGGER.log(Level.SEVERE,
	 * "Error adding workgroup Expression", e); } return null; }
	 */
	public Date getTrackingDateFrom() {
		return trackingDateFrom;
	}

	public void setTrackingDateFrom(Date trackingDateFrom) {
		this.trackingDateFrom = trackingDateFrom;
	}

	public Date getTrackingDateTo() {
		return trackingDateTo;
	}

	public void setTrackingDateTo(Date trackingDateTo) {
		this.trackingDateTo = trackingDateTo;
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
		DailyTracking dt = (DailyTracking) getTo();
		if (dt!= null && dt.getCustomer() != null && dt.getCustomer().getId() != null) {
			return getDossiers();
		}
		return getAllDossiers();
	}


	public JobType getTrackingJobType() {
		return trackingJobType;
	}

	public void setTrackingJobType(JobType trackingJobType) {
		this.trackingJobType = trackingJobType;
	}

	public void completeCriteria() {
		try {

			if (getTrackingDateFrom() != null) {
				getCriteria().addGreaterThanOrEqualExpression(
						getFieldName(IProjectAlias.DAILY_TRACKING_TRACKING_DATE),
						getTrackingDateFrom());
			}
			if (getTrackingDateTo() != null) {
				getCriteria().addLessThanOrEqualExpression(
						getFieldName(IProjectAlias.DAILY_TRACKING_TRACKING_DATE),
						getTrackingDateTo());
			}
			if (getCustomer() != null && getCustomer().getId() != null) {
				getCriteria().addEqualExpression(
						getFieldName(IProjectAlias.DAILY_TRACKING_CUSTOMER_ID), customer.getId());
			}
			if (getTrackingJobType() != null && getTrackingJobType().getId() != null) {
				getCriteria().addEqualExpression(
						getFieldName(IProjectAlias.DAILY_TRACKING_JOB_TYPE_ID),
						getTrackingJobType().getId());
			}
			if (isMonitor()) {
				if (getUser() != null && getUser().getId() != null) {
					getCriteria().addEqualExpression(
							getFieldName(IProjectAlias.DAILY_TRACKING_USER_ID), getUser().getId());
				}
			}
			getCriteria().addOrder(getFieldName(IProjectAlias.DAILY_TRACKING_TRACKING_DATE),false);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error adding custom expression", e);
		}
	}
}