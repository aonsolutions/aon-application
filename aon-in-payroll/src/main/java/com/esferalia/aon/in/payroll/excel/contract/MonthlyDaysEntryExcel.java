package com.esferalia.aon.in.payroll.excel.contract;

import java.io.Serializable;

public class MonthlyDaysEntryExcel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String mes;
	private int year;
    private int diasMes;
    private int diasLaborables;
    private int diasFestivos;
    private int diasFinDeSemana;
    private int diasTrabajados;
    private int diasVacaciones;
    private int diasIT;
    private int diasNoRecuperables;
    private int diasAusencia;
	
	public MonthlyDaysEntryExcel() {
		super();
	}

	public String getMes() {
		return mes;
	}

	public MonthlyDaysEntryExcel setMes(String mes) {
		this.mes = mes;
		return this;
	}

	public int getDiasMes() {
		return diasMes;
	}
	
	public MonthlyDaysEntryExcel setYear(int year) {
		this.year = year;
		return this;
	}

	public int getYear() {
		return year;
	}

	public MonthlyDaysEntryExcel setDiasMes(int diasMes) {
		this.diasMes = diasMes;
		return this;
	}

	public int getDiasLaborables() {
		return diasLaborables;
	}

	public MonthlyDaysEntryExcel setDiasLaborables(int diasLaborables) {
		this.diasLaborables = diasLaborables;
		return this;
	}

	public int getDiasFestivos() {
		return diasFestivos;
	}

	public MonthlyDaysEntryExcel setDiasFestivos(int diasFestivos) {
		this.diasFestivos = diasFestivos;
		return this;
	}

	public int getDiasFinDeSemana() {
		return diasFinDeSemana;
	}

	public MonthlyDaysEntryExcel setDiasFinDeSemana(int diasFinDeSemana) {
		this.diasFinDeSemana = diasFinDeSemana;
		return this;
	}

	public int getDiasTrabajados() {
		return diasTrabajados;
	}

	public MonthlyDaysEntryExcel setDiasTrabajados(int diasTrabajados) {
		this.diasTrabajados = diasTrabajados;
		return this;
	}

	public int getDiasVacaciones() {
		return diasVacaciones;
	}

	public MonthlyDaysEntryExcel setDiasVacaciones(int diasVacaciones) {
		this.diasVacaciones = diasVacaciones;
		return this;
	}

	public int getDiasIT() {
		return diasIT;
	}

	public MonthlyDaysEntryExcel setDiasIT(int diasIT) {
		this.diasIT = diasIT;
		return this;
	}
	
	public int getDiasNoRecuperables() {
		return diasNoRecuperables;
	}

	public MonthlyDaysEntryExcel setDiasNoRecuperables(int diasNoRecuperables) {
		this.diasNoRecuperables = diasNoRecuperables;
		return this;
	}
	
	public int getDiasAusencia() {
		return diasAusencia;
	}

	public MonthlyDaysEntryExcel setDiasAusencia(int diasAusencia) {
		this.diasAusencia = diasAusencia;
		return this;
	}
	
	public int getDiasTotal() {
		return diasLaborables - diasAusencia;
	}

	@Override
    public String toString() {
        return String.format(
            "  %s: [Total: %d, Laborables: %d, Festivos: %d, Finde: %d, Trabajados: %d, Vacaciones: %d, IT: %d]",
            mes, diasMes, diasLaborables, diasFestivos, diasFinDeSemana, diasTrabajados, diasVacaciones, diasIT
        );
    }
	
}
