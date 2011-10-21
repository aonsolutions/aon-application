package com.code.aon.ui.groupware.controller;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.util.CompanyUtil;
import com.code.aon.groupware.Task;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.groupware.TaskHolderWorkgroup;
import com.code.aon.groupware.dao.IGroupwareAlias;
import com.code.aon.groupware.enumeration.TaskStatus;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.groupware.GroupwareUtils;
import com.code.aon.ui.project.controller.IProjectConstants;
import com.code.aon.ui.project.controller.ProjectCollectionsController;
import com.code.aon.ui.util.AonUtil;

public class GanttController {

	private boolean monitor;

	private Date fromStartDate;
	private Date toStartDate;
	private Date fromDueDate;
	private Date toDueDate;
	private TaskHolder taskHolder;
	private boolean groupTasksVisible;
	private Registry registry;
	private Project project;
	private boolean statusPending;
	private boolean statusInProgress;
	private boolean statusFinished;

	private boolean diagramVisible;

	private  List<SelectItem> projects;
	private GroupwareUtils groupwareUtils;
	
	public GroupwareUtils getGroupwareUtils() {
		if (groupwareUtils == null) {
			groupwareUtils = new GroupwareUtils();
		}
		return groupwareUtils;
	}

	
	public boolean isDiagramVisible() {
		return diagramVisible;
	}
	public void setDiagramVisible(boolean diagramVisible) {
		this.diagramVisible = diagramVisible;
	}

