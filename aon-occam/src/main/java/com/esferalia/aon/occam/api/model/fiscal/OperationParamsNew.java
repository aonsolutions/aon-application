package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class OperationParamsNew implements Serializable {
	
	private static final long serialVersionUID = 4708586223785270098L;

	private int domain;
	
	private Date fromDate;      // Desde Fecha
	private Date toDate;        // Hasta Fecha
	private Integer activity;   // Actividad (ID)
	private String activityDescription;  // Actividad (Descripción)
	private int type;    // 0-Libro de IVA, 1-Libro de IRPF, 2-Libro Unificado de IVA e IRPF
//	private int subType; // 0-Expedidas/Ventas e Ingresos, 1-Recibidas/Compras y Gastos

//	public OperationParamsNew() {
//		super();
//	}

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
	public String getActivityDescription() {
		return activityDescription;
	}
	public OperationParamsNew setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
		return this;
	}
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
	public int getType() {
		return type;
	}
	public OperationParamsNew setType(int type) {
		this.type = type;
		return this;
	}
//	public int getSubType() {
//		return subType;
//	}
//	public OperationParamsNew setSubType(int subType) {
//		this.subType = subType;
//		return this;
//	}
	
}
