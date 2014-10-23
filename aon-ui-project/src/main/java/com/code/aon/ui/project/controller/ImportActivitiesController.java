package com.code.aon.ui.project.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
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
import com.code.aon.project.Project;
import com.code.aon.project.ProjectActivity;
import com.code.aon.project.ProjectType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ImportActivitiesController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(ImportActivitiesController.class);
	
	private boolean importPanel;
	private Integer projectId;
	private DataModel model;

	public boolean isImportPanel() {
		return importPanel;
	}

	public void setImportPanel(boolean importPanel) {
		this.importPanel = importPanel;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public DataModel getModel() {
		if (model == null) {
			setModel(new SerializableListDataModel());
		}
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public void onShowImportPanel(ActionEvent event) {
		setProjectId(null);
		setModel(null);
		setImportPanel(true);
	}

	public void onCloseImportPanel(ActionEvent event) {
		setImportPanel(false);
	}

	@SuppressWarnings("unchecked")
	public void onImport(ActionEvent event) {
		int i = 0;
		try {
			List<CustomActivity> list = (List<CustomActivity>) getModel().getWrappedData();
			IManagerBean bean = BeanManager.getManagerBean(ProjectActivity.class);
			for (CustomActivity ca : list) {
				if (ca.isSelected()) {
					ProjectActivity a = new ProjectActivity();
					a.setProject(getProject());
					a.setActivityType(ca.getProjectActivity().getActivityType());
					i++;
					bean.insert(a);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error importing activities", e);
		} finally {
			FormUtil.getController(IProjectConstants.PROJECT_ACTIVITY_CONTROLLER_NAME).onSearch(event);
			AonUtil.addInfoMessage("" + i + " actividades importadas.");			
			setImportPanel(false);
		}
	}

	public List<SelectItem> getProjects() throws ManagerBeanException {
		List<SelectItem> projects = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Project.class);
		Criteria criteria = new Criteria();
		String identifier = bean.getFieldName(IEntityAlias.PROJECT_ID);
		criteria.addNotEqualExpression(identifier, getProject().getId());
		ProjectType projectType = getProject().getProjectType();
		if (projectType == null || projectType.getId() == null) {
			criteria.addNullExpression( bean.getFieldName(IEntityAlias.PROJECT_PROJECT_TYPE_ID));	
		} else {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_PROJECT_TYPE_ID),projectType.getId());
		}
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_NAME));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to:list) {
			Project project = (Project) to;
			projects.add(getProjectSelectItem(project));
		}
		return projects;
	}

	private SelectItem getProjectSelectItem(Project project) {
		StringBuilder sb = new StringBuilder(project.getName());
		sb.append(" (");
		String alias = StringUtils.isNotEmpty(project.getRegistry().getAlias())?
				project.getRegistry().getAlias() :
				StringUtils.abbreviate(project.getRegistry().getFullName(), 25);			
		sb.append(alias);
		sb.append(")");
		if (!project.isActive()) {
			sb.append(" ( inactv.)");
		}
		return new SelectItem(project.getId(), sb.toString());
	}

	public void onChangeProject(ActionEvent event) {
		List<CustomActivity> list = new LinkedList<CustomActivity>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ProjectActivity.class);
			Criteria criteria = new Criteria();
			String alias = bean.getFieldName(IEntityAlias.PROJECT_ACTIVITY_PROJECT_ID);
			criteria.addEqualExpression(alias, getProjectId());
			List<ITransferObject> beanList = bean.getList(criteria);
			for (ITransferObject to:beanList) {
				ProjectActivity act = (ProjectActivity) to;
				CustomActivity ca = new CustomActivity();
				ca.setProjectActivity(act);
				ca.setSelected( ca.isClickable() );
				list.add(ca);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error loading projects", e);
		}
		setModel(new SerializableListDataModel(list));
	}

	
	private Project getProject() {
		return (Project) FormUtil.getController(IProjectConstants.PROJECT_CONTROLLER_NAME).getTo();
	}

	public class CustomActivity {
		private boolean selected;
		private ProjectActivity projectActivity;

		@SuppressWarnings("unchecked")
		public boolean isClickable() {
			try {
				IController controller =  FormUtil.getController(IProjectConstants.PROJECT_ACTIVITY_CONTROLLER_NAME);	
				List<ProjectActivity> list = (List<ProjectActivity>) controller.getModel().getWrappedData();
				for (ProjectActivity pa : list ){
					if (this.projectActivity.getActivityType().getId().equals(pa.getActivityType().getId())) {
						return false;
					}
				}
				Integer a = null;
				if (this.projectActivity.getActivityType().getProjectType() != null) {
					a = this.projectActivity.getActivityType().getProjectType().getId();
				}
				Integer b = getProject().getProjectType().getId();
				return (a== null || a.equals(b));
			} catch (ManagerBeanException e) {
				return false;
			}
		}
		
		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public ProjectActivity getProjectActivity() {
			return projectActivity;
		}

		public void setProjectActivity(ProjectActivity projectActivity) {
			this.projectActivity = projectActivity;
		}

	}
}
