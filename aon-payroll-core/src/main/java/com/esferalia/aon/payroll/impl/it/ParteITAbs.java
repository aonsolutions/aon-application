package com.esferalia.aon.payroll.impl.it;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.core.util.DateUtils;
import com.esferalia.aon.payroll.core.ITrabajo;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.core.it.IParteITCalculator;
import com.esferalia.aon.payroll.core.it.ParteITCalculatorFactory;
import com.esferalia.aon.payroll.core.nomina.CalculatorException;

public abstract class ParteITAbs implements IParteIT {

	private static final long serialVersionUID = -2580689973452415814L;

	public int validate() {
		// El empleado es un dato requerido.
		if (getEmpleado() == null) {
			return 1;
		}

		// El empleado debe estar activo.
		if (getEmpleado().getFechaFin() != null) {
			return 2;
		}

		// El empresa del empleado debe estar activa.
		if (!getEmpleado().getEmpresa().isActive()) {
			return 3;
		}

		// La fecha de la baja es un dato requerido.
		if (getFechaBaja() == null) {
			return 4;
		}

		// Si se está grabando una baja, uno de los datos, n- colegiado o CIAS
		// es requerido.
		if (StringUtils.isBlank(getCiasBaja())
				&& StringUtils.isBlank(getNumeroColegiadoBaja())) {
			return 5;
		}

		// Si se está grabando una alta, uno de los datos, n- colegiado o CIAS
		// es requerido.
		if (getFechaAlta() != null
				&& StringUtils.isBlank(getCiasAlta())
				&& StringUtils.isBlank(getNumeroColegiadoAlta())) {
			return 6;
		}

		int valid = DateUtils.validateRange(getFechaBaja(), getFechaAlta(), getEmpleado().getFechaInicio(), getEmpleado().getFechaFin());
		if (valid > 0) {
			if (valid == 1) return 4; //La fecha de la baja es un dato requerido.
			if (valid == 2) return 7; //La fecha de Alta es menor a la de Baja.
			if (valid == 3) return 8; //La fecha de Baja no está en el rango de fechas del empleado.
			if (valid == 4) return 9; //La fecha de Alta no está en el rango de fechas del empleado.
		}
		
		
		//No se genera parte de I.T. por tratarse de un Alto Cargo.
		if (getEmpleado().getCuentaCotizacion() == CuentaCotizacion.ALTO_CARGO) {
			return 10;
		}
		
		//No se genera Parte de I.T. por Maternidad por tratarse de un Trabajador Mayor de 65 años y más de 35 años Cotizados.
		if (getEmpleado().isMayor65() && getTipoContingencia() == TipoContingencia.MATERNIDAD) {
			return 11;
		}
		
		//El el caso de recaída, debe indicar la primera I.T.
		if (isRecaida() && getParteITRecaida() == null) {
			return 12;
		}
		
		return 0;
	}
	
	public void calculate(ITrabajo trabajo) throws CalculatorException {
		ParteITCalculatorFactory factory = ParteITCalculatorFactory.getInstance();
		IParteITCalculator calculator = factory.getParteITCalculator( this, trabajo );
		setBaseRetribucionPeriodoAnterior( calculator.getBaseRetribucionPeriodoAnterior(this) );
		setDiasPeriodoAnterior(calculator.getDiasPeriodoAnterior(this) );
		setBaseReguladoraDiaria( calculator.getBaseReguladoraDiaria(this) );
		setBaseDiariaContingenciasComunes(calculator.getBaseDiariaContingenciasComunes(this));
		setBaseDiariaAccidentesTrabajo(calculator.getBaseDiariaAccidentesTrabajo(this) );
		setPrestacionDiaria60(calculator.getPrestacionDiaria60(this));
		setPrestacionDiaria75(calculator.getPrestacionDiaria75(this));
	}

}
