package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class IrpfResult implements Serializable {

	private static final long serialVersionUID = 5204896474187895711L;
	
	private double applicableReduction;
	private double deducibleExpense;
	private double compensatoryPension;
	private double foodAnnuality;

	private boolean homeLoanCommunnication;

	private byte lessThan3Descendent;
	private byte lessThan3DescendentRatio;
	private byte otherDescendent;
	private byte otherDescendentRatio;

	private byte disabilityDescendent33;
	private byte disabilityDescendent33Ratio;
	private byte disabilityDescendentDependence;
	private byte disabilityDescendentDependenceRatio;
	private byte disabilityDescendent65;
	private byte disabilityDescendent65Ratio;

	private byte lessThan75Ascendant;
	private byte lessThan75AscendantRatio;
	private byte ascendant;
	private byte ascendantRatio;

	private byte disabilityAscendant33;
	private byte disabilityAscendant33Ratio;
	private byte disabilityAscendantDependence;
	private byte disabilityAscendantDependenceRatio;
	private byte disabilityAscendant65;
	private byte disabilityAscendant65Ratio;

	private byte firstChildCalculation;
	private byte secondChildCalculation;
	private byte thirdChildCalculation;

	public double getApplicableReduction() {
		return applicableReduction;
	}
	public IrpfResult setApplicableReduction(double applicableReduction) {
		this.applicableReduction = applicableReduction;
		return this;
	}
	public double getDeducibleExpense() {
		return deducibleExpense;
	}
	public IrpfResult setDeducibleExpense(double deducibleExpense) {
		this.deducibleExpense = deducibleExpense;
		return this;
	}
	public double getCompensatoryPension() {
		return compensatoryPension;
	}
	public IrpfResult setCompensatoryPension(double compensatoryPension) {
		this.compensatoryPension = compensatoryPension;
		return this;
	}
	public double getFoodAnnuality() {
		return foodAnnuality;
	}
	public IrpfResult setFoodAnnuality(double foodAnnuality) {
		this.foodAnnuality = foodAnnuality;
		return this;
	}
	public boolean isHomeLoanCommunnication() {
		return homeLoanCommunnication;
	}
	public IrpfResult setHomeLoanCommunnication(boolean homeLoanCommunnication) {
		this.homeLoanCommunnication = homeLoanCommunnication;
		return this;
	}
	public byte getLessThan3Descendent() {
		return lessThan3Descendent;
	}
	public IrpfResult setLessThan3Descendent(byte lessThan3Descendent) {
		this.lessThan3Descendent = lessThan3Descendent;
		return this;
	}
	public byte getLessThan3DescendentRatio() {
		return lessThan3DescendentRatio;
	}
	public IrpfResult setLessThan3DescendentRatio(byte lessThan3DescendentRatio) {
		this.lessThan3DescendentRatio = lessThan3DescendentRatio;
		return this;
	}
	public byte getOtherDescendent() {
		return otherDescendent;
	}
	public IrpfResult setOtherDescendent(byte otherDescendent) {
		this.otherDescendent = otherDescendent;
		return this;
	}
	public byte getOtherDescendentRatio() {
		return otherDescendentRatio;
	}
	public IrpfResult setOtherDescendentRatio(byte otherDescendentRatio) {
		this.otherDescendentRatio = otherDescendentRatio;
		return this;
	}
	public byte getDisabilityDescendent33() {
		return disabilityDescendent33;
	}
	public IrpfResult setDisabilityDescendent33(byte disabilityDescendent33) {
		this.disabilityDescendent33 = disabilityDescendent33;
		return this;
	}
	public byte getDisabilityDescendent33Ratio() {
		return disabilityDescendent33Ratio;
	}
	public IrpfResult setDisabilityDescendent33Ratio(byte disabilityDescendent33Ratio) {
		this.disabilityDescendent33Ratio = disabilityDescendent33Ratio;
		return this;
	}
	public byte getDisabilityDescendentDependence() {
		return disabilityDescendentDependence;
	}
	public IrpfResult setDisabilityDescendentDependence(byte disabilityDescendentDependence) {
		this.disabilityDescendentDependence = disabilityDescendentDependence;
		return this;
	}
	public byte getDisabilityDescendentDependenceRatio() {
		return disabilityDescendentDependenceRatio;
	}
	public IrpfResult setDisabilityDescendentDependenceRatio(
			byte disabilityDescendentDependenceRatio) {
		this.disabilityDescendentDependenceRatio = disabilityDescendentDependenceRatio;
		return this;
	}
	public byte getDisabilityDescendent65() {
		return disabilityDescendent65;
	}
	public IrpfResult setDisabilityDescendent65(byte disabilityDescendent65) {
		this.disabilityDescendent65 = disabilityDescendent65;
		return this;
	}
	public byte getDisabilityDescendent65Ratio() {
		return disabilityDescendent65Ratio;
	}
	public IrpfResult setDisabilityDescendent65Ratio(byte disabilityDescendent65Ratio) {
		this.disabilityDescendent65Ratio = disabilityDescendent65Ratio;
		return this;
	}
	public byte getLessThan75Ascendant() {
		return lessThan75Ascendant;
	}
	public IrpfResult setLessThan75Ascendant(byte lessThan75Ascendant) {
		this.lessThan75Ascendant = lessThan75Ascendant;
		return this;
	}
	public byte getLessThan75AscendantRatio() {
		return lessThan75AscendantRatio;
	}
	public IrpfResult setLessThan75AscendantRatio(byte lessThan75AscendantRatio) {
		this.lessThan75AscendantRatio = lessThan75AscendantRatio;
		return this;
	}
	public byte getAscendant() {
		return ascendant;
	}
	public IrpfResult setAscendant(byte ascendant) {
		this.ascendant = ascendant;
		return this;
	}
	public byte getAscendantRatio() {
		return ascendantRatio;
	}
	public IrpfResult setAscendantRatio(byte ascendantRatio) {
		this.ascendantRatio = ascendantRatio;
		return this;
	}
	public byte getDisabilityAscendant33() {
		return disabilityAscendant33;
	}
	public IrpfResult setDisabilityAscendant33(byte disabilityAscendant33) {
		this.disabilityAscendant33 = disabilityAscendant33;
		return this;
	}
	public byte getDisabilityAscendant33Ratio() {
		return disabilityAscendant33Ratio;
	}
	public IrpfResult setDisabilityAscendant33Ratio(byte disabilityAscendant33Ratio) {
		this.disabilityAscendant33Ratio = disabilityAscendant33Ratio;
		return this;
	}
	public byte getDisabilityAscendantDependence() {
		return disabilityAscendantDependence;
	}
	public IrpfResult setDisabilityAscendantDependence(byte disabilityAscendantDependence) {
		this.disabilityAscendantDependence = disabilityAscendantDependence;
		return this;
	}
	public byte getDisabilityAscendantDependenceRatio() {
		return disabilityAscendantDependenceRatio;
	}
	public IrpfResult setDisabilityAscendantDependenceRatio(
			byte disabilityAscendantDependenceRatio) {
		this.disabilityAscendantDependenceRatio = disabilityAscendantDependenceRatio;
		return this;
	}
	public byte getDisabilityAscendant65() {
		return disabilityAscendant65;
	}
	public IrpfResult setDisabilityAscendant65(byte disabilityAscendant65) {
		this.disabilityAscendant65 = disabilityAscendant65;
		return this;
	}
	public byte getDisabilityAscendant65Ratio() {
		return disabilityAscendant65Ratio;
	}
	public IrpfResult setDisabilityAscendant65Ratio(byte disabilityAscendant65Ratio) {
		this.disabilityAscendant65Ratio = disabilityAscendant65Ratio;
		return this;
	}
	public byte getFirstChildCalculation() {
		return firstChildCalculation;
	}
	public IrpfResult setFirstChildCalculation(byte firstChildCalculation) {
		this.firstChildCalculation = firstChildCalculation;
		return this;
	}
	public byte getSecondChildCalculation() {
		return secondChildCalculation;
	}
	public IrpfResult setSecondChildCalculation(byte secondChildCalculation) {
		this.secondChildCalculation = secondChildCalculation;
		return this;
	}
	public byte getThirdChildCalculation() {
		return thirdChildCalculation;
	}
	public IrpfResult setThirdChildCalculation(byte thirdChildCalculation) {
		this.thirdChildCalculation = thirdChildCalculation;
		return this;
	}
}
