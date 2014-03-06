package com.code.aon.ui.groupware.controller;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.WorkGroup;
import com.code.aon.groupware.Campaign;
import com.code.aon.groupware.CampaignProject;
import com.code.aon.groupware.CampaignType;
import com.code.aon.groupware.Process;
import com.code.aon.groupware.ProcessDetail;
import com.code.aon.groupware.enumeration.CampaignStatus;
import com.code.aon.groupware.task.TaskManager;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Project;
import com.code.aon.project.ProjectActivity;
import com.code.aon.project.ProjectType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.project.controller.IProjectConstants;
import com.code.aon.ui.project.controller.ProjectCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProcessLauncherWizard implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessLauncherWizard.class);
	private static final String[] STEPS = { "process_wizard_step0","process_wizard_step1", "process_wizard_step2","process_wizard_step3" };

	private int currentStep;
	private Process process;
	private String description;
	private Date startDate;
	private Date endDate;
	private WorkGroup workGroup;
	private CampaignType type;
	private CampaignStatus status;
	private List<CampaignProject> projects;
	private DataModel projectsModel;

	private Registry registry;
	private Project project;
	private ProjectType projectType;
	private ActivityType activityType;
	private String projectName;

	private List<SelectItem> availableProjects;
	private TaskManager taskManager;

	private TaskManager getTaskManager() {
		if (taskManager == null) {
			taskManager = new TaskManager();
		}
		return taskManager;
	}

	public int getCurrentStep() {
		return currentStep;
	}
	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public Process getProcess() {
		return process;
	}
	public void setProcess(Process process) {
		this.process = process;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public WorkGroup getWorkGroup() {
		return workGroup;
	}
	public void setWorkGroup(WorkGroup workGroup) {
		this.workGroup = workGroup;
	}

	public CampaignType getType() {
		return type;
	}
	public void setType(CampaignType type) {
		this.type = type;
	}

	public CampaignStatus getStatus() {
		return status;
	}
	public void setStatus(CampaignStatus status) {
		this.status = status;
	}

	public Registry getRegistry() {
		return registry;
	}
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public ProjectType getProjectType() {
		return projectType;
	}
	public void setProjectType(ProjectType projectType) {
		this.projectType = projectType;
	}

	public ActivityType getActivityType() {
		return activityType;
	}
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public List<SelectItem> getAvailableProjects() {
		if (availableProjects == null) {
			try {
				loadAvailableProjects();
			} catch (ManagerBeanException e) {
				String msg = "Error al buscar proyectos.";
				LOGGER.error(msg,e);
				AonUtil.addErrorMessage(msg);
				availableProjects = new LinkedList<SelectItem>();
			}
		}
		return availableProjects;
	}
	public void setAvailableProjects(List<SelectItem> availableProjects) {
		this.availableProjects = availableProjects;
	}

	private void loadAvailableProjects() throws ManagerBeanException {
		setAvailableProjects(new LinkedList<SelectItem>());
		IManagerBean bean = BeanManager.getManagerBean(Project.class);
		Criteria criteria = new Criteria();
		if (getRegistry() != null) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID),getRegistry().getId());
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_ACTIVE),true);
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID));
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_NAME));
		Iterator<?> iterator = bean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			Project project = (Project) iterator.next();
			StringBuilder sb = new StringBuilder(project.getName());
			if (getRegistry() == null) {
				sb.append(" (");
				sb.append(StringUtils.isNotBlank(project.getRegistry().getAlias()) ? project.getRegistry().getAlias() : project.getRegistry().getFullName() );
				sb.append(")");
			}
			SelectItem item = new SelectItem(project, sb.toString());
			availableProjects.add(item);
		}
	}

	public List<CampaignProject> getProjects() {
		if (projects == null) {
			projects = new ArrayList<CampaignProject>();
		}
		return projects;
	}
	public void setProjects(List<CampaignProject> projects) {
		this.projects = projects;
	}

	public DataModel getProjectsModel() {
		if (projectsModel == null) {
			projectsModel = new SerializableListDataModel(getProjects());
		}
		return projectsModel;
	}
	public void setProjectsModel(DataModel projectsModel) {
		this.projectsModel = projectsModel;
	}

	public List<SelectItem> getAvailableActivityTypes() throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) AonUtil.getRegisteredBean(IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME);
		if (getProjectType() != null && getProjectType().getId() != null) {
			return pcc.getActivityTypes(getProjectType().getId());	
		}
		return pcc.getActivityTypes( );
	}

	private void initializeController() throws ManagerBeanException {
		setProcess(null);
		setDescription(null);
		setStartDate(new Date());
		setEndDate(new Date());
		setWorkGroup(null);
		setType(null);
		setStatus(CampaignStatus.IN_PROGRESS);
		setRegistry( (Registry) BeanManager.getManagerBean(Registry.class).createNewTo() );
		setProject(null);
		setProjectType(null);
		setActivityType(null);
		setProjectName(null);
		setProjects(null);
		setProjectsModel(null);
		setAvailableProjects(null);
	}

	private String determineStep() {
		if (getProcess() == null) {
			setCurrentStep(0);
		} else if (getDescription() == null || getStartDate() == null || getEndDate() == null || getWorkGroup() == null) {
			setCurrentStep(1);
		} else {
			setCurrentStep(2);
		}
		return STEPS[getCurrentStep()];
	}

	public String start() {
		try {
			initializeController();
			return determineStep();
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar el lanzador.";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg,e);
			throw new AbortProcessingException(msg,e);
		}
		
	}

	public String startWithProcess() {
		try {
			initializeController();
			return determineStep();
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar el lanzador.";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg,e);
			throw new AbortProcessingException(msg,e);
		}
	}

	public String previous() {
		setCurrentStep(getCurrentStep() - 1);
		return STEPS[getCurrentStep()];
	}

	public String next() {
		setCurrentStep(getCurrentStep() + 1);
		return STEPS[getCurrentStep()];
	}

	public boolean isPreviousAvailable() {
		return (getCurrentStep() > 0);
	}

	public boolean isNextAvailable() {
		return (getCurrentStep() < 3);
	}

	public boolean isFinaliceAvailable() {
		return (getCurrentStep() == 3);
	}

	public void processChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			if (getDescription() == null) {
				Process process = (Process) event.getNewValue();
				setDescription(process.getDescription());
			}
		}
	}

	public void registryChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null) {
			setAvailableProjects(null);
		}
	}

	public void projectChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			if (getRegistry() == null) {
				Project project = (Project) event.getNewValue();
				Registry c = project.getRegistry();
				setRegistry(c);
				LookupChangeEvent e = new LookupChangeEvent(event
						.getComponent(), c);
				registryChanged(e);
			}
		}
	}

	public void addExistingProject(ActionEvent event) {
		try {
			if (getProject() != null) {
				addProject(getProject());
			} else {
				if (getActivityType() == null) {
					IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
					Criteria criteria = new Criteria();
					if (getRegistry() != null && getRegistry().getId() != null) {
						criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID),getRegistry().getId());
					}
					if (getProjectType() != null && getProjectType().getId() != null) {
						criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_PROJECT_TYPE_ID),getProjectType().getId());
					}
					if (!StringUtils.isBlank(getProjectName())) {
						criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_NAME),getProjectName());
					}
					criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_ACTIVE), true);
					List<ITransferObject> list = projectBean.getList(criteria);
					for (ITransferObject to : list) {
						Project d = (Project) to;
						addProject(d);
					}
				} else {
					IManagerBean bean = BeanManager.getManagerBean(ProjectActivity.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_ACTIVITY_ACTIVITY_TYPE_ID),getActivityType().getId());
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_ACTIVITY_ACTIVE),true);
					criteria.addEqualExpression("ProjectActivity.project.active",true);
					if (getRegistry() != null && getRegistry().getId() != null) {
						criteria.addEqualExpression("ProjectActivity.project.registry.id", getRegistry().getId());
					}
					List<ITransferObject> list = bean.getList(criteria);
					for (ITransferObject to : list) {
						ProjectActivity a = (ProjectActivity) to;
						addProject(a.getProject());
					}
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible realizar la búsqueda de expedientes";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg,e);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void addNewProject(ActionEvent event) {
		try {
			Project project = new Project();
			project.setRegistry(getRegistry());
			project.setProjectType(getProjectType());
			project.setName(getProjectName());
			addProject(project);
		} catch (ManagerBeanException e) {
			String msg = "Error al añadir el expediente.";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg,e);
			throw new AbortProcessingException(msg, e);
		}
	}

	private void addProject(Project dossier) throws ManagerBeanException {
		CampaignProject cd = new CampaignProject();
		cd.setProject(dossier);
		if (validate(cd)) {
			getProjects().add(cd);
			setRegistry( (Registry) BeanManager.getManagerBean(Registry.class).createNewTo() );
			setAvailableProjects(null);
		}
	}

	private boolean validate(CampaignProject cd) {
		if (cd.getProject() == null) {
			String msg = "Proyecto no puede estar vacio!";
			AonUtil.addErrorMessage(msg);
			return false;
		}
		if (cd.getProject().getRegistry() == null) {
			String msg = "Debe asignar el proyecto!";
			AonUtil.addErrorMessage(msg);
			return false;
		}
		if (StringUtils.isEmpty(cd.getProject().getName())) {
			String msg = "El nombre del proyecto no puede estar vacio!";
			AonUtil.addErrorMessage(msg);
			return false;
		}
//		if (cd.getProject().getProjectType() == null) {
//			String msg = "Tipo de proyecto no puede estar vacio!";
//			AonUtil.addErrorMessage(msg);
//			return false;
//		}
		return true;
	}

	public void removeProject(ActionEvent event) {
		int index = getProjectsModel().getRowIndex();
		List<?> list = (List<?>) getProjectsModel().getWrappedData();
		list.remove(index);
	}

	public String finalice() {
		try {
			IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
			IManagerBean campaignProjectBean = BeanManager.getManagerBean(CampaignProject.class);
			IManagerBean campaignBean = BeanManager.getManagerBean(Campaign.class);
			IManagerBean processDetailBean = BeanManager.getManagerBean(ProcessDetail.class);

			Criteria criteria = new Criteria();
			String alias = processDetailBean.getFieldName(IEntityAlias.PROCESS_DETAIL_PROCESS_ID);
			criteria.addEqualExpression(alias, getProcess().getId());
			criteria.addOrder(processDetailBean.getFieldName(IEntityAlias.PROCESS_DETAIL_POSITION));
			List<?> list = processDetailBean.getList(criteria);
			if (list.size() == 0) {
				String msg = "El proceso seleccionado no tiene acciones";
				AonUtil.addErrorMessage(msg);
			} else {

				ProcessDetail pd = (ProcessDetail) list.get(0);

				Campaign c = new Campaign();
				c.setDescription(getDescription());
				c.setEndDate(getEndDate());
				c.setProcess(getProcess());
				c.setStartDate(getStartDate());
				c.setStatus(getStatus());
				c.setManual(true);
				c.setCampaignType(getType());
				c.setWorkGroup(getWorkGroup());
				c = (Campaign) campaignBean.insert(c);
				int i = 0;
				for (CampaignProject cd : projects) {
					if (cd.getProject().getId() == null) {
						Project d = cd.getProject();
						d.setActive(true);
						d = (Project) projectBean.insert(d);
						cd.setProject(d);
					}
					cd.setCampaign(c);
					campaignProjectBean.insert(cd);
					getTaskManager().addProcessTask(null, c, pd, cd);
					i++;					
				}
				AonUtil.addInfoMessage("Proceso lanzado correctamente. " + i + " tareas creadas");
				setCurrentStep(0);
				return start();
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			LOGGER.error(e.getMessage(),e);
			throw new AbortProcessingException(e);
		}
		return null;
		
	}

}
