package com.code.aon.ui.fiscal.controller;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.enumeration.Model303Column;
import com.code.aon.fiscal.enumeration.VatPeriod;
import com.code.aon.fiscal.model303.Model303;
import com.code.aon.fiscal.model303.Model303CollectionProvider;
import com.code.aon.fiscal.model303.Model303Parameters;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.util.AonUtil;

public class Model303Controller implements IFinanceMessages{

	private DataModel model;
	private List<Model303> summary;
	private Model303Parameters params;

	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel(getSummary());
		}
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}

	public Model303Parameters getParams() {
		return params;
	}
	public void setParams(Model303Parameters params) {
		this.params = params;
	}

	public List<Model303> getSummary() {
		if (summary == null) {
			summary = new LinkedList<Model303>();
		}
		return summary;
	}

	public void setSummary(List<Model303> summary) {
		this.summary = summary;
	}

	public void onReset(ActionEvent event) {
		setParams(new Model303Parameters()); 
		Calendar c = Calendar.getInstance();
		getParams().setDate(c.getTime());
		c.setTime(new Date());
		getParams().setYear(c.get(Calendar.YEAR));
		getParams().setVatPeriod( VatPeriod.getQuarterlyVatPeriod( c.get(Calendar.MONTH )) );
		getParams().setFromDate(getParams().getVatPeriod().getStartDate(getParams().getYear()));	
		getParams().setToDate(getParams().getVatPeriod().getDueDate(getParams().getYear()));
		setSummary(null);
		setModel(null);
	}

	public void onYearChanged(ValueChangeEvent event) {
		getParams().setFromDate(null);
		getParams().setToDate(null);
		if (event.getNewValue() != null) {
			Integer year = (Integer) event.getNewValue(); 
			getParams().setFromDate(getParams().getVatPeriod().getStartDate(year));	
			getParams().setToDate(getParams().getVatPeriod().getDueDate(year));
		}
	}
	
	public void onVatPeriodChanged(ValueChangeEvent event) {
		getParams().setFromDate(null);
		getParams().setToDate(null);
		if (getParams().getYear() != null) {
			VatPeriod vp = (VatPeriod) event.getNewValue(); 
			getParams().setFromDate(vp.getStartDate(getParams().getYear()));	
			getParams().setToDate(vp.getDueDate(getParams().getYear()));
		} else {
			String msg = "El Periodo IVA es necesario para calcular las fecha de inicio y fin del periodo.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onSearch(ActionEvent event) {
		try {
			Model303CollectionProvider provider = new Model303CollectionProvider();
			setSummary( provider.getModel303(getParams() ));
			calculateTax();
			setModel(new ListDataModel(getSummary()));
		} catch (ManagerBeanException e) {
			String msg = e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException();
		}
	}
	
	private void calculateTax() {
		for (Model303 model303:getSummary()) {
			double at = model303.getAcumuladoTaxableBase();
			double dt = model303.getDeclaradoTaxableBase();
			double rt = CommonUtil.round(at - dt);
			model303.setResultadoTaxableBase(rt);
			model303.setDeclararTaxableBase(rt);

			double aq = model303.getAcumuladoQuota();
			double dq = model303.getDeclaradoQuota();
			double rq = CommonUtil.round(aq - dq);
			model303.setResultadoQuota(rq);
			model303.setDeclararQuota(rq);
		}
	}
	
	public void onRecalculate(ActionEvent event ) {
		for (Model303 model303:getSummary()) {
			double rq = model303.getResultadoQuota();
			double dq = model303.getDeclararQuota();
			double jq = CommonUtil.round(rq - dq);
			model303.setAjusteQuota(jq);
		}
	}
	
	public List<Model303Column> getColumns() {
		return Arrays.asList( Model303Column.values() );
	}

}
