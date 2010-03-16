package com.esferalia.aon.payroll.impl.it;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.core.util.DateUtils;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.core.calc.CalculatorException;
import com.esferalia.aon.payroll.core.calc.INominaDAO;
import com.esferalia.aon.payroll.core.calc.NominaDAOFactory;
import com.esferalia.aon.payroll.core.enumeration.TipoNomina;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.impl.calc.NominaParams;

public class ParteITCalculator {

	protected INomina getNominaAnterior(IParteIT it) throws CalculatorException {
		try {
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
			INominaDAO nominaDAO = NominaDAOFactory.getInstance()
					.getNominaDAO();
			INomina nomina = nominaDAO.getNomina(params);
			if (nomina == null) {
				throw new CalculatorException(
						"El trabajador no tiene calculada la nómina anterior");
			}
			return nomina;
		} catch (PayrollException e) {
			throw new CalculatorException(e);
		}
	}

	protected INomina[] getNominasAnteriores(IParteIT it, int count)
			throws CalculatorException {
		try {
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
		} catch (PayrollException e) {
			throw new CalculatorException(e);
		}
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

}
