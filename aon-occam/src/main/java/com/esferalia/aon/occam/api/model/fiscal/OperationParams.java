package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class OperationParams implements Serializable {
	
	private static final long serialVersionUID = 4708586223785270098L;

	private int domain;
	private Date fromDate;      // Desde Fecha
	private Date toDate;        // Hasta Fecha
	private Integer activity;   // Actividad (ID)
	private int bookType;       // 0-Libros de IVA, 1-Libros de IRPF, 2-Libros Unificados de IVA e IRPF
	private int tabType;        // 0-Expedidas/Ventas e Ingresos, 1-Recibidas/Compras y Gastos
	private double lastProratePercentage; // Porcentaje de prorrata del último modelo 303
	private String lastProrateType;       // Tipo de prorrata del último modelo 303
	private boolean distributeInvoice;    // Repartir factura, cuando factura de compra o gasto está imputada a todas las actividades y se debe repartir entre las actividades de la empresa (por ahora, solo cuando hay unicamente 2 actividades, una de ellas régimen exento de IVA y la otra no y se aplica la regla de prorrata) 
	private boolean draft;      // Documento Borrador 
	
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
	public int getBookType() {
		return bookType;
	}
	public OperationParams setBookType(int bookType) {
		this.bookType = bookType;
		return this;
	}
	public int getTabType() {
		return tabType;
	}
	public OperationParams setTabType(int tabType) {
		this.tabType = tabType;
		return this;
	}
	public double getLastProratePercentage() {
		return lastProratePercentage;
	}
	public OperationParams setLastProratePercentage(double lastProratePercentage) {
		this.lastProratePercentage = lastProratePercentage;
		return this;
	}
	public String getLastProrateType() {
		return lastProrateType;
	}
	public OperationParams setLastProrateType(String lastProrateType) {
		this.lastProrateType = lastProrateType;
		return this;
	}
	public boolean isDistributeInvoice() {
		return distributeInvoice;
	}
	public OperationParams setDistributeInvoice(boolean distributeInvoice) {
		this.distributeInvoice = distributeInvoice;
		return this;
	}
	public boolean isDraft() {
		return draft;
	}
	public OperationParams setDraft(boolean draft) {
		this.draft = draft;
		return this;
	}
	
}
