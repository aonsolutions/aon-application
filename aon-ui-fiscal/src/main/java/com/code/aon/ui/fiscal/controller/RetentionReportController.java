package com.code.aon.ui.fiscal.controller;


import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import com.code.aon.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.fiscal.enumeration.InvoiceReportOrder;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.retention.Retention;
import com.code.aon.fiscal.retention.RetentionCollection;
import com.code.aon.fiscal.retention.RetentionCollectionParameters;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;

public class RetentionReportController implements ICollectionProvider, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private RetentionCollectionParameters params;
	private Integer year;
	private Period period;
	private InvoiceReportOrder order;

	private List<Retention> summary;
	private DataModel model;
	private DataScrollerState detailState;
	private DataModel groupedModel;
	private String title;

	public List<Retention> getSummary() {
		return summary;
	}

	public void setSummary(List<Retention> summary) {
		this.summary = summary;
	}

	public DataModel getModel() {
		if (model == null) {
			model = new SerializableListDataModel( decorate() );
		}
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public DataScrollerState getDetailState() {
		return detailState;
	}

	public void setDetailState(DataScrollerState detailState) {
		this.detailState = detailState;
	}

	public DataModel getGroupedModel() {
		return groupedModel;
	}

	public void setGroupedModel(DataModel groupedModel) {
		this.groupedModel = groupedModel;
	}

	public RetentionCollectionParameters getParams() {
		return params;
	}
	public void setParams(RetentionCollectionParameters params) {
		this.params = params;
	}

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}

	public InvoiceReportOrder getOrder() {
		return order;
	}
	public void setOrder(InvoiceReportOrder order) {
		this.order = order;
	}

	public void onReset(ActionEvent event) {
		setParams(new RetentionCollectionParameters(AonUtil.getDomainName()));
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		getParams().setDate(c.getTime());
		setYear(c.get(Calendar.YEAR));
		setPeriod( Period.getQuarterlyPeriod( c.get(Calendar.MONTH )) );
		getParams().setFromInvoiceDate(getPeriod().getStartDate(getYear()));	
		getParams().setToInvoiceDate(getPeriod().getDueDate(getYear()));
		getParams().setFromSeries(null);
		getParams().setFromNumber(null);
		getParams().setToSeries(null);
		getParams().setToNumber(null);
		getParams().setSecurityLevel(null);
		getParams().setToCustomer(false);
		getParams().setByPercent(false);
		getParams().setTaxDateEnabled(true);
		onResetModel(event);
	}
	
	public void onResetModel(ActionEvent event) {
		setSummary(null);
		setModel(null);
	}

	
	public List<Retention> search() {
		try {
			RetentionCollection rc = new RetentionCollection();
			return rc.getRetentionList(params);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onSearch(ActionEvent event) {
		setSummary( search());
	}

	public void setTitle(RetentionCollectionParameters params) {
		Locale locale = AonUtil.getCurrentLocale();
		StringBuilder buf = new StringBuilder();
		if (params.getWithholdingType() != null) {
			buf.append(  params.getWithholdingType().getName(locale) );
		} 
		if (params.getPercent()!= null) {
			Double percent = params.getPercent(); 
			buf.append(" (");
			buf.append(percent);
			buf.append("%");
			buf.append(")");
		}
		this.title = buf.toString();
	}

	public String getTitle() {
		return this.title;	
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
		FacesContext ctx = FacesContext.getCurrentInstance();
		String reportKey = ctx.getExternalContext().getRequestParameterMap().get("reportKey");
		if (reportKey != null && reportKey.equals("retentionBookGrouped")) {
			return (Collection) getGroupedModel().getWrappedData();	
		}
		return (Collection) getDetailState().getModel().getWrappedData();
	}

	@Override
	public Collection<?> getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onYearChanged(ValueChangeEvent event) {
		getParams().setFromInvoiceDate(null);
		getParams().setToInvoiceDate(null);
		if (event.getNewValue() != null) {
			Integer year = (Integer) event.getNewValue(); 
			getParams().setFromInvoiceDate(getPeriod().getStartDate(year));	
			getParams().setToInvoiceDate(getPeriod().getDueDate(year));
		}
	}
	
	public void onPeriodChanged(ValueChangeEvent event) {
		getParams().setFromInvoiceDate(null);
		getParams().setToInvoiceDate(null);
		if (getYear() != null) {
			Period vp = (Period) event.getNewValue(); 
			getParams().setFromInvoiceDate(vp.getStartDate(getYear()));	
			getParams().setToInvoiceDate(vp.getDueDate(getYear()));
		} else {
			String msg = "El Periodo IVA es necesario para calcular las fecha de inicio y fin del periodo.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	private List<Retention> decorate() {
		List<Retention> decorated = new LinkedList<Retention>();
		WithholdingType pre = null;
		Retention granTotal = new Retention();
		granTotal.setTotal(true);
		granTotal.setWithholdingHidden(true);
		granTotal.setGrandTotal(true);
		Retention total = new Retention();
		total.setWithholdingHidden(false);
		total.setTotal(true);
		total.setGrandTotal(false);
		for (Retention ret: getSummary()) {
			if (getParams().isByPercent()) {
				if (ret.getWithholdingType() == pre ) {
					ret.setWithholdingHidden(true);	
				} else {
					if (pre != null) {
						total.setWithholdingType(pre);
						decorated.add(total);
						total = new Retention();
						total.setWithholdingHidden(false);
						total.setTotal(true);
						total.setGrandTotal(false);
					}
				}
				total.setCount(total.getCount() + ret.getCount());
				total.setBase(CommonUtil.round(total.getBase() + ret.getBase()) );
				total.setQuota(CommonUtil.round(total.getQuota() + ret.getQuota()) );
				pre = ret.getWithholdingType();
			}
			granTotal.setBase(CommonUtil.round(granTotal.getBase() + ret.getBase()) );
			granTotal.setQuota(CommonUtil.round(granTotal.getQuota() + ret.getQuota()) );
			decorated.add(ret);
		}
		if (pre != null) {
			total.setWithholdingType(pre);
			decorated.add(total);
		}
		decorated.add(granTotal);
		return decorated;
	}
	
	public void onDetail(ActionEvent event) {
		try {
			Retention ret = (Retention) getModel().getRowData();
			getParams().setWithholdingType(ret.getWithholdingType());
			getParams().setPercent(ret.getPercent());
			setTitle(getParams());
			RetentionCollection vc = new RetentionCollection();
			List<Retention> list = vc.getRetentionDetailList(getParams(),getOrder());
			setDetailState(new DataScrollerState(new SerializableListDataModel(list), "retentionReportDetail"));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public void onGroupedDetail(ActionEvent event) {
		try {
			RetentionCollection vc = new RetentionCollection();
			List<Retention> list = vc.getGroupedRetentionDetailList(getParams(),getOrder());
			setGroupedModel(new SerializableListDataModel(list));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public void onSwitchPercent(ActionEvent event) {
		params.setByPercent(!params.isByPercent());
		setModel(null);
		onSearch(event);
	}
}