	public boolean isMonitor() {
		return monitor;
	}
	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}

	public Date getFromStartDate() {
		return fromStartDate;
	}
	public void setFromStartDate(Date fromStartDate) {
		this.fromStartDate = fromStartDate;
	}

	public Date getToStartDate() {
		return toStartDate;
	}
	public void setToStartDate(Date toStartDate) {
		this.toStartDate = toStartDate;
	}

	public Date getFromDueDate() {
		return fromDueDate;
	}
	public void setFromDueDate(Date fromDueDate) {
		this.fromDueDate = fromDueDate;
	}

	public Date getToDueDate() {
		return toDueDate;
	}
	public void setToDueDate(Date toDueDate) {
		this.toDueDate = toDueDate;
	}

	public boolean isStatusPending() {
		return statusPending;
	}
	public void setStatusPending(boolean statusPending) {
		this.statusPending = statusPending;
	}

	public boolean isStatusInProgress() {
		return statusInProgress;
	}
	public void setStatusInProgress(boolean statusInProgress) {
		this.statusInProgress = statusInProgress;
	}

	public boolean isStatusFinished() {
		return statusFinished;
	}
	public void setStatusFinished(boolean statusFinished) {
		this.statusFinished = statusFinished;
	}

	public TaskHolder getTaskHolder() {
		return taskHolder;
	}
	public void setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
	}
	
	public boolean isGroupTasksVisible() {
		return groupTasksVisible;
	}
	public void setGroupTasksVisible(boolean groupTasksVisible) {
		this.groupTasksVisible = groupTasksVisible;
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
	
	private void loadProjects(Integer registryId) throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) 
		AonUtil.getRegisteredBean( IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME );
		projects = pcc.getProjects( registryId);
	}
	public void onRegistryChanged(LookupChangeEvent event) {
		try {
			Registry registry = (Registry) event.getNewValue();
			if (registry == null || registry.getId() == null) {
				setProject(null);
				loadProjects(null);
			} else {
				loadProjects(registry.getId());
			}
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
	
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		IManagerBean bean = BeanManager.getManagerBean(Task.class);
		CompanyUtil companyUtil = new CompanyUtil(); 
		criteria.addEqualExpression(bean.getFieldName(IGroupwareAlias.TASK_ENTERPRISE_ID), companyUtil.getActiveEnterprise().getId() );
		if(!isMonitor()){
			setTaskHolder(getGroupwareUtils().getCurrentTaskHolder());
		}
		if (getTaskHolder() != null) {
			if (isGroupTasksVisible()) {
				String taskHolderAlias = bean.getFieldName(IGroupwareAlias.TASK_TASK_HOLDER_ID);
				Expression userExpr = ExpressionUtilities.getEqualExpression(taskHolderAlias, taskHolder.getId() );
				Expression workGroupExpr = obtainTaskHolderWorkGroupsExpression(taskHolder, bean.getFieldName(IGroupwareAlias.TASK_WORK_GROUP_ID));
				Expression groupExpr = ExpressionUtilities.getNullExpression(taskHolderAlias);
				workGroupExpr = ExpressionUtilities.getAndExpression(workGroupExpr, groupExpr);
				criteria.addExpression(ExpressionUtilities.getOrExpression(userExpr, workGroupExpr));
			} else {
				criteria.addEqualExpression( bean.getFieldName(IGroupwareAlias.TASK_TASK_HOLDER_ID), getTaskHolder().getId() ); 	
			}
		}
		
		if (getFromStartDate() != null) {
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IGroupwareAlias.TASK_START_DATE), getFromStartDate());
		}
		if (getToStartDate() != null) {
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IGroupwareAlias.TASK_START_DATE), getToStartDate());
		}
		if (getFromDueDate() != null) {
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IGroupwareAlias.TASK_DUE_DATE), getFromDueDate());
		}
		if (getToDueDate() != null) {
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IGroupwareAlias.TASK_DUE_DATE), getToDueDate());
		}
		if ((getRegistry() != null) && (getRegistry().getId() != null)) {
			criteria.addEqualExpression(bean.getFieldName(IGroupwareAlias.TASK_REGISTRY_ID), getRegistry().getId());
		}		
		if ((getProject() != null) && (getProject().getId() != null)) {
			criteria.addEqualExpression(bean.getFieldName(IGroupwareAlias.TASK_PROJECT_ID), getProject().getId());
		}	
		loadStatusCriteria(bean.getFieldName(IGroupwareAlias.TASK_STATUS),criteria);
		criteria.addOrder(bean.getFieldName(IGroupwareAlias.TASK_START_DATE));
	}
	
    private Expression obtainTaskHolderWorkGroupsExpression(TaskHolder taskHolder, String alias) throws ManagerBeanException {
        Expression expression = null;
        IManagerBean bean = BeanManager.getManagerBean(TaskHolderWorkgroup.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(bean.getFieldName(IGroupwareAlias.TASK_HOLDER_WORKGROUP_TASK_HOLDER_ID), taskHolder.getId());
        List<ITransferObject> list = bean.getList(criteria);
        for (ITransferObject to: list ) {
        	TaskHolderWorkgroup thwg = (TaskHolderWorkgroup) to;
            expression = ExpressionUtilities.getOrExpression(expression, ExpressionUtilities.getEqualExpression(alias, thwg.getWorkGroup().getId()));
        }
        return expression;
    }

	private void loadStatusCriteria(String statusAlias,Criteria criteria) throws ManagerBeanException {
		if (isStatusFinished() || isStatusInProgress() || isStatusPending()) {
			// Hay que realizar una expression OR con los valores
			// seleccionados. Como hay
			// cuatro valores de status creamos un array con esas
			// dimensiones y asignamos
			// las expresiones correspondientes al array.
			Expression[] exps = { null, null, null, null };
			int count = 0;
			int inCaseCount1 = -1;
			if (isStatusFinished()) {
				exps[1] = ExpressionUtilities.getEqualExpression(statusAlias,TaskStatus.FINISHED);
				count++;
				inCaseCount1 = 1;
			}
			if (isStatusInProgress()) {
				exps[2] = ExpressionUtilities.getEqualExpression(statusAlias,TaskStatus.IN_PROGRESS);
				count++;
				inCaseCount1 = 2;
			}
			if (isStatusPending()) {
				exps[3] = ExpressionUtilities.getEqualExpression(statusAlias,TaskStatus.PENDING);
				count++;
				inCaseCount1 = 3;
			}
			addOrExpression(criteria,exps,count,inCaseCount1);
		}
	}
	
	private void addOrExpression(Criteria criteria, Expression[] exps,int count, int inCaseCount1) {
		Expression expToAdd = null;
		if (count == 1) {
			expToAdd = exps[inCaseCount1];
		} else {
			// Si count > 1 hay que hacer una OR Expression
			boolean ready = false;
			for (int i = 0; i < exps.length; i++) {
				if (exps[i] != null) {
					if (!ready) {
						expToAdd = exps[i];
						ready = true;
					} else {
						expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exps[i]);
					}
				}
			}
		}
		criteria.addExpression(expToAdd);
	}
	
	public void onLaunch(ActionEvent event) {
		try {
			setFromStartDate(null);
			setToStartDate(null);
			setFromDueDate(null);
			setToDueDate(null);
			setRegistry((Registry)BeanManager.getManagerBean(Registry.class).createNewTo());
			setProject(null);
			setStatusPending(true);
			setStatusInProgress(true);
			setStatusFinished(false);
			if (!isMonitor()) {
				setTaskHolder( getGroupwareUtils().getCurrentTaskHolder() );
			} else {
				setTaskHolder(null);
			}
			setGroupTasksVisible(false);
			setDiagramVisible(false);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo inicializar el diagrama de Gantt";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void onShowDiagram( ActionEvent event) {
		setDiagramVisible(false);
		if (!isStatusFinished() && !isStatusInProgress() && !isStatusPending()) {
			String msg = "Seleccione al menos un estado para el diagrama";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if (isMonitor()) {
			if (getProject() == null && getTaskHolder() == null && (getRegistry() == null || (getRegistry() != null && getRegistry().getId() == null)) ) {
				String msg = "Seleccione datos para el diagrama";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}			
		}
		setDiagramVisible(true);
	}

	public Date getLastModified() {
		return new Date();
	}
	
	public void paintGanttChart(OutputStream out, Object data)
			throws IOException, ManagerBeanException, ExpressionException {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Locale locale = ctx.getViewRoot().getLocale();
		IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
		Criteria criteria = new Criteria();
		completeCriteria(criteria);
		List<ITransferObject> list = taskBean.getList(criteria);
		TaskSeries s1 = new TaskSeries(TaskStatus.PENDING.getName(locale));
		TaskSeries s2 = new TaskSeries(TaskStatus.IN_PROGRESS.getName(locale));
		TaskSeries s3 = new TaskSeries(TaskStatus.FINISHED.getName(locale));
		int i = 1;
		if (list.size() <= 500) {
			for (ITransferObject to : list) {
				Task task = (Task) to;
				Date start = task.getStartDate();
				Date end = task.getEndDate() == null ? task.getDueDate() : task.getEndDate();
				if (end.before(start)) {
					start = CommonUtil.getMonthFirstDay(end);
				}
				org.jfree.data.gantt.Task ganttTask = new org.jfree.data.gantt.Task("" + i + ".-" + task.getDescription(), start, end);
				(task.isFinished() ? s3 : (task.isInProgress() ? s2 : s1)).add(ganttTask);
				++i;
			}
		}
		TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(s1);
		collection.add(s2);
		collection.add(s3);
		JFreeChart chart = ChartFactory.createGanttChart(null, null, null, collection, true, false, false);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setSeriesPaint(1, Color.MAGENTA);
		renderer.setSeriesPaint(2, Color.GREEN);		
		plot.setRangeGridlinePaint(new Color(150,150,150));
		plot.setBackgroundPaint(Color.WHITE);
		plot.setNoDataMessage(
				list.size() > 500 ?
					"Se han solicitado " + list.size() + " tareas. No se pudo inicializar el diagrama de Gantt, acote los parámetros de consulta.":			
					AonUtil.getMessage("bundle","aon_search_no_results"));
		int width =1024;
		int height = i * 14;
		height += 100; 
		height = height < 125 ? 125 : height;
		float quality = 1;
		ChartUtilities.writeChartAsJPEG(out, quality, chart, width, height);
	}
	
}
