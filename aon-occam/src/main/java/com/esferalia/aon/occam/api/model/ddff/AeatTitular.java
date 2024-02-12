package com.esferalia.aon.occam.api.model.ddff;

import java.io.Serializable;
import java.util.Date;

public class AeatTitular implements Serializable {
	
	private static final long serialVersionUID = -3234857038985969063L;
	
	private String nif;	
	private String apellidosNombre;	
	private AeatDiscapacidad discapacidadIRPF;
	private AeatDiscapacidad discapacidad990;
	private Date fechaNacimiento;
	private AeatSexo sexo;
	private Date fechaFallecimiento;
	private AeatComunidadAutonoma comunidadAutonoma; 
	private String IBAN; 
	private String SWIFT;
	private Date fechaAdquisicionViviendaHabitual;
	private String numeroPrestamoHipotecario;
	private Double porcentajePrestamo;
	private boolean deduccionViviendaEjercicioAnterior;
	private boolean iglesiaCatolica;
	private boolean finesSociales;
	
	public String getNif() {
		return nif;
	}
	public AeatTitular setNif(String nif) {
		this.nif = nif;
		return this;
	}
	
	public String getApellidosNombre() {
		return apellidosNombre;
	}
	public AeatTitular setApellidosNombre(String apellidosNombre) {
		this.apellidosNombre = apellidosNombre;
		return this;
	}
	
	public AeatDiscapacidad getDiscapacidadIRPF() {
		return discapacidadIRPF;
	}
	public AeatTitular setDiscapacidadIRPF(AeatDiscapacidad discapacidadIRPF) {
		this.discapacidadIRPF = discapacidadIRPF;
		return this;
	}
	
	public AeatDiscapacidad getDiscapacidad990() {
		return discapacidad990;
	}
	public AeatTitular setDiscapacidad990(AeatDiscapacidad discapacidad990) {
		this.discapacidad990 = discapacidad990;
		return this;
	}
	
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}
	public AeatTitular setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
		return this;
	}
	
	public AeatSexo getSexo() {
		return sexo;
	}
	public AeatTitular setSexo(AeatSexo sexo) {
		this.sexo = sexo;
		return this;
	}
	
	public Date getFechaFallecimiento() {
		return fechaFallecimiento;
	}
	public AeatTitular setFechaFallecimiento(Date fechaFallecimiento) {
		this.fechaFallecimiento = fechaFallecimiento;
		return this;
	}
	
	public AeatComunidadAutonoma getComunidadAutonoma() {
		return comunidadAutonoma;
	}
	public AeatTitular setComunidadAutonoma(AeatComunidadAutonoma comunidadAutonoma) {
		this.comunidadAutonoma = comunidadAutonoma;
		return this;
	}
	
	public String getIBAN() {
		return IBAN;
	}
	public AeatTitular setIBAN(String iBAN) {
		IBAN = iBAN;
		return this;
	}
	
	public String getSWIFT() {
		return SWIFT;
	}
	public AeatTitular setSWIFT(String sWIFT) {
		SWIFT = sWIFT;
		return this;
	}
	
	public Date getFechaAdquisicionViviendaHabitual() {
		return fechaAdquisicionViviendaHabitual;
	}
	public AeatTitular setFechaAdquisicionViviendaHabitual(Date fechaAdquisicionViviendaHabitual) {
		this.fechaAdquisicionViviendaHabitual = fechaAdquisicionViviendaHabitual;
		return this;
	}
	
	public String getNumeroPrestamoHipotecario() {
		return numeroPrestamoHipotecario;
	}
	public AeatTitular setNumeroPrestamoHipotecario(String numeroPrestamoHipotecario) {
		this.numeroPrestamoHipotecario = numeroPrestamoHipotecario;
		return this;
	}
	
	public Double getPorcentajePrestamo() {
		return porcentajePrestamo;
	}
	public AeatTitular setPorcentajePrestamo(Double porcentajePrestamo) {
		this.porcentajePrestamo = porcentajePrestamo;
		return this;
	}
	
	public boolean isDeduccionViviendaEjercicioAnterior() {
		return deduccionViviendaEjercicioAnterior;
	}
	public AeatTitular setDeduccionViviendaEjercicioAnterior(boolean deduccionViviendaEjercicioAnterior) {
		this.deduccionViviendaEjercicioAnterior = deduccionViviendaEjercicioAnterior;
		return this;
	}
	
	public boolean isIglesiaCatolica() {
		return iglesiaCatolica;
	}
	public AeatTitular setIglesiaCatolica(boolean iglesiaCatolica) {
		this.iglesiaCatolica = iglesiaCatolica;
		return this;
	}

	public boolean isFinesSociales() {
		return finesSociales;
	}
	public AeatTitular setFinesSociales(boolean finesSociales) {
		this.finesSociales = finesSociales;
		return this;
	}
}