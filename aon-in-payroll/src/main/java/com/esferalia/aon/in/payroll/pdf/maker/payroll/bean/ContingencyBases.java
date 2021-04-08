package com.esferalia.aon.in.payroll.pdf.maker.payroll.bean;

import java.util.Optional;

public class ContingencyBases {

	private Optional<Double> monthlyAmount;
	private Optional<Double> extraProrationAmount;

	private Optional<Double> commonContBase;
	private Optional<Double> commonContType;
	private Optional<Double> commonContApEnterprise;

	private Optional<Double> professionalContBase;

	private Optional<Double> atEpType;
	private Optional<Double> atEpApEnterprise;

	private Optional<Double> unemploymentType;
	private Optional<Double> unemploymentApEnterprise;

	private Optional<Double> profesFormType;
	private Optional<Double> profesFormApEnterprise;

	private Optional<Double> fogasaType;
	private Optional<Double> fogasaApEnterprise;

	private Optional<Double> forceMajeureBase;
	private Optional<Double> forceMajeureType;
	private Optional<Double> forceMajeureApEnterprise;

	private Optional<Double> noStructBase;
	private Optional<Double> noStructType;
	private Optional<Double> noStructApEnterprise;

	private Optional<Double> irpfEsp;
	private Optional<Double> irpfRetribDiner;
	private Optional<Double> total;

	private ContingencyBases() {
	}

	public Optional<Double> getMonthlyAmount() {
		return monthlyAmount;
	}

	public Optional<Double> getExtraProrationAmount() {
		return extraProrationAmount;
	}

	public Optional<Double> getCommonContBase() {
		return commonContBase;
	}

	public Optional<Double> getCommonContType() {
		return commonContType;
	}

	public Optional<Double> getCommonContApEnterprise() {
		return commonContApEnterprise;
	}

	public Optional<Double> getProfessionalContBase() {
		return professionalContBase;
	}

	public Optional<Double> getAtEpType() {
		return atEpType;
	}

	public Optional<Double> getAtEpApEnterprise() {
		return atEpApEnterprise;
	}

	public Optional<Double> getUnemploymentType() {
		return unemploymentType;
	}

	public Optional<Double> getUnemploymentApEnterprise() {
		return unemploymentApEnterprise;
	}

	public Optional<Double> getProfesFormType() {
		return profesFormType;
	}

	public Optional<Double> getProfesFormApEnterprise() {
		return profesFormApEnterprise;
	}

	public Optional<Double> getFogasaType() {
		return fogasaType;
	}

	public Optional<Double> getFogasaApEnterprise() {
		return fogasaApEnterprise;
	}

	public Optional<Double> getForceMajeureBase() {
		return forceMajeureBase;
	}

	public Optional<Double> getForceMajeureType() {
		return forceMajeureType;
	}

	public Optional<Double> getForceMajeureApEnterprise() {
		return forceMajeureApEnterprise;
	}

	public Optional<Double> getNoStructBase() {
		return noStructBase;
	}

	public Optional<Double> getNoStructType() {
		return noStructType;
	}

	public Optional<Double> getNoStructApEnterprise() {
		return noStructApEnterprise;
	}

	public Optional<Double> getIrpfEsp() {
		return irpfEsp;
	}

	public Optional<Double> getIrpfRetribDiner() {
		return irpfRetribDiner;
	}

	public Optional<Double> getTotal() {
		return total;
	}

	public static class ContingencyBasesBuilder {

		private Optional<Double> monthlyAmount;
		private Optional<Double> extraProrationAmount;

		private Optional<Double> commonContBase;
		private Optional<Double> commonContType;
		private Optional<Double> commonContApEnterprise;

		private Optional<Double> professionalContBase;

		private Optional<Double> atEpType;
		private Optional<Double> atEpApEnterprise;

		private Optional<Double> unemploymentType;
		private Optional<Double> unemploymentApEnterprise;

		private Optional<Double> profesFormType;
		private Optional<Double> profesFormApEnterprise;

		private Optional<Double> fogasaType;
		private Optional<Double> fogasaApEnterprise;

		private Optional<Double> forceMajeureBase;
		private Optional<Double> forceMajeureType;
		private Optional<Double> forceMajeureApEnterprise;

		private Optional<Double> noStructBase;
		private Optional<Double> noStructType;
		private Optional<Double> noStructApEnterprise;

		private Optional<Double> irpfEsp;
		private Optional<Double> irpfRetribDiner;
		private Optional<Double> total;

		public ContingencyBasesBuilder setMonthlyAmount(Optional<Double> monthly_amount) {
			this.monthlyAmount = monthly_amount;
			return this;
		}

		public ContingencyBasesBuilder setExtraProrationAmount(Optional<Double> extraProrationAmount) {
			this.extraProrationAmount = extraProrationAmount;
			return this;
		}

		public ContingencyBasesBuilder setCommonContBase(Optional<Double> commonContBase) {
			this.commonContBase = commonContBase;
			return this;
		}

