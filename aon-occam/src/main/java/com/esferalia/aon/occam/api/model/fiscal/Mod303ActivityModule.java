package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod303ActivityModule implements Serializable {

	private static final long serialVersionUID = 6080727858224392417L;
	
	private String description;
	private double value;
	private String unit;
	private double factor;
	private double result;
	
	private boolean salariedStaff;
	private boolean noSalariedStaff;
	
	private double may19Hours; 		//Mayores de 19 años
	private double men19Hours; 		//Menores de 19 años y trabajadores con contratos de aprendizaje o formación, que no sean discapacitados.
	private double disHours; 		//Discapacitados con grado de minusvalía igual o superior al 33 por 100
	private double yearHours; 		//Horas anuales
	
	private double ownerHours; 		//Horas anuales del titular. (máximo 1.800 horas)
	private double spouseHours; 	//Horas anuales del cónyuge. (máximo 1.800 horas)
	private boolean spouseDis; 		//Indique si el c\u00F3nyuge es discapacitado en grado igual o superior al 33%
	private double childMen18Hours; //Horas anuales de los hijos menores de 18 años.
	private double childDisHours; 	//Horas anuales de los hijos menores de 18 años con discapacidad en grado igual o superior al 33%

	
	public String getDescription() {
		return description;
	}

	public Mod303ActivityModule setDescription(String description) {
		this.description = description;
		return this;
	}

	public double getValue() {
		return value;
	}

	public Mod303ActivityModule setValue(double value) {
		this.value = value;
		return this;
	}

	public String getUnit() {
		return unit;
	}

	public Mod303ActivityModule setUnit(String unit) {
		this.unit = unit;
		return this;
	}

	public double getFactor() {
		return factor;
	}

	public Mod303ActivityModule setFactor(double factor) {
		this.factor = factor;
		return this;
	}

	public double getResult() {
		return result;
	}

	public Mod303ActivityModule setResult(double result) {
		this.result = result;
		return this;
	}

	public boolean isSalariedStaff() {
		return salariedStaff;
	}

	public Mod303ActivityModule setSalariedStaff(boolean salariedStaff) {
		this.salariedStaff = salariedStaff;
		return this;
	}

	public boolean isNoSalariedStaff() {
		return noSalariedStaff;
	}

	public Mod303ActivityModule setNoSalariedStaff(boolean noSalariedStaff) {
		this.noSalariedStaff = noSalariedStaff;
		return this;
	}
	
	public double getMay19Hours() {
		return may19Hours;
	}

	public Mod303ActivityModule setMay19Hours(double may19Hours) {
		this.may19Hours = may19Hours;
		return this;
	}

	public double getMen19Hours() {
		return men19Hours;
	}

	public Mod303ActivityModule setMen19Hours(double men19Hours) {
		this.men19Hours = men19Hours;
		return this;
	}

	public double getDisHours() {
		return disHours;
	}

	public Mod303ActivityModule setDisHours(double disHours) {
		this.disHours = disHours;
		return this;
	}

	public double getYearHours() {
		return yearHours;
	}

	public Mod303ActivityModule setYearHours(double yearHours) {
		this.yearHours = yearHours;
		return this;
	}

	public double getOwnerHours() {
		return ownerHours;
	}

	public Mod303ActivityModule setOwnerHours(double ownerHours) {
		this.ownerHours = ownerHours;
		return this;
	}

	public double getSpouseHours() {
		return spouseHours;
	}

	public Mod303ActivityModule setSpouseHours(double spouseHours) {
		this.spouseHours = spouseHours;
		return this;
	}

	public boolean isSpouseDis() {
		return spouseDis;
	}

	public Mod303ActivityModule setSpouseDis(boolean spouseDis) {
		this.spouseDis = spouseDis;
		return this;
	}

	public double getChildMen18Hours() {
		return childMen18Hours;
	}

	public Mod303ActivityModule setChildMen18Hours(double childMen18Hours) {
		this.childMen18Hours = childMen18Hours;
		return this;
	}

	public double getChildDisHours() {
		return childDisHours;
	}

	public Mod303ActivityModule setChildDisHours(double childDisHours) {
		this.childDisHours = childDisHours;
		return this;
	}

	public static Mod303ActivityModule clone(Mod303ActivityModule toClone) {
		return new Mod303ActivityModule()
			.setDescription(toClone.getDescription())
			.setValue(toClone.getValue())
			.setUnit(toClone.getUnit()) 
			.setFactor(toClone.getFactor())
			.setResult(toClone.getResult())
			.setSalariedStaff(toClone.isSalariedStaff())
			.setNoSalariedStaff(toClone.isNoSalariedStaff())
			.setMay19Hours(toClone.getMay19Hours())
			.setMen19Hours(toClone.getMen19Hours())
			.setDisHours(toClone.getDisHours())
			.setYearHours(toClone.getYearHours())
			.setOwnerHours(toClone.getOwnerHours())
			.setSpouseHours(toClone.getSpouseHours())
			.setSpouseDis(toClone.isSpouseDis())
			.setChildMen18Hours(toClone.getChildMen18Hours())
			.setChildDisHours(toClone.getChildDisHours())
				;
	}
	

}
