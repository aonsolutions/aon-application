package com.code.aon.ui.campaign.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.myfaces.custom.sortheader.HtmlCommandSortHeader;

import com.code.aon.campaign.CampaignDossier;
import com.code.aon.campaign.ProcessDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.project.util.CampaignTaskManager;

public class CampaignDossierController extends LinesController {

    private static final Logger LOGGER = Logger.getLogger(CampaignDossierController.class.getName());
    
    private Criteria mainCriteria;

    private String sortColumn;

    private boolean ascending;

    private ArrayList<CampaignDossier> checks = new ArrayList<CampaignDossier>();

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
        this.sortColumn = sortColumn;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(String sortColumn) {
        this.ascending = (sortColumn.equals(getSortColumn())) ? !isAscending() : true;
    }

    public boolean getRowChecked() {
        CampaignDossier to = (CampaignDossier)model.getRowData();
        return checks.contains(to);
    }

    public void setRowChecked(boolean rowChecked) {
        if (rowChecked) {
            CampaignDossier to = (CampaignDossier)model.getRowData();
            if (!checks.contains(to)) {
                checks.add(to);
            }
        } else {
            CampaignDossier to = (CampaignDossier)model.getRowData();
            if (checks.contains(to)) {
                checks.remove(to);
            }
        }
    }

    public void rowSelected(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            setRowChecked(((Boolean)event.getNewValue()).booleanValue());
        }
    }

    public String getProcessDetail() {
        try {
            if (model.getRowData() != null) {
                ProcessDetail currentProcessDetail = CampaignTaskManager.getCurrentProcessDetail((CampaignDossier)model.getRowData());
                return (currentProcessDetail==null) ? null : currentProcessDetail.getDescription();
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining current process detail of dossier with id=" + ((CampaignDossier)model.getRowData()).getId(), e);
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void onRemoveCampaignDossier(ActionEvent event) {
        removeCampaignDossier();
    }        

    private void removeCampaignDossier() {
        try {
            IManagerBean campaignDossierBean = BeanManager.getManagerBean(CampaignDossier.class);
            Iterator iterator = checks.iterator();
            while (iterator.hasNext()) {
                CampaignDossier campaignDossier = (CampaignDossier)iterator.next();
                CampaignTaskManager.removeCampaignTask(campaignDossier);
                campaignDossierBean.remove(campaignDossier);
            }
            onSearch(null);

            checks = new ArrayList<CampaignDossier>();
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error removing campaign dossier in campaign with id=" + ((CampaignDossier)this.getTo()).getCampaign(), e);
        }
    }

    public void sort(ActionEvent event) {
        try {
            UIComponent component = event.getComponent();
            if (component instanceof HtmlCommandSortHeader) {
                HtmlCommandSortHeader header = (HtmlCommandSortHeader)component;
                setAscending(header.getColumnName());
                Criteria criteria = new Criteria();
                criteria.addExpression(getMainCriteria().getExpression());
                criteria.addOrder(getFieldName(header.getColumnName()), isAscending());
                setSortColumn(header.getColumnName());
                setCriteria(criteria);
                onSearch(null);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error sorting campaign dossier list", e);
        }
    }

}