		public ContingencyBasesBuilder setCommonContType(Optional<Double> commonContType) {
			this.commonContType = commonContType;
			return this;
		}

		public ContingencyBasesBuilder setCommonContApEnterprise(Optional<Double> commonContApEnterprise) {
			this.commonContApEnterprise = commonContApEnterprise;
			return this;
		}

		public ContingencyBasesBuilder setProfessionalContBase(Optional<Double> professionalContBase) {
			this.professionalContBase = professionalContBase;
			return this;
		}

		public ContingencyBasesBuilder setAtEpType(Optional<Double> atEpType) {
			this.atEpType = atEpType;
			return this;
		}

		public ContingencyBasesBuilder setAtEpApEnterprise(Optional<Double> atEpApEnterprise) {
			this.atEpApEnterprise = atEpApEnterprise;
			return this;
		}

		public ContingencyBasesBuilder setUnemploymentType(Optional<Double> unemploymentType) {
			this.unemploymentType = unemploymentType;
			return this;
		}

		public ContingencyBasesBuilder setUnemploymentApEnterprise(Optional<Double> unemploymentApEnterprise) {
			this.unemploymentApEnterprise = unemploymentApEnterprise;
			return this;
		}

		public ContingencyBasesBuilder setProfesFormType(Optional<Double> profesFormType) {
			this.profesFormType = profesFormType;
			return this;
		}

		public ContingencyBasesBuilder setProfesFormApEnterprise(Optional<Double> profesFormApEnterprise) {
			this.profesFormApEnterprise = profesFormApEnterprise;
			return this;
		}

		public ContingencyBasesBuilder setFogasaType(Optional<Double> fogasaType) {
			this.fogasaType = fogasaType;
			return this;
		}

		public ContingencyBasesBuilder setFogasaApEnterprise(Optional<Double> fogasaApEnterprise) {
			this.fogasaApEnterprise = fogasaApEnterprise;
			return this;
		}

		public ContingencyBasesBuilder setForceMajeureBase(Optional<Double> forceMajeureBase) {
			this.forceMajeureBase = forceMajeureBase;
			return this;
		}

		public ContingencyBasesBuilder setForceMajeureType(Optional<Double> forceMajeureType) {
			this.forceMajeureType = forceMajeureType;
			return this;
		}

		public ContingencyBasesBuilder setForceMajeureApEnterprise(Optional<Double> forceMajeureApEnterprise) {
			this.forceMajeureApEnterprise = forceMajeureApEnterprise;
			return this;
		}

		public ContingencyBasesBuilder setNoStructBase(Optional<Double> noStructBase) {
			this.noStructBase = noStructBase;
			return this;
		}

		public ContingencyBasesBuilder setNoStructType(Optional<Double> noStructType) {
			this.noStructType = noStructType;
			return this;
		}

		public ContingencyBasesBuilder setNoStructApEnterprise(Optional<Double> noStructApEnterprise) {
			this.noStructApEnterprise = noStructApEnterprise;
			return this;
		}

		public ContingencyBasesBuilder setIrpfEsp(Optional<Double> irpfEsp) {
			this.irpfEsp = irpfEsp;
			return this;
		}

		public ContingencyBasesBuilder setIrpfRetribDiner(Optional<Double> irpfRetribDiner) {
			this.irpfRetribDiner = irpfRetribDiner;
			return this;
		}

		public ContingencyBasesBuilder setTotal(Optional<Double> total) {
			this.total = total;
			return this;
		}

		public ContingencyBases build() {

			ContingencyBases c = new ContingencyBases();

			c.monthlyAmount			   = this.monthlyAmount;
			c.extraProrationAmount	   = this.extraProrationAmount;
			c.commonContBase		   = this.commonContBase;
			c.commonContType		   = this.commonContType;
			c.commonContApEnterprise   = this.commonContApEnterprise;
			c.professionalContBase	   = this.professionalContBase;
			c.atEpType				   = this.atEpType;
			c.atEpApEnterprise		   = this.atEpApEnterprise;
			c.unemploymentType		   = this.unemploymentType;
			c.unemploymentApEnterprise = this.unemploymentApEnterprise;
			c.profesFormType		   = this.profesFormType;
			c.profesFormApEnterprise   = this.profesFormApEnterprise;
			c.fogasaType			   = this.fogasaType;
			c.fogasaApEnterprise	   = this.fogasaApEnterprise;
			c.forceMajeureBase		   = this.forceMajeureBase;
			c.forceMajeureType		   = this.forceMajeureType;
			c.forceMajeureApEnterprise = this.forceMajeureApEnterprise;
			c.noStructBase			   = this.noStructBase;
			c.noStructApEnterprise	   = this.noStructApEnterprise;
			c.noStructType			   = this.noStructType;
			c.irpfEsp				   = this.irpfEsp;
			c.irpfRetribDiner		   = this.irpfRetribDiner;
			c.total					   = this.total;

			return c;
		}

	}
}
