package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class IrpfResult implements Serializable {

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
	public void setApplicableReduction(double applicableReduction) {
		this.applicableReduction = applicableReduction;
	}
	public double getDeducibleExpense() {
		return deducibleExpense;
	}
	public void setDeducibleExpense(double deducibleExpense) {
		this.deducibleExpense = deducibleExpense;
	}
	public double getCompensatoryPension() {
		return compensatoryPension;
	}
	public void setCompensatoryPension(double compensatoryPension) {
		this.compensatoryPension = compensatoryPension;
	}
	public double getFoodAnnuality() {
		return foodAnnuality;
	}
	public void setFoodAnnuality(double foodAnnuality) {
		this.foodAnnuality = foodAnnuality;
	}
	public boolean isHomeLoanCommunnication() {
		return homeLoanCommunnication;
	}
	public void setHomeLoanCommunnication(boolean homeLoanCommunnication) {
		this.homeLoanCommunnication = homeLoanCommunnication;
	}
	public byte getLessThan3Descendent() {
		return lessThan3Descendent;
	}
	public void setLessThan3Descendent(byte lessThan3Descendent) {
		this.lessThan3Descendent = lessThan3Descendent;
	}
	public byte getLessThan3DescendentRatio() {
		return lessThan3DescendentRatio;
	}
	public void setLessThan3DescendentRatio(byte lessThan3DescendentRatio) {
		this.lessThan3DescendentRatio = lessThan3DescendentRatio;
	}
	public byte getOtherDescendent() {
		return otherDescendent;
	}
	public void setOtherDescendent(byte otherDescendent) {
		this.otherDescendent = otherDescendent;
	}
	public byte getOtherDescendentRatio() {
		return otherDescendentRatio;
	}
	public void setOtherDescendentRatio(byte otherDescendentRatio) {
		this.otherDescendentRatio = otherDescendentRatio;
	}
	public byte getDisabilityDescendent33() {
		return disabilityDescendent33;
	}
	public void setDisabilityDescendent33(byte disabilityDescendent33) {
		this.disabilityDescendent33 = disabilityDescendent33;
	}
	public byte getDisabilityDescendent33Ratio() {
		return disabilityDescendent33Ratio;
	}
	public void setDisabilityDescendent33Ratio(byte disabilityDescendent33Ratio) {
		this.disabilityDescendent33Ratio = disabilityDescendent33Ratio;
	}
	public byte getDisabilityDescendentDependence() {
		return disabilityDescendentDependence;
	}
	public void setDisabilityDescendentDependence(byte disabilityDescendentDependence) {
		this.disabilityDescendentDependence = disabilityDescendentDependence;
	}
	public byte getDisabilityDescendentDependenceRatio() {
		return disabilityDescendentDependenceRatio;
	}
	public void setDisabilityDescendentDependenceRatio(
			byte disabilityDescendentDependenceRatio) {
		this.disabilityDescendentDependenceRatio = disabilityDescendentDependenceRatio;
	}
	public byte getDisabilityDescendent65() {
		return disabilityDescendent65;
	}
	public void setDisabilityDescendent65(byte disabilityDescendent65) {
		this.disabilityDescendent65 = disabilityDescendent65;
	}
	public byte getDisabilityDescendent65Ratio() {
		return disabilityDescendent65Ratio;
	}
	public void setDisabilityDescendent65Ratio(byte disabilityDescendent65Ratio) {
		this.disabilityDescendent65Ratio = disabilityDescendent65Ratio;
	}
	public byte getLessThan75Ascendant() {
		return lessThan75Ascendant;
	}
	public void setLessThan75Ascendant(byte lessThan75Ascendant) {
		this.lessThan75Ascendant = lessThan75Ascendant;
	}
	public byte getLessThan75AscendantRatio() {
		return lessThan75AscendantRatio;
	}
	public void setLessThan75AscendantRatio(byte lessThan75AscendantRatio) {
		this.lessThan75AscendantRatio = lessThan75AscendantRatio;
	}
	public byte getAscendant() {
		return ascendant;
	}
	public void setAscendant(byte ascendant) {
		this.ascendant = ascendant;
	}
	public byte getAscendantRatio() {
		return ascendantRatio;
	}
	public void setAscendantRatio(byte ascendantRatio) {
		this.ascendantRatio = ascendantRatio;
	}
	public byte getDisabilityAscendant33() {
		return disabilityAscendant33;
	}
	public void setDisabilityAscendant33(byte disabilityAscendant33) {
		this.disabilityAscendant33 = disabilityAscendant33;
	}
	public byte getDisabilityAscendant33Ratio() {
		return disabilityAscendant33Ratio;
	}
	public void setDisabilityAscendant33Ratio(byte disabilityAscendant33Ratio) {
		this.disabilityAscendant33Ratio = disabilityAscendant33Ratio;
	}
	public byte getDisabilityAscendantDependence() {
		return disabilityAscendantDependence;
	}
	public void setDisabilityAscendantDependence(byte disabilityAscendantDependence) {
		this.disabilityAscendantDependence = disabilityAscendantDependence;
	}
	public byte getDisabilityAscendantDependenceRatio() {
		return disabilityAscendantDependenceRatio;
	}
	public void setDisabilityAscendantDependenceRatio(
			byte disabilityAscendantDependenceRatio) {
		this.disabilityAscendantDependenceRatio = disabilityAscendantDependenceRatio;
	}
	public byte getDisabilityAscendant65() {
		return disabilityAscendant65;
	}
	public void setDisabilityAscendant65(byte disabilityAscendant65) {
		this.disabilityAscendant65 = disabilityAscendant65;
	}
	public byte getDisabilityAscendant65Ratio() {
		return disabilityAscendant65Ratio;
	}
	public void setDisabilityAscendant65Ratio(byte disabilityAscendant65Ratio) {
		this.disabilityAscendant65Ratio = disabilityAscendant65Ratio;
	}
	public byte getFirstChildCalculation() {
		return firstChildCalculation;
	}
	public void setFirstChildCalculation(byte firstChildCalculation) {
		this.firstChildCalculation = firstChildCalculation;
	}
	public byte getSecondChildCalculation() {
		return secondChildCalculation;
	}
	public void setSecondChildCalculation(byte secondChildCalculation) {
		this.secondChildCalculation = secondChildCalculation;
	}
	public byte getThirdChildCalculation() {
		return thirdChildCalculation;
	}
	public void setThirdChildCalculation(byte thirdChildCalculation) {
		this.thirdChildCalculation = thirdChildCalculation;
	}
}
