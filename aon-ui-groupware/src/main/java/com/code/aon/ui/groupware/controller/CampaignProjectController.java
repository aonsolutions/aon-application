package com.code.aon.ui.groupware.controller;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.groupware.Campaign;
import com.code.aon.groupware.CampaignProject;
import com.code.aon.groupware.Process;
import com.code.aon.groupware.ProcessDetail;
import com.code.aon.groupware.ProcessTask;
import com.code.aon.groupware.Task;
import com.code.aon.groupware.task.TaskManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.groupware.GroupwareUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CampaignProjectController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(CampaignProjectController.class);

	private Criteria mainCriteria;
	private String sortColumn;
	private boolean ascending;
	private DataModel extendedModel;
	private ArrayList<CampaignProjectExtended> checks = new ArrayList<CampaignProjectExtended>();
	private List<SelectItem> processDetailList;
	private Integer processCount;

	private TaskManager taskManager;
	private GroupwareUtils groupwareUtils;

	private TaskManager getTaskManager() {
		if (taskManager == null) {
			taskManager = new TaskManager();
		}
		return taskManager;
	}
	
	public GroupwareUtils getGroupwareUtils() {
		if (groupwareUtils == null) {
			groupwareUtils = new GroupwareUtils();
		}
		return groupwareUtils;
	}

	public Criteria getMainCriteria() {
		return mainCriteria;
	}

	public void setMainCriteria(Expression expression) {
		this.mainCriteria = new Criteria();
		this.mainCriteria.addExpression(expression);
	}

	public String getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(String sortColumn) {
		if (this.sortColumn == sortColumn) {
			setAscending(!isAscending());
		} else {
			setAscending(true);
		}
		this.sortColumn = sortColumn;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public void sort(ActionEvent event) {
		try {
			Criteria criteria = new Criteria();
			criteria.addExpression(getMainCriteria().getExpression());
			criteria.addOrder(getFieldName(getSortColumn()), isAscending());
			setSortColumn(getSortColumn());
			setCriteria(criteria);
			onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error sorting campaign Project list", e);
		}
	}

	public boolean getRowChecked() {
		try {
			CampaignProjectExtended to = (CampaignProjectExtended) getExtendedModel().getRowData();
			return checks.contains(to);
		} catch (ManagerBeanException e) {
			return false;
		}
	}

	public void setRowChecked(boolean rowChecked) {
		try {
			CampaignProjectExtended to = (CampaignProjectExtended) getExtendedModel().getRowData();
			if (rowChecked) {
				if (!checks.contains(to)) {
					checks.add(to);
				}
			} else {
				if (checks.contains(to)) {
					checks.remove(to);
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public void onRemoveCampaignProject(ActionEvent event) {
		try {
			removeCampaignProject();
        } catch (ManagerBeanException e) {
			String msg = "Error al borrar. " + e.getMessage();
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
        }
	}

	private void removeCampaignProject() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(CampaignProject.class);
		Iterator<CampaignProjectExtended> iterator = checks.iterator();
		while (iterator.hasNext()) {
			CampaignProjectExtended cde = iterator.next();
			CampaignProject campaignProject = cde.getCampaignProject();
			getTaskManager().removeCampaignTask(campaignProject, getGroupwareUtils().getCurrentTaskHolder());
			bean.remove(campaignProject);
		}
		onSearch(null);
		checks = new ArrayList<CampaignProjectExtended>();
	}

	@SuppressWarnings("unchecked")
	public DataModel getExtendedModel() throws ManagerBeanException {
		if (extendedModel == null) {
			Campaign campaign = (Campaign) getMasterController().getTo();
			IManagerBean b = BeanManager.getManagerBean(ProcessDetail.class);
			Criteria c = new Criteria();
			c.addEqualExpression(b.getFieldName(IEntityAlias.PROCESS_DETAIL_PROCESS_ID), campaign.getProcess()
					.getId());
			c.addEqualExpression(b.getFieldName(IEntityAlias.PROCESS_DETAIL_ACTIVE), true);
			setProcessCount(b.getCount(c));
			DataModel model = super.getModel();
			List<CampaignProjectExtended> newList = new LinkedList<CampaignProjectExtended>();
			List<ITransferObject> list = (List<ITransferObject>) model.getWrappedData();
			for (ITransferObject to : list) {
				CampaignProject cd = (CampaignProject) to;
				CampaignProjectExtended cde = new CampaignProjectExtended();
				cde = mergeCampaignProject(cde, cd);
				newList.add(cde);
			}
			Collections.sort(newList, new PercenteComparator<CampaignProjectExtended>() );
			setExtendedModel(new SerializableListDataModel(newList));
		}
		return extendedModel;
	}
	public void setExtendedModel(DataModel model) {
		extendedModel = model;
	}

	private CampaignProjectExtended mergeCampaignProject(CampaignProjectExtended cpe, CampaignProject cp)
			throws ManagerBeanException {
		double d = 100.0;
		cpe.setCampaignProject(cp);
		ProcessTask processTask  = getTaskManager().getCurrentProcessTask(cp);
		ProcessDetail processDetail = null;
		if (processTask != null) {
			processDetail = processTask.getProcessDetail(); 
			if (processDetail != null) {
				int pos = 0;
				for (SelectItem si : getProcessDetailList()) {
					ProcessDetail a = (ProcessDetail) si.getValue();
					if (a.getId().equals(processDetail.getId())) {
						break;
					}
					pos++;
				}
				d = CommonUtil.round((pos * 100) / getProcessCount());
			}
		}
		cpe.setProcessDetail(processDetail);
		Task task = getTaskManager().getCurrentTask(cp);
		cpe.setTask(task);
		cpe.setProcessDetailPercent(d);
		cpe.setColor(null);
		cpe.setPercentImage(null);
		return cpe;
	}

	public Campaign getCampaign() {
		Campaign c = (Campaign) getMasterController().getTo();
		return c;
	}

	@Override
	public Collection<ITransferObject> getCollection() {
		try {
			return this.getCollection(false);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<ITransferObject> getCollection(boolean f) throws ManagerBeanException {
		return (Collection<ITransferObject>) getExtendedModel().getWrappedData();
	}

	public List<SelectItem> getProcessDetailList() throws ManagerBeanException {
		if (processDetailList == null) {
			processDetailList = new LinkedList<SelectItem>();
			IManagerBean bean = BeanManager.getManagerBean(ProcessDetail.class);
			Campaign campaign = (Campaign) getMasterController().getTo();
			Process process = campaign.getProcess();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_DETAIL_PROCESS_ID),process.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_DETAIL_ACTIVE),true);
			criteria.addOrder(bean.getFieldName(IEntityAlias.PROCESS_DETAIL_POSITION));
			List<ITransferObject> list = bean.getList(criteria);
			for (ITransferObject to : list ) {
				ProcessDetail processDetail = (ProcessDetail) to;
				SelectItem item = new SelectItem(processDetail, StringUtils.abbreviate(processDetail.getDescription(),30));
				processDetailList.add(item);
			}
		}
		return processDetailList;
	}

	public void setProcessDetailList(List<SelectItem> processDetailList) {
		this.processDetailList = processDetailList;
	}

	public Integer getProcessCount() {
		return processCount;
	}

	public void setProcessCount(Integer processCount) {
		this.processCount = processCount;
	}

	public void onChangeProcessDetail(ActionEvent event) {
		try {
			CampaignProjectExtended to = (CampaignProjectExtended) getExtendedModel().getRowData();
			CampaignProject cp = to.getCampaignProject();
			ProcessDetail pd = to.getProcessDetail();
			Task previousTask = getTaskManager().finishCampaignTask(cp,getGroupwareUtils().getCurrentTaskHolder());
			if (pd != null && pd.getId() != null) {
				getTaskManager().addProcessTask(previousTask,to.getCampaignProject().getCampaign(),pd,cp);
			}
			to = mergeCampaignProject(to, cp);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error changing process detail. ", e);
		}
	}

	private class PercenteComparator<C> implements Comparator<CampaignProjectExtended> {
		@Override
		public int compare(CampaignProjectExtended o1, CampaignProjectExtended o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}
			if (o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}
			return o1.getProcessDetailPercent().compareTo(o2.getProcessDetailPercent());
		}
	}
}