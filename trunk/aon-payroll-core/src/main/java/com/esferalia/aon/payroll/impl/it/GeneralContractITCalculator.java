package com.esferalia.aon.payroll.impl.it;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.core.util.DateUtils;
import com.esferalia.aon.payroll.core.ISalary;
import com.esferalia.aon.payroll.core.calc.ISalaryDAO;
import com.esferalia.aon.payroll.core.calc.SalaryDAOFactory;
import com.esferalia.aon.payroll.core.enumeration.ContingencyType;
import com.esferalia.aon.payroll.core.enumeration.ContractType;
import com.esferalia.aon.payroll.core.enumeration.SalaryType;
import com.esferalia.aon.payroll.core.it.ITemporalDisability;
import com.esferalia.aon.payroll.core.it.ITemporaryDisabilityCalculator;
import com.esferalia.aon.payroll.impl.calc.SalaryParams;

public class GeneralContractITCalculator implements ITemporaryDisabilityCalculator{
	
	@Override
	public boolean accept(ITemporalDisability td) {
		ContractType ct = td.getEmployee().getContractType();
		return (ct == ContractType.GENERAL_TYPE) &&
			(td.getContingencyType() == ContingencyType.COMMON_DISEASE || 
			td.getContingencyType() == ContingencyType.MATERNITY);
	}

	@Override
	public Double calculatePrevPeriodBaseSalary(ITemporalDisability td) {
		SalaryParams params = new SalaryParams();
		params.setEmployee(td.getEmployee());
		params.setMonth( DateUtils.getMonth( td.getStartDate() ));
		params.setYear( DateUtils.getYear( td.getStartDate() ));
		params.setType(SalaryType.STANDARD );
		ISalaryDAO salaryDAO = SalaryDAOFactory.getInstance().getSalaryDAO();
		ISalary salary = salaryDAO.getSalary(params);
		
		if (salary == null) {
			params = new SalaryParams();
			params.setEmployee(td.getEmployee());
			params.setMonth( DateUtils.getMonth( td.getStartDate() ));
			params.setYear( DateUtils.getYear( td.getStartDate() ));
			params.setDueDate(DateUtils.add(td.getStartDate(), -1));
			//salary = calculateSalary(params);
		}
		return null;
	}

	@Override
	public Double getDailyAccidentBase(ITemporalDisability td) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getDailyAssistance60(ITemporalDisability td) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getDailyAssistance75(ITemporalDisability td) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getDailyCommonContingencyBase(ITemporalDisability td) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getDailyRegulatoryBase(ITemporalDisability td) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getPrevDaysCount(ITemporalDisability td) {
		// TODO Auto-generated method stub
		return null;
	}

}
