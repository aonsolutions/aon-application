package com.esferalia.aon.occam.mod200.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod200CompanyParticipation implements Serializable {

	private static final long serialVersionUID = 3400183583793598978L;
	
	private String document;
	private String name;
	private int province;
	private String country;
	private boolean representative;
	private String fjo;      // F/J/O
	private double percent;
	private double nominalValue;
	private double bookValue;
	private double incomes;
	
	private double valueCorrection;       // [Campo a_value ] Corrección de valor ... (Totaliza en 1504)
	private double lossReversion;         // [Campo b_value ] Reversión de pérdidas ... (Totaliza en 1505 HASTA 2022) // FALTA - CASILLA 2376 A PARTIR DE 2024
	private double correctionEffect;      // [Campo c_value ] Efecto de la corrección valorativa ... (Totaliza en 1507)
	private double accountingElimination; // [Campo cc_value] Eliminación del deterioro contable ... (Totaliza en 1506)
	private double correctionsBalance;    // [Campo d_value ] Saldo de correcciones fiscales ... (Totaliza en 1508)
	private double valuesElimination;     // [Campo dd_value] Eliminación del deterioro de valores ... (Totaliza en 1809)
	private double adjustmentDecrease;    // [Campo e_value ] Ajuste por la disminucion de valor ... (Totaliza en 1810)
	
	private double capital;
	private double reserve;
	private double otherAmounts;
	private double result;

	public String getDocument() {
		return document;
	}

	public Mod200CompanyParticipation setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public Mod200CompanyParticipation setName(String name) {
		this.name = name;
		return this;
	}

	public int getProvince() {
		return province;
	}

	public Mod200CompanyParticipation setProvince(int province) {
		this.province = province;
		return this;
	}
	public String getCountry() {
		return country;
	}

	public Mod200CompanyParticipation setCountry(String country) {
		this.country = country;
		return this;
	}

	public boolean isRepresentative() {
		return representative;
	}
	public Mod200CompanyParticipation setRepresentative(boolean representative) {
		this.representative = representative;
		return this;
	}

	public String getNotary() {
		return fjo; // F/J/Otra
	}

	public Mod200CompanyParticipation setNotary(String fjo) {
		this.fjo = fjo; // F/J/Otra
		return this;
	}
	
	public double getPercent() {
		return percent;
	}

	public Mod200CompanyParticipation setPercent(double percent) {
		this.percent = percent;
		return this;
	}

	public double getNominalValue() {
		return nominalValue;
	}

	public Mod200CompanyParticipation setNominalValue(double nominalValue) {
		this.nominalValue = nominalValue;
		return this;
	}

	public double getBookValue() {
		return bookValue;
	}

	public Mod200CompanyParticipation setBookValue(double bookValue) {
		this.bookValue = bookValue;
		return this;
	}

	public double getIncomes() {
		return incomes;
	}

	public Mod200CompanyParticipation setIncomes(double incomes) {
		this.incomes = incomes;
		return this;
	}

	public double getValueCorrection() {
		return valueCorrection;
	}

	public Mod200CompanyParticipation setValueCorrection(double valueCorrection) {
		this.valueCorrection = valueCorrection;
		return this;
	}

	public double getLossReversion() {
		return lossReversion;
	}

	public Mod200CompanyParticipation setLossReversion(double lossReversion) {
		this.lossReversion = lossReversion;
		return this;
	}

	public double getCorrectionEffect() {
		return correctionEffect;
	}

	public Mod200CompanyParticipation setCorrectionEffect(double correctionEffect) {
		this.correctionEffect = correctionEffect;
		return this;
	}

	public double getAccountingElimination() {
		return accountingElimination;
	}

	public Mod200CompanyParticipation setAccountingElimination(double accountingElimination) {
		this.accountingElimination = accountingElimination;
		return this;
	}

	public double getCorrectionsBalance() {
		return correctionsBalance;
	}

	public Mod200CompanyParticipation setCorrectionsBalance(double correctionsBalance) {
		this.correctionsBalance = correctionsBalance;
		return this;
	}

	public double getCapital() {
		return capital;
	}

	public Mod200CompanyParticipation setCapital(double capital) {
		this.capital = capital;
		return this;
	}

	public double getReserve() {
		return reserve;
	}

	public Mod200CompanyParticipation setReserve(double reserve) {
		this.reserve = reserve;
		return this;
	}

	public double getOtherAmounts() {
		return otherAmounts;
	}

	public Mod200CompanyParticipation setOtherAmounts(double otherAmounts) {
		this.otherAmounts = otherAmounts;
		return this;
	}

	public double getResult() {
		return result;
	}

	public Mod200CompanyParticipation setResult(double result) {
		this.result = result;
		return this;
	}

	public String getEntity() {
		if (AonStringUtils.isEmpty(document)) {
			return null;
		}
		return AonDocumentUtil.isEntity(document)?"J":"F";
	}
	public String getProvinceStr() {
		String p = Integer.toString(province);
		return AonStringUtils.isEmpty(getDocument())
				?null
				:AonStringUtils.leftPad(p, 2, "0");
	}
	public String getRepresenStr() {
		return isRepresentative()?"1":"0"; 
	}

	public double getValuesElimination() {
		return valuesElimination;
	}

	public Mod200CompanyParticipation setValuesElimination(double valuesElimination) {
		this.valuesElimination = valuesElimination;
		return this;
	}

	public double getAdjustmentDecrease() {
		return adjustmentDecrease;
	}

	public Mod200CompanyParticipation setAdjustmentDecrease(double adjustmentDecrease) {
		this.adjustmentDecrease = adjustmentDecrease;
		return this;
	}
	
}
