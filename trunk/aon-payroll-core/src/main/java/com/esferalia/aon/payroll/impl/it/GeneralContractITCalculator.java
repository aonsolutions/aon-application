package com.esferalia.aon.payroll.impl.it;

import com.esferalia.aon.core.util.DateUtils;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.core.calc.INominaDAO;
import com.esferalia.aon.payroll.core.calc.NominaDAOFactory;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.enumeration.TipoContrato;
import com.esferalia.aon.payroll.core.enumeration.TipoNomina;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.core.it.IParteITCalculator;
import com.esferalia.aon.payroll.impl.calc.NominaParams;

public class GeneralContractITCalculator implements IParteITCalculator{
	
	@Override
	public boolean accept(IParteIT td) {
		TipoContrato ct = td.getEmpleado().getTipoContrato();
		return (ct == TipoContrato.GENERAL) &&
			(td.getTipoContingencia() == TipoContingencia.ENFERMEDAD_COMUN || 
			td.getTipoContingencia() == TipoContingencia.MATERNIDAD);
	}

	@Override
	public Double calculateBaseRetribucionPeriodoAnterior(IParteIT td) {
		NominaParams params = new NominaParams();
		params.setEmpleado(td.getEmpleado());
		params.setMes( DateUtils.getMonth( td.getFechaBaja() ));
		params.setYear( DateUtils.getYear( td.getFechaBaja() ));
		params.setTipo(TipoNomina.NORMAL );
		INominaDAO salaryDAO = NominaDAOFactory.getInstance().getNominaDAO();
		INomina salary = salaryDAO.getNomina(params);
		
		if (salary == null) {
			params = new NominaParams();
			params.setEmpleado(td.getEmpleado());
			params.setMes( DateUtils.getMonth( td.getFechaBaja() ));
			params.setYear( DateUtils.getYear( td.getFechaBaja() ));
			params.setFechaTope(DateUtils.add(td.getFechaBaja(), -1));
			//salary = calculateSalary(params);
		}
		return null;
	}

	@Override
	public Double getBaseDiariaAccidentesTrabajo(IParteIT td) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getPrestacionDiaria60(IParteIT td) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getPrestacionDiaria75(IParteIT td) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getBaseDiariaContingenciasComunes(IParteIT td) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getBaseReguladoraDiaria(IParteIT td) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getDiasPeriodoAnterior(IParteIT td) {
		// TODO Auto-generated method stub
		return null;
	}

}
