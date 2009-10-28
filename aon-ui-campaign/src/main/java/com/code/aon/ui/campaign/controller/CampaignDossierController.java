package com.code.aon.ui.campaign.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.campaign.Campaign;
import com.code.aon.campaign.CampaignDossier;
import com.code.aon.campaign.Process;
import com.code.aon.campaign.ProcessDetail;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.project.Task;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.project.util.CampaignTaskManager;

public class CampaignDossierController extends LinesController {

	private static final Logger LOGGER = Logger
			.getLogger(CampaignDossierController.class.getName());

	private Criteria mainCriteria;
	private String sortColumn;
	private boolean ascending;
	private DataModel extendedModel;
	
	private ArrayList<CampaignDossierExtended> checks = new ArrayList<CampaignDossierExtended>();
	private CampaignTaskManager campaignTaskManager;
	private List<SelectItem> processDetailList;
	private Integer processCount;
	
	private CampaignTaskManager getCampaignTaskManager() {
		if (campaignTaskManager == null) {
			campaignTaskManager = new CampaignTaskManager();
		}
		return campaignTaskManager;
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
			LOGGER.log(Level.SEVERE, "Error sorting campaign dossier list", e);
		}
	}

	public boolean getRowChecked() {
		try {
			CampaignDossierExtended to = (CampaignDossierExtended) getExtendedModel().getRowData();
			return checks.contains(to);
		} catch (ManagerBeanException e) {
			return false;
		}
	}

	public void setRowChecked(boolean rowChecked) {
		try {
			CampaignDossierExtended to = (CampaignDossierExtended) getExtendedModel().getRowData();
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

	public void onRemoveCampaignDossier(ActionEvent event) {
		removeCampaignDossier();
	}

	@SuppressWarnings(value = "unchecked")
	private void removeCampaignDossier() {
		try {
			IManagerBean campaignDossierBean = BeanManager.getManagerBean(CampaignDossier.class);
			Iterator iterator = checks.iterator();
			while (iterator.hasNext()) {
				CampaignDossierExtended cde = (CampaignDossierExtended) iterator.next();
				CampaignDossier campaignDossier = cde.getCampaignDossier();
				getCampaignTaskManager().removeCampaignTask(campaignDossier);
				campaignDossierBean.remove(campaignDossier);
			}
			onSearch(null);

			checks = new ArrayList<CampaignDossierExtended>();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error removing campaign dossier in campaign with id="
					+ ((CampaignDossier) this.getTo()).getCampaign(), e);
		}
	}

	public void setExtendedModel(DataModel model) {
		extendedModel = model;
	}

	@SuppressWarnings("unchecked")
	public DataModel getExtendedModel() throws ManagerBeanException {
		if (extendedModel == null) {
			Campaign campaign = (Campaign) getMasterController().getTo();
			IManagerBean b = BeanManager.getManagerBean(ProcessDetail.class);
			Criteria c = new Criteria();
			c.addEqualExpression(b.getFieldName(ICampaignAlias.PROCESS_DETAIL_PROCESS_ID), campaign
					.getProcess().getId());
			processCount = b.getCount(c);
			DataModel model = super.getModel();
			List<CampaignDossierExtended> newList = new LinkedList<CampaignDossierExtended>();
			List<ITransferObject> list = (List<ITransferObject>) model.getWrappedData();
			for (ITransferObject to : list) {
				CampaignDossier cd = (CampaignDossier) to;
				CampaignDossierExtended cde = new CampaignDossierExtended();
				cde = mergeCampaignDossier(cde,cd);
				newList.add(cde);
			}
			setExtendedModel(new ListDataModel(newList));
		}
		return extendedModel;
	}

	private CampaignDossierExtended mergeCampaignDossier(CampaignDossierExtended cde,CampaignDossier cd) throws ManagerBeanException {
		cde.setCampaignDossier(cd);
		ProcessDetail processDetail = getCampaignTaskManager().getCurrentProcessDetail(cd);
		cde.setProcessDetail(processDetail);
		Task task = getCampaignTaskManager().getCurrentTask(cd);
		cde.setTask(task);
		double d = 100.0;
		if (processDetail != null) {
			int pos = processDetail.getPosition();
			d = CommonUtil.round((pos * 100) / processCount);
		} 
		cde.setProcessDetailPercent(d);
		cde.setColor(null);
		cde.setPercentImage(null);
		return cde;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
		try {
			return this.getCollection(false);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean f) throws ManagerBeanException {
		return (List) getExtendedModel().getWrappedData();
	}
	
	
    @SuppressWarnings("unchecked")
	public List getProcessDetailList() throws ManagerBeanException {
    	if (processDetailList == null) {
            processDetailList = new LinkedList<SelectItem>();
            IManagerBean processDetailBean = BeanManager.getManagerBean(ProcessDetail.class);
    		Campaign campaign = (Campaign) getMasterController().getTo();
    		Process process = campaign.getProcess();
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(processDetailBean.getFieldName(ICampaignAlias.PROCESS_DETAIL_PROCESS_ID), process.getId());
            Iterator iter = processDetailBean.getList(criteria).iterator();
            while(iter.hasNext()){
                ProcessDetail processDetail = (ProcessDetail)iter.next();
                SelectItem item = new SelectItem(processDetail, StringUtils.abbreviate(processDetail.getDescription(), 30));
                processDetailList.add(item);
            }
    	}
        return processDetailList;
    }

    public void onChangeProcessDetail(ActionEvent event) {
        try {
			CampaignDossierExtended to = (CampaignDossierExtended) getExtendedModel().getRowData();
			CampaignDossier cd = to.getCampaignDossier();
			ProcessDetail pd = to.getProcessDetail();
    		Task task = getCampaignTaskManager().finishCampaignTask(cd);
            if (pd != null && pd.getId() != null) {
            	getCampaignTaskManager().addCampaignTask(cd, pd.getPosition(), null, task);
            }
            mergeCampaignDossier(to,cd);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error changing process detail. ",e);
        }
    }
}