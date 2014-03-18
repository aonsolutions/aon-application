package com.code.aon.ui.finance.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;


public class FinancePaymentPrintController implements ICollectionProvider, IFinanceConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(FinancePaymentPrintController.class);
	
	private final String REPORT_TEMPLATE_1 = "fPaymentList";
	private final String REPORT_TEMPLATE_2 = "fPaymentListTemplate2";

	private ArrayList<Finance> checks = new ArrayList<Finance>();
	
	private String selectedTemplate;
	
	public String getSelectedTemplate() {
		if(StringUtils.isBlank(selectedTemplate)){
			selectedTemplate = REPORT_TEMPLATE_1;
		}
		return selectedTemplate;
	}

	public void setSelectedTemplate(String selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
	}
	
	public List<SelectItem> getAvailableTemplates(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		list.add(new SelectItem(REPORT_TEMPLATE_1, "Plantilla 1"));
		list.add(new SelectItem(REPORT_TEMPLATE_2, "Plantilla 2"));
		return list;
	}

	private DataModel getModel() {
		FinanceController financeController = (FinanceController) AonUtil.getRegisteredBean(FINANCE_CONTROLLER_NAME);
		try {
			return financeController.getModel();
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining finance list", e);
		}
		return null;
	}
	
	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getRowChecked() {
		Finance to = (Finance) getModel().getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Finance to = (Finance) getModel().getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Finance to = (Finance) getModel().getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<Finance> getCheckedFinances() {
		return checks;
	}

	public void clearCheckedFinances() {
		checks = new ArrayList<Finance>();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		FinanceController financeController = (FinanceController) AonUtil.getRegisteredBean(FINANCE_CONTROLLER_NAME);
		List<ITransferObject> list = financeController.getManagerBean().getList(financeController.getCriteria());
		for (ITransferObject ito : list) {
			Finance detail = (Finance)ito;
			if (!checks.contains(detail)) {
				checks.add( detail );
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedFinances();
	}

	public int getCheckedCount() {
		return getCheckedFinances().size();
	}
	
	public void onExecuteReport(){
		ReportManager report = (ReportManager) AonUtil.getRegisteredBean("report");
		report.setReportKey(getSelectedTemplate());
		report.onExecute();
	}
	
	public void onEditSearchPayment(ActionEvent event){
		FinanceController financeController = (FinanceController) AonUtil.getRegisteredBean(FINANCE_CONTROLLER_NAME);
		financeController.onEditSearchPayment(event);
		clearCheckedFinances();
		setSelectedTemplate(null);
	}
	
	@Override
	public Collection<?> getCollection() {
		return getCheckedFinances();
	}

	@Override
	public Collection<?> getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
}