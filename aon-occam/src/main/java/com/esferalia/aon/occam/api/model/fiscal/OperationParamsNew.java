package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class OperationParamsNew implements Serializable {
	
	private static final long serialVersionUID = 4708586223785270098L;

	private int domain;
	
	private Date fromDate;      // Desde Fecha
	private Date toDate;        // Hasta Fecha
	private Integer activity;   // Actividad (ID)
	private int bookType;       // 0-Libros de IVA, 1-Libros de IRPF, 2-Libros Unificados de IVA e IRPF
	private int tabType;        // 0-Expedidas/Ventas e Ingresos, 1-Recibidas/Compras y Gastos

	public int getDomain() {
		return domain;
	}
	public OperationParamsNew setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public OperationParamsNew setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
//	public String getActivityDescription() {
//		return activityDescription;
//	}
//	public OperationParamsNew setActivityDescription(String activityDescription) {
//		this.activityDescription = activityDescription;
//		return this;
//	}
	public Date getFromDate() {
		return fromDate;
	}
	public OperationParamsNew setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}
	public Date getToDate() {
		return toDate;
	}
	public OperationParamsNew setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}
	public int getBookType() {
		return bookType;
	}
	public OperationParamsNew setBookType(int bookType) {
		this.bookType = bookType;
		return this;
	}
	public int getTabType() {
		return tabType;
	}
	public OperationParamsNew setTabType(int tabType) {
		this.tabType = tabType;
		return this;
	}
	
}
