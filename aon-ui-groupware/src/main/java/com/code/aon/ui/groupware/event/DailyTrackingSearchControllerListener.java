package com.code.aon.ui.groupware.event;

import static com.code.aon.ui.groupware.controller.DailyTrackingController.COMPANY_TYPE;
import static com.code.aon.ui.groupware.controller.DailyTrackingController.CUSTOMER_TYPE;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.customer.Customer;
import com.code.aon.groupware.JobType;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.groupware.GroupwareUtils;
import com.code.aon.ui.groupware.controller.DailyTrackingController;
import com.code.aon.ui.groupware.controller.IGroupWareConstants;
import com.code.aon.ui.project.controller.IProjectConstants;
import com.code.aon.ui.project.controller.ProjectCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DailyTrackingSearchControllerListener extends ControllerSearchListener {

	private Registry registry;
	private Customer customer;
	private int registryType;
	private TaskHolder taskHolder;
	private Project project;
	private ActivityType activityType;
	private JobType jobType;
	
	private  List<SelectItem> projects;
	private  List<SelectItem> activityTypes;

	private GroupwareUtils groupwareUtils;
	
	public GroupwareUtils getGroupwareUtils() {
		if (groupwareUtils == null) {
			groupwareUtils = new GroupwareUtils();
		}
		return groupwareUtils;
	}

	public Registry getRegistry() {
		return registry;
	}
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public TaskHolder getTaskHolder() {
		return taskHolder;
	}
	public void setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
	}

	public ActivityType getActivityType() {
		return activityType;
	}
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}
	
	public JobType getJobType() {
		return jobType;
	}
	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public int getRegistryType() {
		return registryType;
	}

	public void setRegistryType(int registryType) {
		this.registryType = registryType;
	}

	private void loadProjects(Integer registryId) throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) 
		AonUtil.getRegisteredBean( IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME );
		projects = pcc.getProjects( registryId);
	}

	private void loadActivityTypes(Integer projectTypeId) throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) 
			AonUtil.getRegisteredBean( IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME );
		if (projectTypeId != null) {
			activityTypes = pcc.getActivityTypes( projectTypeId );
		} else {
			activityTypes = new LinkedList<SelectItem>();	
		}
	}

	public void onRegistryChanged(LookupChangeEvent event) {
		Registry registry = (Registry) event.getNewValue();
		registryChanged(registry);
	}

	private void registryChanged( Registry registry ) {
		try {
			if (registry == null || registry.getId() == null) {
				setProject(null);
				loadProjects(null);
			} else {
				loadProjects(registry.getId());
			}
			Integer projectTypeId = null;
			if (getProject() != null && getProject().getProjectType() != null) {
				projectTypeId = getProject().getProjectType().getId();	
			}
			loadActivityTypes(projectTypeId);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar la lista de proyectos.";
			throw new AbortProcessingException(msg,e);
		}
	}

	public void onChangeProject(ActionEvent event) {
		try {
			Registry registry = null;
			if (getProject() != null) {
				registry = getProject().getRegistry();
			} else {
				registry = (Registry) BeanManager.getManagerBean(Registry.class).createNewTo();
			}
			setRegistry(registry);
			LookupChangeEvent e = new LookupChangeEvent(event.getComponent(), registry);
			onRegistryChanged(e);
			Integer projectTypeId = null;
			if (getProject() != null && getProject().getProjectType() != null) {
				projectTypeId = getProject().getProjectType().getId();	
			}
			loadActivityTypes(projectTypeId);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar el proyecto.";
			throw new AbortProcessingException(msg,e);
		}
	}

	public List<SelectItem> getProjects() throws ManagerBeanException {
		if (projects == null) {
			projects = new LinkedList<SelectItem>();
		}
		return projects;
	}

	public List<SelectItem> getActivityTypes() throws ManagerBeanException {
		return activityTypes;
	}
	
	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setRegistry((Registry)BeanManager.getManagerBean(Registry.class).createNewTo());
		setCustomer((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setTaskHolder(null);
		setProject(null);
		setActivityType(null);
		setJobType(null);
		setRegistry(null);
		setRegistryType(CUSTOMER_TYPE);
		loadProjects(null);
		loadActivityTypes(null);
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		super.completeCriteria( criteria );
		DailyTrackingController controller = (DailyTrackingController) FormUtil.getController(IGroupWareConstants.DAILY_TRACKING_CONTROLLER_NAME);
		String taskHolderAlias = getFieldName(IEntityAlias.DAILY_TRACKING_TASK_HOLDER_ID);
		if(!controller.isMonitor()){
			TaskHolder taskHolder = getGroupwareUtils().getCurrentTaskHolder();
			if (taskHolder == null) {
				// No va a encontrar nada.
				criteria.addNullExpression(IEntityAlias.DAILY_TRACKING_TASK_HOLDER_ID);
				
				String msg = "No existe un operario vinculado a la cuenta de acceso. Cree un operario y vincule la cuenta de acceso.";
				AonUtil.addErrorMessage(msg);
				
			} else {
				criteria.addEqualExpression(taskHolderAlias, taskHolder.getId() );	
			}
		} else {
			if (getTaskHolder() != null) {
				criteria.addEqualExpression( taskHolderAlias, getTaskHolder().getId() ); 	
			}
		}
		if ((getRegistry() != null) && (getRegistry().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.DAILY_TRACKING_REGISTRY_ID), getRegistry().getId());
		}		
		if ((getProject() != null) && (getProject().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.DAILY_TRACKING_PROJECT_ID), getProject().getId());
		}		
		if ((getActivityType() != null) && (getActivityType().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.DAILY_TRACKING_ACTIVITY_TYPE_ID), getActivityType().getId());
		}		
		if ((getJobType() != null) && (getJobType().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.DAILY_TRACKING_JOB_TYPE_ID), getJobType().getId());
		}		
	}

	public void onCustomerChanged(LookupChangeEvent event) {
		Customer customer = (Customer) event.getNewValue();
		Registry registry = (customer!=null)?customer.getRegistry():null; 
		registryChanged( registry );
		setRegistry( registry );
	}
	
	public void onRegistryTypeChanged( ActionEvent event ) throws ManagerBeanException {
		switch ( getRegistryType() ) {
			case CUSTOMER_TYPE:
				setCustomer((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
				setRegistry(getCustomer().getRegistry());
				break;
			case COMPANY_TYPE:
				DailyTrackingController controller = (DailyTrackingController) FormUtil.getController(IGroupWareConstants.DAILY_TRACKING_CONTROLLER_NAME);
				Company company = controller.getCompany();
				registryChanged( company );
				setRegistry(company);
				break;
		}
	}
	
}