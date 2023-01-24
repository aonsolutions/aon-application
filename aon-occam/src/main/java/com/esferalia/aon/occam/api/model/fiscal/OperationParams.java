package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class OperationParams implements Serializable {
	
	private static final long serialVersionUID = 2683060057390169937L;
	
	private int domain;
	
	private boolean expenses;   // Compras y Gastos (true) / Ventas e Ingresos (false)
	private Date fromDate;      // Desde Fecha
	private Date toDate;        // Hasta Fecha
	private Integer activity;   // Actividad (ID)
	private String activityDescription;  // Actividad (Descripción)
	private boolean irpf;       // Listado IRPF (true) / Listado IVA (false)
	private boolean aeatBook;     // Libro Registro AEAT
	private boolean unifiedBook;  // Libro AEAT Unificado (IVA e IRPF)

	public OperationParams() {
		super();
		this.aeatBook = false;
		this.unifiedBook = false;
	}

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
	public boolean isExpenses() {
		return expenses;
	}
	public OperationParams setExpenses(boolean expenses) {
		this.expenses = expenses;
		return this;
	}
	public String getAccountPrefix() {
		return isExpenses()?"6":"7";
	}
	public boolean isIrpf() {
		return irpf;
	}
	public OperationParams setIrpf(boolean irpf) {
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
	public boolean getAeatBook() {
		return aeatBook;
	}
	public OperationParams setAeatBook(boolean aeatBook) {
		this.aeatBook = aeatBook;
		return this;
	}
	public boolean getUnifiedBook() {
		return unifiedBook;
	}
	public OperationParams setUnifiedBook(boolean unifiedBook) {
		this.unifiedBook = unifiedBook;
		return this;
	}
	
}
