package com.esferalia.aon.payroll.impl;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.core.util.DateUtils;
import com.esferalia.aon.payroll.core.calc.ISalaryCalculator;
import com.esferalia.aon.payroll.core.enumeration.ContingencyType;
import com.esferalia.aon.payroll.core.enumeration.ContributionAccount;
import com.esferalia.aon.payroll.core.it.ITemporalDisability;
import com.esferalia.aon.payroll.core.it.ITemporaryDisabilityCalculator;
import com.esferalia.aon.payroll.core.it.TemporaryDisabilityCalculatorFactory;

public abstract class TemporalDisabilityAbs implements ITemporalDisability {

	private static final long serialVersionUID = -2580689973452415814L;

	public int validate() {
		// El empleado es un dato requerido.
		if (getEmployee() == null) {
			return 1;
		}

		// El empleado debe estar activo.
		if (getEmployee().getStopDate() != null) {
			return 2;
		}

		// El empresa del empleado debe estar activa.
		if (!getEmployee().getCompany().isActive()) {
			return 3;
		}

		// La fecha de la baja es un dato requerido.
		if (getStartDate() != null) {
			return 4;
		}

		// Si se está grabando una baja, uno de los datos, n- colegiado o CIAS
		// es requerido.
		if (StringUtils.isBlank(getStartMedicalAreaId())
				&& StringUtils.isBlank(getStartMedicalLicenseNumber())) {
			return 5;
		}

		// Si se está grabando una alta, uno de los datos, n- colegiado o CIAS
		// es requerido.
		if (getStopDate() != null
				&& StringUtils.isBlank(getStopMedicalAreaId())
				&& StringUtils.isBlank(getStopMedicalLicenseNumber())) {
			return 6;
		}

		int valid = DateUtils.validateRange(getStartDate(), getStopDate(), getEmployee().getStartDate(), getEmployee().getStopDate());
		if (valid > 0) {
			if (valid == 1) return 4; //La fecha de la baja es un dato requerido.
			if (valid == 2) return 7; //La fecha de Alta es menor a la de Baja.
			if (valid == 3) return 8; //La fecha de Baja no está en el rango de fechas del empleado.
			if (valid == 4) return 9; //La fecha de Alta no está en el rango de fechas del empleado.
		}
		
		
		//No se genera parte de I.T. por tratarse de un Alto Cargo.
		if (getEmployee().getContributionAccount() == ContributionAccount.EXECUTIVE) {
			return 10;
		}
		
		//No se genera Parte de I.T. por Maternidad por tratarse de un Trabajador Mayor de 65 años y más de 35 años Cotizados.
		if (getEmployee().isOlder65() && getContingencyType() == ContingencyType.MATERNITY) {
			return 11;
		}
		
		//El el caso de recaída, debe indicar la primera I.T.
		if (isRelapase() && getFirstTemporalDisability() == null) {
			return 12;
		}
		
		return 0;
	}
	
	public void calculate() {
		TemporaryDisabilityCalculatorFactory factory = TemporaryDisabilityCalculatorFactory.getInstance();
		ITemporaryDisabilityCalculator calculator = factory.getTemporaryDisabilityCalculator( this );
		setPrevPeriodBaseSalary( calculator.calculatePrevPeriodBaseSalary(this) );
		setPrevDaysCount(calculator.getPrevDaysCount(this) );
		setDailyRegulatoryBase( calculator.getDailyRegulatoryBase(this) );
		setDailyCommonContingencyBase(calculator.getDailyCommonContingencyBase(this));
		setDailyAccidentBase(calculator.getDailyAccidentBase(this) );
		setDailyAssistance60(calculator.getDailyAssistance60(this));
		setDailyAssistance75(calculator.getDailyAssistance75(this));
	}

}
