package com.esferalia.aon.payroll.impl.it;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.core.util.DateUtils;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.core.ITrabajo;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.core.it.IParteITCalculator;
import com.esferalia.aon.payroll.core.it.ParteITCalculatorFactory;
import com.esferalia.aon.payroll.core.nomina.CalculatorException;

public class ParteITAccidentesCalculator extends ParteITCalculator implements IParteITCalculator {

	static {
		ParteITCalculatorFactory.register( new ParteITAccidentesCalculator());
	}
	
	@Override
	public boolean accept(IParteIT td,ITrabajo trabajo) {
		return (td.getTipoContingencia() == TipoContingencia.ACCIDENTE_LABORAL);
	}

	@Override
	public Double getBaseRetribucionPeriodoAnterior(IParteIT it)
			throws CalculatorException {
		INomina nomina = getNominaAnterior(it);
		Double baseAccidentesTrabajoSinHorasExtras = nomina.getBaseAccidentesTrabajoSinHorasExtras();
		INomina[] nominas = getNominasAnteriores(it,12);
        double baseHorasExtrasEstruc = 0.0;
        double baseHorasExtrasNoEstruc = 0.0;
		for (INomina nom : nominas) {
			if (nom != null) {
				baseHorasExtrasEstruc += nom.getBaseHorasExtrasEstructurales();
				baseHorasExtrasNoEstruc += nom.getBaseHorasExtrasNoEstructurales();
			}
		}
		double baseHorasExtras = baseHorasExtrasEstruc + baseHorasExtrasNoEstruc;
		long dias = DateUtils.getDaysBetweenDates(it.getEmpleado().getFechaInicio(), it.getFechaBaja() );
		int meses = 0;
		if (dias < 30) {
			meses = 1;
		} else if (dias < 365) {
			meses = (int) (dias / 30);
			if ( meses > 12 ){
				meses = 12;	
			}
		} else {
			meses = 12;
			dias = 365; 
		}
		
		if (it.getProrrateoCotizacion() == Periodicidad.MENSUAL) {
			baseHorasExtras = CommonUtil.round(baseHorasExtras / meses);
		}
		if (it.getProrrateoCotizacion() == Periodicidad.DIARIO) {
			baseHorasExtras = CommonUtil.round(baseHorasExtras / dias);
			baseHorasExtras = CommonUtil.round( baseHorasExtras * nomina.getDiasNomina() );
		}
		// TODO Comprobar TOPES.
		return (baseAccidentesTrabajoSinHorasExtras + baseHorasExtras);
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
	public Double getBaseDiariaContingenciasComunes(IParteIT it) throws CalculatorException {
		INomina nomina = getNominaAnterior(it);
		return (nomina.getBaseContingenciasGenerales() /  it.getDiasPeriodoAnterior());
	}

	@Override
	public Double getBaseDiariaAccidentesTrabajo(IParteIT it) throws CalculatorException {
		return it.getBaseRetribucionPeriodoAnterior() / it.getDiasPeriodoAnterior();
	}



}
