package com.code.aon.calculator;

import com.code.aon.calculator.enumeration.ContractType;

public class CompanyInfo {

	private ContractType contractType;

	public ContractType getContractType() {
		return contractType;
	}

	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}

    public double getCommonContingenciesPercent() throws CalculatorException {
        return 23.60;
    }

	public double getUnemploymentPercent() throws CalculatorException {
		if (contractType == ContractType.GENERAL_TYPE) {
			return 5.75;
		} else if (contractType == ContractType.TEMPORARY_FULL_TIME) {
			return 6.70;
		} else {
			return 7.70;
		}
	}

	public double getWageGuaranteePercent() throws CalculatorException {
		return 0.20;
	}

	public double getOccupationalTrainingPercent() throws CalculatorException {
		return 0.60;
	}

    public double getAccidentAndDiseasePercent() throws CalculatorException {
        return 1.80;
    }

}
