package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod131ActivityModule implements Serializable {

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

	public Mod131ActivityModule setDescription(String description) {
		this.description = description;
		return this;
	}

	public double getValue() {
		return value;
	}

	public Mod131ActivityModule setValue(double value) {
		this.value = value;
		return this;
	}

	public String getUnit() {
		return unit;
	}

	public Mod131ActivityModule setUnit(String unit) {
		this.unit = unit;
		return this;
	}

	public double getFactor() {
		return factor;
	}

	public Mod131ActivityModule setFactor(double factor) {
		this.factor = factor;
		return this;
	}

	public double getResult() {
		return result;
	}

	public Mod131ActivityModule setResult(double result) {
		this.result = result;
		return this;
	}

	public boolean isSalariedStaff() {
		return salariedStaff;
	}

	public Mod131ActivityModule setSalariedStaff(boolean salariedStaff) {
		this.salariedStaff = salariedStaff;
		return this;
	}

	public boolean isNoSalariedStaff() {
		return noSalariedStaff;
	}

	public Mod131ActivityModule setNoSalariedStaff(boolean noSalariedStaff) {
		this.noSalariedStaff = noSalariedStaff;
		return this;
	}
	
	public double getMay19Hours() {
		return may19Hours;
	}

	public Mod131ActivityModule setMay19Hours(double may19Hours) {
		this.may19Hours = may19Hours;
		return this;
	}

	public double getMen19Hours() {
		return men19Hours;
	}

	public Mod131ActivityModule setMen19Hours(double men19Hours) {
		this.men19Hours = men19Hours;
		return this;
	}

	public double getDisHours() {
		return disHours;
	}

	public Mod131ActivityModule setDisHours(double disHours) {
		this.disHours = disHours;
		return this;
	}

	public double getYearHours() {
		return yearHours;
	}

	public Mod131ActivityModule setYearHours(double yearHours) {
		this.yearHours = yearHours;
		return this;
	}

	public double getOwnerHours() {
		return ownerHours;
	}

	public Mod131ActivityModule setOwnerHours(double ownerHours) {
		this.ownerHours = ownerHours;
		return this;
	}

	public double getSpouseHours() {
		return spouseHours;
	}

	public Mod131ActivityModule setSpouseHours(double spouseHours) {
		this.spouseHours = spouseHours;
		return this;
	}

	public boolean isSpouseDis() {
		return spouseDis;
	}

	public Mod131ActivityModule setSpouseDis(boolean spouseDis) {
		this.spouseDis = spouseDis;
		return this;
	}

	public double getChildMen18Hours() {
		return childMen18Hours;
	}

	public Mod131ActivityModule setChildMen18Hours(double childMen18Hours) {
		this.childMen18Hours = childMen18Hours;
		return this;
	}

	public double getChildDisHours() {
		return childDisHours;
	}

	public Mod131ActivityModule setChildDisHours(double childDisHours) {
		this.childDisHours = childDisHours;
		return this;
	}
	

}
