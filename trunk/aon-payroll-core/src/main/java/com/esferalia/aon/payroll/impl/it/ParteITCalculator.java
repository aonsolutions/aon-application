package com.esferalia.aon.payroll.impl.it;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.core.util.DateUtils;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoNomina;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.core.nomina.INominaDAO;
import com.esferalia.aon.payroll.core.nomina.NominaDAOFactory;
import com.esferalia.aon.payroll.core.nomina.NominaParams;

public class ParteITCalculator {

	protected INomina getNominaAnterior(IParteIT it) throws PayrollException {
		int month = DateUtils.getMonth(it.getFechaBaja());
		int year = DateUtils.getYear(it.getFechaBaja());
		if (month == 0) {
			month = 11;
			year--;
		}
		NominaParams params = new NominaParams();
		params.setEmpleado(it.getEmpleado());
		params.setMes(month);
		params.setYear(year);
		params.setTipo(TipoNomina.NORMAL);
		NominaDAOFactory f = NominaDAOFactory.getInstance();
		INominaDAO nominaDAO = f.getNominaDAO();
		INomina nomina = nominaDAO.getNomina(params);
		if (nomina == null) {
			throw new PayrollException(
					"El trabajador no tiene calculada la nómina anterior");
		}
		return nomina;
	}

	protected INomina[] getNominasAnteriores(IParteIT it, int count)
			throws PayrollException {
		int year = DateUtils.getYear(it.getFechaBaja());
		INomina[] nominas = new INomina[12];
		int month = DateUtils.getMonth(it.getFechaBaja());
		for (int i = 0; i < count; ++i) {
			if (month == 0) {
				month = 11;
				year--;
			}
			NominaParams params = new NominaParams();
			params.setEmpleado(it.getEmpleado());
			params.setMes(month);
			params.setYear(year);
			params.setTipo(TipoNomina.NORMAL);
			INominaDAO nominaDAO = NominaDAOFactory.getInstance()
					.getNominaDAO();
			INomina nomina = nominaDAO.getNomina(params);
			if (nomina != null) {
				nominas[i] = nomina;
			}
			month--;
		}
		return nominas;
	}

	public Double getBaseReguladoraDiaria(IParteIT it) {
		return (it.getBaseRetribucionPeriodoAnterior() / it
				.getDiasPeriodoAnterior());
	}

	public Double getPrestacionDiaria60(IParteIT it) {
		return CommonUtil.round((it.getBaseReguladoraDiaria() * 60) / 100);
	}

	public Double getPrestacionDiaria75(IParteIT it) {
		return CommonUtil.round((it.getBaseReguladoraDiaria() * 75) / 100);
	}
	
	public Periodicidad getProrrateoCotizacion(IParteIT it) throws PayrollException{
		return it.getProrrateoCotizacion();
	}

}
