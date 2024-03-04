package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303ActivityFarmer implements Serializable {

	private static final long serialVersionUID = -7300170585931202609L;
	private int index;
	
	private String code;		// Código
	private String description;	// Descripción
	
	private double vol;			// Volumen ingresos 
	private double ind;			// Índice de cuota
	private double cuo;			// Cuota Devengada
	private double por;			// Porcentaje de ingreso a a cuenta
	private double ing;			// Ingreso a cuenta
	private double tso;			// Cuota soportada (4T)
	private double com;			// Compensaciones satisfechas a sujetos pasivos en R.E.A.G.P.
	private double dev;			// 1% de la cuota devengada por operaciones corrientes
	private double sop;			// Cuota soportada operaciones corrientes
	private double cad;			// Cuota anual derivada	del Régimen simplificado
	
	public int getIndex() {
		return index;
	}
	public Mod303ActivityFarmer setIndex(int index) {
		this.index = index;
		return this;
	}
	public String getFullDescription() {
		return AonStringUtils.trimToEmpty(code)
			+ (AonStringUtils.isBlank(description)?AonStringUtils.EMPTY:AonStringUtils.HYPHEN) 
			+ AonStringUtils.trimToEmpty(description);
	}
		
	public String getCode() {
		return code;
	}
	public Mod303ActivityFarmer setCode(String code) {
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public Mod303ActivityFarmer setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public double getVol() {
		return vol;
	}
	public Mod303ActivityFarmer setVol(double vol) {
		this.vol = vol;
		return this;
	}

	public double getInd() {
		return ind;
	}
	public Mod303ActivityFarmer setInd(double ind) {
		this.ind = ind;
		return this;
	}

	public double getCuo() {
		return cuo;
	}
	public Mod303ActivityFarmer setCuo(double cuo) {
		this.cuo = cuo;
		return this;
	}
	
	public double getPor() {
		return por;
	}
	public Mod303ActivityFarmer setPor(double por) {
		this.por = por;
		return this;
	}
	
	public double getIng() {
		return ing;
	}
	public Mod303ActivityFarmer setIng(double ing) {
		this.ing = ing;
		return this;
	}
	
	public double getSop() {
		return sop;
	}
	public Mod303ActivityFarmer setSop(double sop) {
		this.sop = sop;
		return this;
	}
	
	public double getCom() {
		return com;
	}
	public Mod303ActivityFarmer setCom(double com) {
		this.com = com;
		return this;
	}
	public double getDev() {
		return dev;
	}
	public Mod303ActivityFarmer setDev(double dev) {
		this.dev = dev;
		return this;
	}
	public double getTso() {
		return tso;
	}
	public Mod303ActivityFarmer setTso(double tso) {
		this.tso = tso;
		return this;
	}
	
	public double getCad() {
		return cad;
	}
	public Mod303ActivityFarmer setCad(double cad) {
		this.cad = cad;
		return this;
	}

	public boolean isEmpty() {
		return AonStringUtils.isBlank( getCode() );
	}
	public boolean isNotEmpty() {
		return AonStringUtils.isNotBlank( getCode() );
	}
	
	public void initialize() {
		this.setCode(null)
		.setDescription(null)
		.setVol(0.0)
		.setInd(0.0)
		.setCuo(0.0)
		.setPor(0.0)
		.setIng(0.0)
		.setSop(0.0)
		.setCom(0.0)
		.setDev(0.0)
		.setTso(0.0)
		.setCad(0.0);
		
	}
	
	public static Mod303ActivityFarmer clone (Mod303ActivityFarmer toClone) {
		return new Mod303ActivityFarmer()
			.setCode(toClone.getCode())
			.setDescription(toClone.getDescription())
			.setVol(toClone.getVol())
			.setInd(toClone.getInd())
			.setCuo(toClone.getCuo())
			.setPor(toClone.getPor())
			.setIng(toClone.getIng())
			.setSop(toClone.getSop())
			.setCom(toClone.getCom())
			.setDev(toClone.getDev())
			.setTso(toClone.getTso())
			.setCad(toClone.getCad());
	}
	
}
