package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class OperationParams implements Serializable {
	
	private static final long serialVersionUID = 2683060057390169937L;
	
	private int domain;
	
	private Boolean expenses;   // Compras y Gastos (true) / Ventas e Ingresos (false)
	private Date fromDate;      // Desde Fecha
	private Date toDate;        // Hasta Fecha
	private Integer activity;   // Actividad (ID)
	private String activityDescription;  // Actividad (Descripción)
	private Boolean irpf;       // Listado IRPF (true) / Listado IVA (false)

	public int getDomain() {
		return domain;
	}
	public OperationParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public OperationParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public String getActivityDescription() {
		return activityDescription;
	}
	public OperationParams setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
		return this;
	}
	public Boolean getExpenses() {
		return expenses;
	}
	public OperationParams setExpenses(Boolean expenses) {
		this.expenses = expenses;
		return this;
	}
	public Boolean getIrpf() {
		return irpf;
	}
	public OperationParams setIrpf(Boolean irpf) {
		this.irpf = irpf;
		return this;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public OperationParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}
	public Date getToDate() {
		return toDate;
	}
	public OperationParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}
	
}
