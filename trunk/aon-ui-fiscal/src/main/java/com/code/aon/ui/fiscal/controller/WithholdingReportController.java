package com.code.aon.ui.fiscal.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.withholding.EnterpriseWithholding;
import com.code.aon.fiscal.withholding.Model111;
import com.code.aon.fiscal.withholding.WithholdingManager;
import com.code.aon.fiscal.withholding.WithholdingParameters;
import com.code.aon.ui.util.AonUtil;


public class WithholdingReportController {

	private FiscalParametersController fiscalParams;
	private WithholdingParameters params;
	private Enterprise companyEnterprise;
	private String beanName;
	private DataModel model;
	private Model111 model111;
	
	public FiscalParametersController getFiscalParams() {
		if (fiscalParams == null) {
			fiscalParams = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		}
		return fiscalParams;
	}
	
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public Model111 getModel111() {
		return model111;
	}
	public void setModel111(Model111 model111) {
		this.model111 = model111;
	}

	public WithholdingParameters getParams() {
		return params;
	}

	public void setParams(WithholdingParameters params) {
		this.params = params;
	}
	
	public void onReset(ActionEvent event) {
		try {
			setModel(null);
			initializeParams();
		} catch (ManagerBeanException e) {
			String msg = "No se pudieron inicializar los parámetros de búsqueda";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	public void onEditSearch(ActionEvent event) {
		setModel(null);
	}

	private void initializeParams() throws ManagerBeanException {
		setParams( new WithholdingParameters() );
		String defYear = getFiscalParams().getDefaultYear();
		if (StringUtils.isNotBlank(defYear)) {
			getParams().setYear( Integer.parseInt(defYear) );
			getParams().setDate(new Date());
			Period period = Period.getQuarterlyPeriod( CommonUtil.getMonth(getParams().getDate()));
			getParams().setFromDate(period.getStartDate(getParams().getYear()));
			getParams().setToDate(period.getStartDate(getParams().getYear()));
		}
		Boolean b = (Boolean) AonUtil.getBeanValue("fiscal","enterpriseEnabled");
		if (b == null || !b) {
			getParams().setEnterprise(getCompanyEnterprise());
		} else {
			getParams().setEnterprise((Enterprise)BeanManager.getManagerBean(Enterprise.class).createNewTo());
		}
	}
	
	private Enterprise getCompanyEnterprise() throws ManagerBeanException {
		if (companyEnterprise == null) {
			IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
			Iterator<ITransferObject> iter = companyBean.getList(null, 0, 1).iterator();
			if (iter.hasNext()) {
				Company company = ((Company) iter.next());
				IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
				companyEnterprise = (Enterprise) enterpriseBean.get(company.getId());	
			}
		}
		return companyEnterprise;
	}
	
	public void onSearch(ActionEvent event) {
		try {
			WithholdingManager wm = new WithholdingManager();
			List<?> list = wm.getList(getParams());
			setModel(new ListDataModel(list));
		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar la consulta";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	public void onDetail(ActionEvent event) {
		
		AonUtil.addErrorMessage("La opción seleccionada aún no está disponible.");
	}
	
	public void onSelect(ActionEvent event) {
		try {
			EnterpriseWithholding ew = (EnterpriseWithholding) getModel().getRowData();
			IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
			Enterprise enterprise = (Enterprise) enterpriseBean.get(ew.getEnterpriseId());
			WithholdingParameters parameters = new WithholdingParameters();
			parameters.setEnterprise(enterprise);
			parameters.setYear(getParams().getYear());
			parameters.setPeriod(getParams().getPeriod());
			WithholdingManager wm = new WithholdingManager();
			setModel111(wm.getModel111(parameters) );
			getModel111().setEnterprise(enterprise);
			getModel111().setYear(getParams().getYear());
			getModel111().setPeriod(getParams().getPeriod());
		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar la consulta";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
}
