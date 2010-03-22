package com.esferalia.aon.payroll.impl.it;

import com.esferalia.aon.core.util.DateUtils;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.core.calc.CalculatorException;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.enumeration.TipoContrato;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.core.it.IParteITCalculator;

public class ParteITTiempoCompletoCalculator extends ParteITCalculator implements IParteITCalculator {

	@Override
	public boolean accept(IParteIT td) {
		TipoContrato ct = td.getEmpleado().getTipoContrato();
		return (ct == TipoContrato.TIEMPO_COMPLETO)
				&& (td.getTipoContingencia() == TipoContingencia.ENFERMEDAD_COMUN || td
						.getTipoContingencia() == TipoContingencia.MATERNIDAD);
	}

	@Override
	public Double calculateBaseRetribucionPeriodoAnterior(IParteIT it)
			throws CalculatorException {
		INomina nomina = getNominaAnterior(it);
		return nomina.getBaseContingenciasGenerales();
	}

	@Override
	public Integer getDiasPeriodoAnterior(IParteIT it)
			throws CalculatorException {
		INomina nomina = getNominaAnterior(it);
		int diasNomina = nomina.getDiasNomina();
		if (it.getProrrateoCotizacion() == Periodicidad.MENSUAL) {
			if (diasNomina == 31 || diasNomina == DateUtils.getMonthDays(nomina.getMes(),nomina.getYear())) {
				return 30;
			}
		}
		return diasNomina;
	}


	@Override
	public Double getBaseDiariaContingenciasComunes(IParteIT it) {
		// No se redondea a posta.
		return it.getBaseRetribucionPeriodoAnterior() / it.getDiasPeriodoAnterior();
	}

	@Override
	public Double getBaseDiariaAccidentesTrabajo(IParteIT it) throws CalculatorException {
		INomina nomina = getNominaAnterior(it);
		return nomina.getBaseAccidentesTrabajo() / it.getDiasPeriodoAnterior();
	}

}
