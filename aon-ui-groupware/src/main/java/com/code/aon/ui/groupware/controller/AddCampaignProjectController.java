package com.code.aon.ui.groupware.controller;

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

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.Campaign;
import com.code.aon.groupware.CampaignProject;
import com.code.aon.groupware.Process;
import com.code.aon.groupware.ProcessDetail;
import com.code.aon.groupware.task.TaskManager;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Project;
import com.code.aon.project.ProjectActivity;
import com.code.aon.project.ProjectType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AddCampaignProjectController extends DataScrollerState {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AddCampaignProjectController.class);
	
	private boolean addPanelVisible;
	private List<CampaignProject> checked;
	private List<CampaignProject> projects;
	private List<SelectItem> availableProjects;
	
	private TaskManager taskManager;
	private Registry registry;
	private Project project;
	private ProjectType projectType;
	private ActivityType activityType;
	private String projectName;
	
	private TaskManager getTaskManager() {
		if (taskManager == null) {
			taskManager = new TaskManager();
		}
		return taskManager;
	}

	public List<CampaignProject> getChecked() {
		if (checked == null) {
			setChecked( new LinkedList<CampaignProject>());
		}
		return checked;
	}
	public void setChecked(List<CampaignProject> checked) {
		this.checked = checked;
	}

	public List<CampaignProject> getProjects() {
		if (projects == null) {
			setProjects( new LinkedList<CampaignProject>());
		}
		return projects;
	}
	public void setProjects(List<CampaignProject> projects) {
		this.projects = projects;
	}

	public DataModel getModel() {
		if (getDirectModel() == null) {
			setModel(new SerializableListDataModel(getProjects()));
		}
		return getDirectModel();
	}

	public boolean isAddPanelVisible() {
		return addPanelVisible;
	}
	public void setAddPanelVisible(boolean addPanelVisible) {
		this.addPanelVisible = addPanelVisible;
	}
		
	public void showAddPanel(ActionEvent event) {
		setAddPanelVisible(true);
		initialize();
	}
	public void hideAddPanel(ActionEvent event) {
		setAddPanelVisible(false);
		initialize();
	}
	
	private void initialize() {
		try {
			setProjects(null);
			setChecked(null);
			setModel(null);
			setRegistry( (Registry) BeanManager.getManagerBean(Registry.class).createNewTo() );
			setProject(null);
			setProjectType(null);
			setActivityType(null);
			setProjectName(null);
			setAvailableProjects(null);
		} catch (ManagerBeanException e) {
			String msg = "Imposible inicializar el objeto 'Registry'";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	public boolean getRowChecked() {
    	CampaignProject to = (CampaignProject) getDirectModel().getRowData();
        return getChecked().contains(to);
    }
    public void setRowChecked(boolean rowChecked) {
        if (rowChecked) {
        	CampaignProject to = (CampaignProject) getDirectModel().getRowData();
            if (!getChecked().contains(to)) {
            	getChecked().add(to);
            }
        } else {
        	Project to = (Project) getDirectModel().getRowData();
            if (getChecked().contains(to)) {
            	getChecked().remove(to);
            }
        }
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

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

    public void onAddCampaignProjects(ActionEvent event) {
        try {
            IManagerBean campaignProjectBean = BeanManager.getManagerBean(CampaignProject.class);
            ProcessDetail processDetail = getFirstProcessDetail();
            for (CampaignProject campaignProject: getProjects()) {
                campaignProject = (CampaignProject) campaignProjectBean.insert(campaignProject);
                getTaskManager().addProcessTask(null, campaignProject.getCampaign(), processDetail, campaignProject);
            }
            CampaignProjectController campaignProjectController = (CampaignProjectController)FormUtil.getController("campaignProject");
            campaignProjectController.onSearch(null);
            hideAddPanel(event);
        } catch (ManagerBeanException e) {
            LOGGER.error(e.getMessage(), e);
            AonUtil.addErrorMessage(e.getMessage());
        	throw new AbortProcessingException("La campaña no tiene proceso seleccionado");
        }
    }

	private ProcessDetail getFirstProcessDetail() throws ManagerBeanException {
        Campaign campaign = (Campaign)FormUtil.getController( IGroupWareConstants.CAMPAIGN_CONTROLLER_NAME).getTo();
        if (campaign == null) {
        	throw new ManagerBeanException("No es posible encontrar la campaña");
        }
        Process process = campaign.getProcess();
        if (process== null) {
        	throw new ManagerBeanException("La campaña no tiene proceso seleccionado");
        }
        IManagerBean bean = BeanManager.getManagerBean(ProcessDetail.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_DETAIL_PROCESS_ID), process.getId());
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_DETAIL_ACTIVE), true);
        criteria.addOrder(bean.getFieldName(IEntityAlias.PROCESS_DETAIL_POSITION));
        List<ITransferObject> list =  bean.getList(criteria);
        if (list != null && list.size()>0) {
        	return (ProcessDetail) list.get(0);
        }
    	throw new ManagerBeanException("El proceso no tiene detalles definidos.");
	}

	public void addExistingProject(ActionEvent event) {
		try {
	        Campaign campaign = (Campaign)FormUtil.getController("campaign").getTo();
			if (getProject() != null) {
				addProject(campaign,getProject());	
			} else {
				if (getActivityType() == null) {
					IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
					Criteria criteria = new Criteria();
					if (getRegistry() != null && getRegistry().getId() != null) {
						criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID), getRegistry().getId());
					}
					if (getProjectType() != null && getProjectType().getId() != null) {
						criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_PROJECT_TYPE_ID), getProjectType().getId());
					}
					if (!StringUtils.isBlank(getProjectName())) {
						criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_NAME), getProjectName());
					}
					criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_ACTIVE), true);
					List<ITransferObject> list = projectBean.getList(criteria);
					for (ITransferObject to:list) {
						Project d = (Project) to;
						addProject(campaign,d);	
					}
				} else {
					IManagerBean activityBean = BeanManager.getManagerBean(ProjectActivity.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(activityBean.getFieldName(IEntityAlias.PROJECT_ACTIVITY_ACTIVITY_TYPE_ID), getActivityType().getId());
					criteria.addEqualExpression(activityBean.getFieldName(IEntityAlias.PROJECT_ACTIVITY_ACTIVE), true);
					if (getRegistry() != null && getRegistry().getId() != null) {
						criteria.addEqualExpression("ProjectActivity.project.registry.id", getRegistry().getId());
					}
					List<ITransferObject> list = activityBean.getList(criteria);
					for (ITransferObject to:list) {
						ProjectActivity a = (ProjectActivity) to;
						addProject(campaign,a.getProject());
					}
				}
			}
		}catch (ManagerBeanException e) {
			String msg = "Imposible realizar la búsqueda de expedientes";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
    
	private void addProject(Campaign campaign,Project project) throws ManagerBeanException {
		// Si el project ya está añadido en la campaña, se excluye.
		IManagerBean cdBean = BeanManager.getManagerBean(CampaignProject.class);
		Criteria c = new Criteria();
		c.addEqualExpression(cdBean.getFieldName(IEntityAlias.CAMPAIGN_PROJECT_CAMPAIGN_ID), campaign.getId());
		c.addEqualExpression(cdBean.getFieldName(IEntityAlias.CAMPAIGN_PROJECT_PROJECT_ID), project.getId());
		List<ITransferObject> ex = cdBean.getList(c);
		if (ex.size() == 0) {
			CampaignProject cd = new CampaignProject();
			cd.setCampaign(campaign);
			cd.setProject(project);
			boolean added = false;
			for (CampaignProject saved: getProjects()) {
				if (saved.getProject().equals(cd.getProject())){
					added = true;
				}
			}
			if (!added) {
				getProjects().add(cd);	
			}
		}

	}
	public List<SelectItem> getAvailableProjects() {
		if (availableProjects == null) {
			try {
				loadAvailableProjects();
			}catch (ManagerBeanException e) {
				String msg = "Error al buscar proyectos." + e.getMessage();
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
		IManagerBean managerBean = BeanManager.getManagerBean(Project.class);
		Criteria criteria = new Criteria();
		if (getRegistry() != null) {
			criteria.addEqualExpression(managerBean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID), getRegistry().getId());
		}
		criteria.addEqualExpression(managerBean.getFieldName(IEntityAlias.PROJECT_ACTIVE), true);
		criteria.addOrder(managerBean.getFieldName(IEntityAlias.PROJECT_NAME));
		criteria.addOrder(managerBean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID));
		List<ITransferObject> list = managerBean.getList(criteria);
		for (ITransferObject to : list ) {
			Project project = (Project) to;
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
	
	public void projectChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			if (getRegistry() == null) {
				Project project = (Project) event.getNewValue();
				Registry rgtry = project.getRegistry();
				setRegistry(rgtry);
				LookupChangeEvent e = new LookupChangeEvent(event.getComponent(), rgtry);
				registryChanged(e);
			}
		}
	}
	
	public void registryChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null) {
			setAvailableProjects(null);
		}
	}

	public void removeProject(ActionEvent event) {
		int index = getModel().getRowIndex();
		List<?> list = (List<?>) getModel().getWrappedData();
		list.remove(index);
	}
   
}