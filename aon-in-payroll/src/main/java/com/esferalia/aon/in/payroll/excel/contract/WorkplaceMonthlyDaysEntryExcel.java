package com.esferalia.aon.in.payroll.excel.contract;

import java.io.Serializable;

public class WorkplaceMonthlyDaysEntryExcel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int mes;
	private int year;
    private int diasMes;
    private int diasLaborables;
    private int diasFestivos;
    private int diasFinDeSemana;
    private int horasJornada;
	
	public WorkplaceMonthlyDaysEntryExcel() {
		super();
	}

	public int getMes() {
		return mes;
	}

	public WorkplaceMonthlyDaysEntryExcel setMes(int mes) {
		this.mes = mes;
		return this;
	}
	
	public int getYear() {
		return year;
	}

	public WorkplaceMonthlyDaysEntryExcel setYear(int year) {
		this.year = year;
		return this;
	}

	public int getDiasMes() {
		return diasMes;
	}

	public WorkplaceMonthlyDaysEntryExcel setDiasMes(int diasMes) {
		this.diasMes = diasMes;
		return this;
	}

	public int getDiasLaborables() {
		return diasLaborables;
	}

	public WorkplaceMonthlyDaysEntryExcel setDiasLaborables(int diasLaborables) {
		this.diasLaborables = diasLaborables;
		return this;
	}

	public int getDiasFestivos() {
		return diasFestivos;
	}

	public WorkplaceMonthlyDaysEntryExcel setDiasFestivos(int diasFestivos) {
		this.diasFestivos = diasFestivos;
		return this;
	}

	public int getDiasFinDeSemana() {
		return diasFinDeSemana;
	}

	public WorkplaceMonthlyDaysEntryExcel setDiasFinDeSemana(int diasFinDeSemana) {
		this.diasFinDeSemana = diasFinDeSemana;
		return this;
	}

	public int getHorasJornada() {
		return horasJornada;
	}

	public WorkplaceMonthlyDaysEntryExcel setHorasJornada(int horasJornada) {
		this.horasJornada = horasJornada;
		return this;
	}
	
}
